import {
  AfterViewInit,
  Component,
  EventEmitter,
  Inject,
  Output,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { first } from 'rxjs/operators';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CityZipCodesService } from 'src/app/modules/features/code-tables/components/city-zipcode/services/city-zip-codes.service';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { Address } from '../../../interfaces/contact-interface';
import { buildAddressLine2 } from '../../../shared/build-address-line-2';

@Component({
  selector: 'shared-insert-dialog',
  templateUrl: './create-address-dialog.component.html',
  styleUrls: [
    '../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
    './create-address-dialog.component.scss',
  ],
  providers: [CityZipCodesService],
})
export class CreateAddressDialogComponent
  extends DataManagementDialogComponent
  implements AfterViewInit
{
  @Output()
  public blurEvent: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<CreateAddressDialogComponent>,
    private cityZipCodesService: CityZipCodesService,
    private snackBar: SnackBarService
  ) {
    super(dialogRef);
  }

  public mode = this.data.mode;

  public title =
    `${
      this.data.mode === DataManagementDialogModes.Insert
        ? 'Add New '
        : 'Update '
    }` +
    this.data.title +
    ' Record';
  public tooltip = `${
    this.data.mode === DataManagementDialogModes.Insert ? 'Insert' : 'Update'
  }`;
  public checkboxesState: any = {
    isForeign: this.data.values.foreignAddress === 'Y',
    isPrimMail: this.getPrimMail(this.data.values.isPrimMail),
    rtnMail: this.data.values.unresolvedFlag === 'Y',
  };
  public displayFields = this.getDisplayFields(false);
  public currentLocs: any[] = [];

  // Info for the checkboxes below the title
  public addressCheckboxes: ColumnDefinitionInterface[] = [
    {
      columnId: 'isPrimMail',
      title: 'Prim Mail',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];

  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  initFunction() {
    if (this.mode === DataManagementDialogModes.Insert) {
      this.addressCheckboxes.push({
        columnId: 'isForeign',
        title: 'Foreign',
        type: FormFieldTypeEnum.Checkbox,
      });
    }
    if (this.data.values?.showRtnMail) {
      this.addressCheckboxes.push({
        columnId: 'rtnMail',
        title: 'Rtn Mail',
        type: FormFieldTypeEnum.Checkbox,
      });
    }
    if (this.mode === DataManagementDialogModes.Update) {
      if (this.checkboxesState.isForeign) {
        return;
      }
      // If the address is not foreign and we're in UPDATE mode then search for the city and state
      const zipCodeId = this.data.values?.zipCodeId;
      this.triggerZipSearch(this.data.values.zipCode, { zipCodeId }, false);
    }
  }

  ngAfterViewInit() {
    if (this.mode === DataManagementDialogModes.Insert) {
      // SetTimeout is used to avoid error ExpressionChangedAfterItHasBeenCheckedError
      setTimeout(() => {
        // Disable the fields when the dialog first opens
        this.formGroup.get('cityAndState').disable();
      });
    }
    setTimeout(() => {
      if (this.data.values?.contactStatus === 'DEC') {
        this.formGroup.get('addressLine1').disable();
        this.formGroup.get('addressLine1').setValue('***DECEASED***');
      }
    });
  }

  private getColumn(columnId: string): ColumnDefinitionInterface {
    let index: number;
    for (let i = 0; i < this.displayFields.length; i++) {
      if (this.displayFields[i].columnId === columnId) {
        index = i;
      }
    }
    return this.displayFields[index];
  }

  // Function that determines whether to show the foreign field
  private getDisplayFields(treatAsInsert: boolean) {
    const displayFields = this.checkboxesState?.isForeign
      ? this.data.values.foreignAddressColumns
      : this.data.values.addressColumns;
    return displayFields.filter((item) => {
      if (this.mode === DataManagementDialogModes.Insert || treatAsInsert) {
        return item?.displayInInsert == null ? true : item?.displayInInsert;
      }

      if (this.mode === DataManagementDialogModes.Update && !treatAsInsert) {
        return item?.displayInEdit == null ? true : item?.displayInEdit;
      }
    });
  }

  // Getter for isPrimMail
  private getPrimMail(value: boolean) {
    // allow primMail to change if canChangeIsPrimMail is false
    if (this.data.values.canChangeIsPrimMail) {
      return value;
    }
    return this.data.values.isPrimMail;
  }

  private searchLocationWithZip(zipCode: string) {
    const queryParameters = { ...this.queryParameters, filters: { zipCode } };
    return this.cityZipCodesService.get(queryParameters).pipe(first());
  }

  private getCitiesAndStatesArr(zipResults: any[]): { values: any[] } {
    if (!zipResults) {
      return;
    }

    const citiesAndStates = [];

    zipResults.forEach((zipResult) => {
      citiesAndStates.push({
        value: zipResult.id,
        name: `${zipResult.cityName}, ${zipResult.stateName}`,
      });
    });

    return { values: citiesAndStates };
  }

  private handleZipResponse(data: any, values?: { zipCodeId: string }) {
    this.currentLocs = data?.results || [];
    if (!this.formGroup.get('cityAndState')) {
      return;
    }
    if (data?.results?.length >= 1) {
      // Enable the fields
      this.formGroup.get('cityAndState').enable();
      // Set the values in the select fields
      const citiesAndStatesArr = this.getCitiesAndStatesArr(data.results);
      this.getColumn('cityAndState').selectArr = citiesAndStatesArr.values;

      if (data?.results?.length > 1 && citiesAndStatesArr.values?.length) {
        citiesAndStatesArr.values.unshift({
          value: null,
          description: null,
        });
      }

      // Set the value passed as parameter or the first result found in the search
      this.formGroup
        .get('cityAndState')
        .setValue(values?.zipCodeId || citiesAndStatesArr.values[0].value);
    } else {
      this.snackBar.open('Please provide a valid Zip Code.');
      this.formGroup.get('zipCode').setValue('');
      this.formGroup.get('cityAndState').setValue('');
      this.getColumn('cityAndState').selectArr = [];
    }
  }

  private triggerZipSearch(
    zipCode: string,
    values?: { zipCodeId: string },
    validateForm = true
  ) {
    // Make sure the ZipCode is valid before triggering the request
    if (
      zipCode.length >= 5 &&
      (!validateForm || this.formGroup.get('zipCode').valid)
    ) {
      this.searchLocationWithZip(zipCode).subscribe((data) => {
        // Handle response data
        this.handleZipResponse(data, values);
      });
    }
  }

  private getZipInfoWithZipId(zipId) {
    const loc = this.currentLocs.filter((loc) => loc.id === zipId);
    if (loc[0]) {
      return loc[0];
    }
  }

  // Handle primMail and isForeign changes
  public checkboxChangeHandler(event) {
    // Filter by isPrimMail
    if (event.fieldName === 'isPrimMail') {
      this.checkboxesState.isPrimMail = this.getPrimMail(event.checked);
      this.formGroup
        .get('isPrimMail')
        .setValue(this.checkboxesState.isPrimMail);
    }
    if (event.fieldName === 'rtnMail') {
      this.checkboxesState.rtnMail = event.checked;
    }

    // Filter by isForeign
    if (event.fieldName === 'isForeign') {
      // Reset values
      this.data.values = {
        addressColumns: this.data.values.addressColumns,
        foreignAddressColumns: this.data.values.foreignAddressColumns,
        isPrimMail: this.data.values.isPrimMail,
        canChangeIsPrimMail: this.data.values.canChangeIsPrimMail,
      };

      if (this.getColumn('cityAndState')) {
        this.getColumn('cityAndState').selectArr = [];
      }

      // Update Form with foreign fields
      this.checkboxesState.isForeign = event.checked;
      this.formGroup = new FormGroup({});
      this.displayFields = [...this.getDisplayFields(event.checked)];
      this.addressCheckboxes = this.addressCheckboxes.map((field) => ({
        ...field,
      }));
      // Restore focus
      setTimeout(() => {
        const addressLine1: HTMLElement = document.querySelector(
          'input[ng-reflect-name="addressLine1"]'
        );
        if (addressLine1) {
          addressLine1.focus();
        }
      });
    }
  }

  public onBlurHandler(data) {
    if (this.checkboxesState.isForeign) {
      return;
    }

    if (data.fieldName === 'zipCode') {
      const value = data.event.target.value;
      // Trigger ZipCode search
      this.triggerZipSearch(value);
    }
  }

  public save(): void {
    const address: Address = {
      ...this.formGroup.getRawValue(),
      foreignAddressValue: this.checkboxesState.isForeign ? 'YES' : 'NO',
      foreignAddress: this.checkboxesState.isForeign ? 'Y' : 'N',
      isForeign: this.checkboxesState.isForeign,
      primaryMailValue: this.checkboxesState.isPrimMail ? 'YES' : 'NO',
      primaryMail: this.checkboxesState.isPrimMail ? 'Y' : 'N',
      isPrimMail: this.checkboxesState.isPrimMail,
      unresolvedFlag: this.checkboxesState.rtnMail ? 'Y' : 'N',
      unresolvedFlagValue: this.checkboxesState.rtnMail ? 'YES' : 'NO',
      rtnMail: this.checkboxesState.rtnMail,
    };

    // If isForeign is not checked then set the city and state values
    if (!this.checkboxesState.isForeign) {
      const zipInfo = this.getZipInfoWithZipId(
        this.formGroup.getRawValue().cityAndState
      );

      address.cityName = zipInfo?.cityName;
      address.stateCode = zipInfo?.stateCode;
      address.cityId = zipInfo?.cityId;
      address.stateName = zipInfo?.stateName;
      address.zipCodeId = zipInfo?.id;
    }

    // Set all string values to uppercase
    Object.keys(address).forEach((key) => {
      if (typeof address[key] === 'string') {
        address[key] = address[key].toUpperCase();
      }
    });

    // If the user tries to save a non-foreign address without the cityId, stateCode or zipCodeId the modal shouldn't close
    if (
      (!address.cityId || !address.zipCodeId || !address.stateCode) &&
      !this.checkboxesState.isForeign
    ) {
      return;
    }

    if (
      this.checkboxesState.isForeign &&
      buildAddressLine2(address)?.length > 50
    ) {
      this.snackBar.open(
        'The concatenation of Foreign City, Foreign Province or State and Foreign Zip cannot have more than 48 digits.'
      );
      return;
    }
    this.dialogRef.close(address);
  }
}
