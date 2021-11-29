import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { MoreInfoDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/more-info-dialog/more-info-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { Address } from '../../../interfaces/contact-interface';
import { buildAddressLine2 } from '../../../shared/build-address-line-2';
import {
  addressColumnsForDisplay,
  addressColumnsForForm,
  foreignAddressColumns,
} from '../../../shared/columns';
import { formatAddressForDisplay } from '../../../shared/format-address';
import { CreateAddressDialogComponent } from '../../create/create-address-dialog/create-address-dialog.component';
import { ContactAddressesService } from '../../edit/components/address/services/contact-addresses.service';

@Component({
  selector: 'app-address-table',
  templateUrl:
    '../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../shared/components/templates/code-table/code-table.template.scss',
    './address-table.component.scss',
  ],
  providers: [ContactAddressesService],
})
export class AddressTableComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  @Output() insertEvent: EventEmitter<any> = new EventEmitter();
  @Output() editEvent: EventEmitter<any> = new EventEmitter();
  @Output() deleteEvent: EventEmitter<any> = new EventEmitter();
  @Output() reloadAddresses = new EventEmitter();
  @Input() containerStyles = {};
  @Input() reloadAddressesData;
  @Input() contactId;
  @Input() hideActions;
  @Input() enableMoreInfo;
  @Input() hideInsert = true;
  @Input() showRtnMail = false;
  @Input() showNotes = false;
  @Input() contactStatus = null;

  constructor(
    public service: ContactAddressesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Columns that'll appear in the address form for US addresses
  public addressColumnsForForm: ColumnDefinitionInterface[] = [
    ...addressColumnsForForm,
  ];

  // Columns that'll appear when creating an foreign address
  public foreignAddressColumns: ColumnDefinitionInterface[] = [
    ...foreignAddressColumns,
  ];

  public columns: ColumnDefinitionInterface[] = [...addressColumnsForDisplay];

  public title = '';
  public searchable = false;
  public reloadAddressesSub$: Subscription;
  public currentAddressId;
  public isInMain = false;

  protected initFunction(): void {
    this.dataMessage = 'No data found';
    if (this.showRtnMail) {
      this.columns.splice(1, 0, {
        columnId: 'rtnMail',
        title: 'Return Mail',
        type: FormFieldTypeEnum.Checkbox,
        noSort: true,
        width: 100,
        displayInEdit: false,
        displayInInsert: false,
      });
    }

    if (this.showNotes) {
      this.addressColumnsForForm = [
        ...addressColumnsForForm,
        {
          columnId: 'modReason',
          title: 'Notes',
          type: FormFieldTypeEnum.TextArea,
          validators: [Validators.maxLength(4000)],
        },
      ];

      this.foreignAddressColumns = [
        ...foreignAddressColumns,
        {
          columnId: 'modReason',
          title: 'Notes',
          type: FormFieldTypeEnum.TextArea,
          validators: [Validators.maxLength(4000)],
        },
      ];
    }
  }

  public ngAfterViewInit() {
    setTimeout(() => {
      this.idArray = [this.contactId];
      this._get();

      if (this.reloadAddressesData) {
        this.reloadAddressesSub$ = this.reloadAddressesData.subscribe(() => {
          this._get();
        });
      }
    });
  }

  public ngOnDestroy() {
    if (this.contactId) {
      this.contactId = null;
    }
    if (this.reloadAddressesSub$) {
      this.reloadAddressesSub$.unsubscribe();
    }
  }

  protected _getHelperFunction(data: any) {
    if (data?.get?.results?.length) {
      const addresses = [...data.get.results];
      if (addresses[0].primaryMail === 'Y') {
        addresses[0].hideDelete = true;
      }
      return {
        ...data.get,
        results: addresses.map(formatAddressForDisplay),
      };
    }
    return { results: [] };
  }

  private clearCityAndStateValues() {
    this.getColumn('cityAndState', this.addressColumnsForForm).selectArr = [];
  }

  private getForeignAddressFormat(address: Address) {
    return {
      ...address,
      cityName: null,
      cityId: null,
      stateName: null,
      stateCode: null,
      zipCode: null,
      zipCodeId: null,
      addressLine2: buildAddressLine2(address),
    };
  }

  private getAddressDto(address: Address) {
    return {
      addressLine1: address.addressLine1,
      addressLine2: address.addressLine2,
      addressLine3: address.addressLine3,
      cityId: address.cityId,
      stateCode: address.stateCode,
      zipCodeId: address.zipCodeId,
      foreignPostal: address.foreignPostal,
      primaryMail: address.primaryMail,
      foreignAddress: address.foreignAddress,
      unresolvedFlag: address.unresolvedFlag,
      pl4: address.pl4,
      modReason: address.modReason,
    };
  }

  private getColumn(
    columnId: string,
    columns: ColumnDefinitionInterface[] = this.columns
  ): ColumnDefinitionInterface {
    let index: number;
    for (let i = 0; i < columns.length; i++) {
      if (columns[i].columnId === columnId) {
        index = i;
      }
    }
    return columns[index];
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(CreateAddressDialogComponent, {
      width: null,
      data: {
        title: 'Address',
        mode: DataManagementDialogModes.Insert,
        values: {
          addressColumns: this.addressColumnsForForm,
          foreignAddressColumns: this.foreignAddressColumns,
          isPrimMail: true,
          canChangeIsPrimMail: !!this.rows?.length,
          contactStatus: this.contactStatus,
        },
      },
    });
    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result: Address) => {
      this.clearCityAndStateValues();
      if (result !== null && result !== undefined) {
        if (result.isForeign) {
          // Switch city, state and zipCodce to addressLine2
          result = this.getForeignAddressFormat(result);
        }
        // Send The POST request
        this._insert(this.getAddressDto(result));
      } else {
        this.firstInsert.focus();
      }
    });
  }

  protected _displayEditDialog(addressData: Address): void {
    const addressesColumns = [...this.addressColumnsForForm];
    const dialogRef = this.dialog.open(CreateAddressDialogComponent, {
      width: null,
      data: {
        title: 'Address',
        mode: DataManagementDialogModes.Update,
        values: {
          ...addressData,
          addressColumns: addressesColumns,
          foreignAddressColumns: this.foreignAddressColumns,
          canChangeIsPrimMail: !addressData.isPrimMail,
          showRtnMail: true,
          contactStatus: this.contactStatus,
        },
      },
    });
    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      this.clearCityAndStateValues();
      if (result !== null && result !== undefined) {
        if (!result.addressLine2 && result.isForeign) {
          // Switch city, state and zipCodce to addressLine2
          result = this.getForeignAddressFormat(result);
        }

        if (addressData?.addressId) {
          // Check the addressId exists
          this.currentAddressId = addressData?.addressId;
          // send the PUT request
          this._update(this.getAddressDto(result));
        }
      }
    });
  }

  protected _displayMoreInfoDialog(row: number): void {
    const columns = [
      this._getColumn('createdByValue'),
      this._getColumn('createdDate'),
      this._getColumn('modReason'),
    ];

    // Open the dialog
    this.dialog.open(MoreInfoDialogComponent, {
      width: '700px',
      data: {
        title: 'Address',
        columns,
        values: {
          ...this.data.results[row],
        },
      },
    });
  }

  // Handler moreInfoEvent
  public moreInfoHandler(row: number): void {
    this._displayMoreInfoDialog(row);
  }

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    this.service
      .update(updatedRow, ...this.idArray, this.currentAddressId)
      .subscribe(
        (dto) => {
          let messages = ['Record successfully updated.'];
          if (!!dto.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.reloadAddresses.emit(null);
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot update record. ';
          message += errorBody.userMessage || ErrorMessageEnum.PUT;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayEditDialog(updatedRow);
        }
      );
  }

  /*
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    if (this.rows[row].isPrimMail) {
      this.snackBar.open('Cannot delete a Prim Mail address.');
      return;
    }

    this.currentAddressId = this.rows[row]?.addressId;

    this.service.delete(...this.idArray, this.currentAddressId).subscribe(
      () => {
        this._get();
        this.reloadAddresses.emit(null);
        this.snackBar.open('Record successfully deleted.');
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot delete record. ';
        message += errorBody.userMessage || ErrorMessageEnum.DELETE;
        this.snackBar.open(message);
      }
    );
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.service.insert(newRow, ...this.idArray).subscribe(
      (dto) => {
        let messages = ['Record successfully added.'];
        if (!!dto.messages) {
          messages = [...dto.messages, ...messages];
        }
        this.snackBar.open(messages.join('\n'));
        this.reloadAddresses.emit(null);
        this._get();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot insert new record. ';
        message += errorBody.userMessage || ErrorMessageEnum.POST;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this._displayInsertDialog(newRow);
      }
    );
  }
}
