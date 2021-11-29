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
import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { Observable, ReplaySubject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { Address, Contact } from '../../../../interfaces/contact-interface';
import { ContactStatusService } from '../../../../services/contact-status.service';
import { ContactSuffixService } from '../../../../services/contact-suffix.service';
import { ContactTypesService } from '../../../../services/contact-types.service';
import { ContactsService } from '../../../../services/contacts.service';
import { formatAddressForDisplay } from '../../../../shared/format-address';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './edit-header.component.scss',
  ],
  providers: [
    ContactStatusService,
    ContactTypesService,
    ContactSuffixService,
    ContactsService,
    SessionStorageService,
  ],
})
export class EditHeaderComponent extends DataRowComponent implements OnDestroy {
  @Input() title;
  @Input() removeFieldsPadding = false;
  @Input() reloadData: Observable<any> = null;

  @Output() reloadAddresses = new EventEmitter();
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();

  public error;
  public dialogWidth = '600px';
  public reloadDataSub$: Subscription;

  constructor(
    public contactStatusService: ContactStatusService,
    public contactTypesService: ContactTypesService,
    public contactSuffixService: ContactSuffixService,
    private sessionStorage: SessionStorageService,
    public service: ContactsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
    this.titleService.setTitle(
      `WRIS - Contacts: ${this.route.snapshot.params.id}`
    );
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      fontWeight: 700,
      width: 135,
    },
    {
      columnId: 'lastName',
      title: 'Last Name/Corporation',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [Validators.required, Validators.maxLength(50)],
    },
    {
      columnId: 'firstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [Validators.maxLength(20)],
    },
    {
      columnId: 'middleInitial',
      title: 'MI',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [Validators.maxLength(2)],
    },
    {
      columnId: 'suffix',
      title: 'Suffix',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'name',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 520,
    },
    {
      columnId: 'contactType',
      title: 'Contact Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'contactTypeValue',
      title: 'Contact Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 300,
    },
    {
      columnId: 'contactStatus',
      title: 'Contact Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'contactStatusValue',
      title: 'Contact Status',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 220,
    },
    {
      columnId: 'address',
      title: 'Address',
      type: FormFieldTypeEnum.TextArea,
      displayInEdit: false,
      width: 560,
    },
  ];

  public reportTitle = 'Contact Reports';

  public reports: ReportDefinition[] = [
    {
      title: 'General Abstract',
      reportId: 'WRD2090AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CUST_ID_SEQ = data.contactId;
      },
    },
    {
      title: 'Name Address Corrections',
      reportId: 'WRD3072R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CUST_ID_SEQ = data.contactId;
        report.params.P_USERNAME = this.sessionStorage.username;
      },
    },
    {
      title: 'Return Mail Checklist',
      reportId: 'WRD3071R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CUST_ID_SEQ = data.contactId;
        report.params.P_USERNAME = this.sessionStorage.username;
      },
    },
  ];

  public ngOnDestroy() {
    if (this.reloadDataSub$) {
      this.reloadDataSub$.unsubscribe();
    }
    if (this.dialog) {
      this.dialog.closeAll();
    }
  }

  protected initFunction() {
    if (this.reloadData) {
      this.reloadDataSub$ = this.reloadData.subscribe(() => {
        this._get();
      });
    }
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  private getRealValueFor(fieldName, value: string) {
    const options = this._getColumn(fieldName).selectArr.filter(
      (option) => option.value === value
    );
    if (options[0]) {
      return options[0].name;
    }
  }

  protected _update(updatedRow: any): void {
    if (updatedRow) {
      this.service.update(updatedRow, ...this.idArray).subscribe(
        () => {
          this._get();
          this.reloadAddresses.emit(null);
          this.snackBar.open('Record successfully updated.', null);
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error;
          let message = 'Cannot update record. ';
          message += errorBody.userMessage || ErrorMessageEnum.PUT;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayEditDialog(updatedRow);
        }
      );
    }
  }

  protected _getHelperFunction(data): { [key: string]: any } {
    this.dataEvent.emit(data);
    let address = data.get.addresses.filter(
      (address) => address?.primaryMail === 'Y'
    )[0];

    if (address) {
      const addr = formatAddressForDisplay(address);
      address = addr.completeAddress;
    }

    return {
      ...data.get,
      contactTypeValue: this.getRealValueFor(
        'contactType',
        data.get?.contactType
      ),
      contactStatusValue: this.getRealValueFor(
        'contactStatus',
        data.get?.contactStatus
      ),
      address,
    };
  }

  private getAddressDto(address: Address) {
    return {
      addressId: address.addressId,
      customerId: address.customerId,
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
      // modReason: address.modReason,
    };
  }

  private getRotatedAddresses(forward: boolean, addresses: Address[]) {
    return addresses.slice().map((address) => {
      const addressLine1 = address.addressLine1;
      const addressLine2 = address.addressLine2;
      const addressLine3 = address.addressLine3;

      if (forward) {
        return {
          ...address,
          addressLine1: '***Deceased***',
          addressLine2: addressLine1,
          addressLine3: addressLine2,
        };
      } else {
        return {
          ...address,
          addressLine1: addressLine2 ? addressLine2 : addressLine1,
          addressLine2: addressLine3,
          addressLine3: null,
        };
      }
    });
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: '600px',
      data: {
        title: 'Update Contact Record',
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result: Contact) => {
      if (
        this.data.contactStatus !== 'DEC' &&
        result?.contactStatus === 'DEC'
      ) {
        let canRotateAddresses = true;
        this.data.addresses.forEach((address: Address) => {
          if (address.addressLine3) {
            canRotateAddresses = false;
          }
        });

        if (!canRotateAddresses) {
          this.snackBar.open(
            'For all addresses, remove data from Address Line 3 before Deceased can be selected.'
          );
          this._displayEditDialog(result);
          return;
        } else {
          result.addresses = this.getRotatedAddresses(
            true,
            this.data.addresses
          ).map(this.getAddressDto.bind(this));
          this._update(result);
        }
      } else {
        if (
          this.data.contactStatus === 'DEC' &&
          result?.contactStatus !== 'DEC'
        ) {
          result.addresses = this.getRotatedAddresses(
            false,
            this.data.addresses
          ).map(this.getAddressDto.bind(this));
        }

        this._update(result);
      }
    });
  }

  public getColumn(columnId: string): ColumnDefinitionInterface {
    let index: number;
    for (let i = 0; i < this.columns.length; i++) {
      if (this.columns[i].columnId === columnId) {
        index = i;
      }
    }
    return this.columns[index];
  }

  public populateDropdowns(): void {
    // Populate ContactStatus values
    this.observables.contactStatus = new ReplaySubject(1);
    this.contactStatusService
      .get(this.queryParameters)
      .subscribe((contactStatuses) => {
        this._getColumn('contactStatus').selectArr =
          contactStatuses.results.map(
            (contactStatus: { value: string; description: string }) => ({
              name: contactStatus.description,
              value: contactStatus.value,
            })
          );
        this._getColumn('contactStatus').selectArr.unshift({
          name: '',
          value: '',
        });
        this.observables.contactStatus.next(contactStatuses);
        this.observables.contactStatus.complete();
      });

    // Populate ContactType values
    this.observables.contactTypes = new ReplaySubject(1);
    this.contactTypesService
      .get(this.queryParameters)
      .subscribe((contactTypes) => {
        this._getColumn('contactType').selectArr = contactTypes.results.map(
          (contactType: { value: string; description: string }) => ({
            name: contactType.description,
            value: contactType.value,
          })
        );
        this.observables.contactTypes.next(contactTypes);
        this.observables.contactTypes.complete();
      });

    // Populate Suffix values
    this.observables.contactSuffixArr = new ReplaySubject(1);
    this.contactSuffixService
      .get(this.queryParameters)
      .subscribe((contactSuffixArr) => {
        this._getColumn('suffix').selectArr = contactSuffixArr.results.map(
          (contactSuffix: { value: string; description: string }) => ({
            name: contactSuffix.description,
            value: contactSuffix.value,
          })
        );
        this._getColumn('suffix').selectArr.unshift({ name: '', value: '' });
        this.observables.contactSuffixArr.next(contactSuffixArr);
        this.observables.contactSuffixArr.complete();
      });
  }

  public _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Contact not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }
}
