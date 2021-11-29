import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ConfirmationDialogComponent } from 'src/app/modules/shared/components/dialogs/confirmation-dialog/confirmation-dialog.component';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';
import { Address } from '../../interfaces/contact-interface';
import { ContactStatusService } from '../../services/contact-status.service';
import { ContactSuffixService } from '../../services/contact-suffix.service';
import { ContactTypesService } from '../../services/contact-types.service';
import { buildAddressLine2 } from '../../shared/build-address-line-2';
import {
  addressColumnsForDisplay,
  addressColumnsForForm,
  foreignAddressColumns,
} from '../../shared/columns';
import { sortAddresses } from '../../shared/sort-address';
import { updateAddressesArr } from '../../shared/update-addresses';
import { CreateAddressDialogComponent } from './create-address-dialog/create-address-dialog.component';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss'],
  providers: [ContactStatusService, ContactTypesService, ContactSuffixService],
})
export class CreateComponent
  extends DataManagementDialogComponent
  implements OnInit
{
  constructor(
    public contactStatusService: ContactStatusService,
    public contactTypesService: ContactTypesService,
    public contactSuffixService: ContactSuffixService,
    public dialogRef: MatDialogRef<CreateComponent>,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(dialogRef);
  }

  public addresses: Address[] = [];
  public permissions: PermissionsInterface = {
    canDELETE: true,
    canPUT: true,
    canPOST: false,
    canGET: true,
  };
  public dataSourceForAddress: any = new MatTableDataSource(this.addresses);

  public title = 'Create New Contact';
  public loaded = true;
  public formGroup: FormGroup = new FormGroup({});
  public observables: { [key: string]: ReplaySubject<unknown> } = {};
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public contactStatus;

  // Columns that'll appear in the address form for US addresses
  public addressColumnsForForm: ColumnDefinitionInterface[] = [
    ...addressColumnsForForm,
  ];

  // Columns that'll appear in the address MAIN table
  public addressColumnsForDisplay: ColumnDefinitionInterface[] = [
    ...addressColumnsForDisplay,
  ];

  // Columns that'll appear when creating an foreign address
  public foreignAddressColumns: ColumnDefinitionInterface[] = [
    ...foreignAddressColumns,
  ];

  // Contact columns
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'lastName',
      title: 'Last Name/Corporation',
      type: FormFieldTypeEnum.Input,
      editable: false,
      validators: [Validators.required, Validators.maxLength(50)],
    },
    {
      columnId: 'contactType',
      title: 'Contact Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'addresses',
      title: 'Addresses',
      type: FormFieldTypeEnum.Input,
      list: [...this.addressColumnsForDisplay],
    },
    {
      columnId: 'firstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(20)],
    },
    {
      columnId: 'middleInitial',
      title: 'MI',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(2)],
    },
    {
      columnId: 'suffix',
      title: 'Suffix',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'contactStatus',
      title: 'Contact Status',
      type: FormFieldTypeEnum.Select,
    },
  ];

  @ViewChild('focus', { static: false }) addFocus: MatButton;

  initFunction(): void {
    this.populateDropdowns();
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

  private populateDropdowns(): void {
    // Populate ContactStatus values
    this.observables.contactStatus = new ReplaySubject(1);
    this.contactStatusService
      .get(this.queryParameters)
      .subscribe((contactStatuses) => {
        this.getColumn('contactStatus', this.columns).selectArr =
          contactStatuses.results.map(
            (contactStatus: { value: string; description: string }) => ({
              name: contactStatus.description,
              value: contactStatus.value,
            })
          );
        this.getColumn('contactStatus', this.columns).selectArr.unshift({
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
        this.getColumn('contactType', this.columns).selectArr =
          contactTypes.results.map(
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
        this.getColumn('suffix', this.columns).selectArr =
          contactSuffixArr.results.map(
            (contactSuffix: { value: string; description: string }) => ({
              name: contactSuffix.description,
              value: contactSuffix.value,
            })
          );
        this.getColumn('suffix', this.columns).selectArr.unshift({
          name: '',
          value: '',
        });
        this.observables.contactSuffixArr.next(contactSuffixArr);
        this.observables.contactSuffixArr.complete();
      });
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

  public displayAddressesInsertDialog(): void {
    // Open the insert dialog
    const dialogRef = this.dialog.open(CreateAddressDialogComponent, {
      width: null,
      data: {
        title: 'Address',
        mode: DataManagementDialogModes.Insert,
        values: {
          addressColumns: this.addressColumnsForForm,
          foreignAddressColumns: this.foreignAddressColumns,
          isPrimMail: true,
          canChangeIsPrimMail: !!this.addresses?.length,
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
        this.addresses = updateAddressesArr(-1, result, this.addresses);
        this.reloadAddressesForDisplay();
      } else {
        this.addFocus.focus();
      }
    });
  }

  public displayAddressesEditDialog(addressIndex: number): void {
    // Open the edit dialog
    const dialogRef = this.dialog.open(CreateAddressDialogComponent, {
      width: null,
      data: {
        title: 'Address',
        mode: DataManagementDialogModes.Update,
        values: {
          ...this.addresses[addressIndex],
          addressColumns: this.addressColumnsForForm,
          foreignAddressColumns: this.foreignAddressColumns,
          canChangeIsPrimMail: !this.addresses[addressIndex].isPrimMail,
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
        this.addresses = updateAddressesArr(
          addressIndex,
          result,
          this.addresses
        );
        this.reloadAddressesForDisplay();
      }
    });
  }

  public displayAddressesDeleteDialog(addressIndex: number): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this.addresses.splice(addressIndex, 1);
        this.reloadAddressesForDisplay();
      }
    });
  }

  public updateAddress(index: number, address: Address): void {
    if (address.isPrimMail) {
      this.addresses = this.addresses.map((address) =>
        // Set all the addresses to isPrimMail = FALSE
        ({
          ...address,
          isPrimMail: false,
          primaryMail: 'N',
          primaryMailValue: 'NO',
        })
      );
    }

    // if the index is -1 this means is a new address
    if (index === -1) {
      this.addresses.unshift(address);
    } else {
      this.addresses[index] = { ...address };
    }

    this.addresses = this.addresses.sort(sortAddresses);

    this.reloadAddressesForDisplay();
  }

  public reloadAddressesForDisplay() {
    this.dataSourceForAddress = new MatTableDataSource(this.addresses);
  }

  public goBack(): void {
    void this.router.navigate(['/wris/contacts']);
  }

  public _onChange(event) {
    if (event.fieldName === 'contactStatus') {
      if (
        event.value === 'DEC' &&
        this.addresses.filter((a) => a.addressLine1 !== '***DECEASED***').length
      ) {
        const confirmationDialog = this.dialog.open(
          ConfirmationDialogComponent,
          {
            data: {
              title: 'Warning',
              message:
                'If you mark this contact as DECEASED, the system will replace the data in Address Line 1 with ***DECEASED***. Do you want to continue? ',
              confirmButtonName: 'Continue',
            },
          }
        );

        confirmationDialog.afterClosed().subscribe((confirmation) => {
          if (confirmation === 'confirmed') {
            this.contactStatus = event.value;
            this.addresses.forEach((currentAddress, addressIndex) => {
              const result = {
                ...currentAddress,
                addressLine1: '***DECEASED***',
              };
              this.addresses = updateAddressesArr(
                addressIndex,
                result,
                this.addresses
              );
            });
            this.reloadAddressesForDisplay();
          } else {
            this.formGroup.get('contactStatus').setValue(this.contactStatus);
          }
        });
      } else {
        this.contactStatus = event.value;
      }
    }
  }

  public getContactDto() {
    const contactDto = {
      ...this.formGroup.getRawValue(),
      addresses: this.addresses.map((address) => ({
        addressLine1: address.addressLine1,
        addressLine2: address.addressLine2,
        addressLine3: address.addressLine3,
        cityId: address.cityId,
        stateCode: address.stateCode,
        zipCodeId: address.zipCodeId,
        foreignPostal: address.foreignPostal,
        primaryMail: address.primaryMail,
        foreignAddress: address.foreignAddress,
        pl4: address.pl4,
      })),
    };
    this.dialogRef.close(contactDto);
  }
}
