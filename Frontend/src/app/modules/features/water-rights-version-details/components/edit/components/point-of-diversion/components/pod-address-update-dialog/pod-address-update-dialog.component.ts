import { AfterViewInit, Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CityZipCodesService } from 'src/app/modules/features/code-tables/components/city-zipcode/services/city-zip-codes.service';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'app-pod-address-update-dialog',
  templateUrl:
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
  providers: [CityZipCodesService],
})
export class PodAddressUpdateDialogComponent
  extends UpdateDialogComponent
  implements AfterViewInit
{
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public zipCodeService: CityZipCodesService,
    public snackBar: SnackBarService
  ) {
    super(dialogRef, data);
  }

  public ngAfterViewInit(): void {
    if (this.data.values?.zipCode != null) {
      this.setZipCodeSelectArray(this.data.values.zipCode);
    }
    setTimeout(() => {
      if (this.data.values?.zipCodeId == null) {
        this.formGroup.get('zipCodeId').disable();
      }
      this.formGroup.get('zipCodeId').setValue(this.data.values.zipCodeId);
      this.formGroup.get('zipCode').setValue(this.data.values.zipCode);
    });
  }

  public _onBlur($event) {
    if ($event?.fieldName === 'zipCode') {
      this.formGroup.get('zipCodeId').setValue(null);
      this.setZipCodeSelectArray(this.formGroup.get('zipCode').value);
    }
  }

  // for clearing out field
  public _onChange($event) {
    if ($event?.fieldName === 'zipCode') {
      this.formGroup.get('zipCodeId').setValue(null);
      this.setZipCodeSelectArray(this.formGroup.get('zipCode').value);
    }
  }

  private setZipCodeSelectArray(zipCode): void {
    if(!Boolean(zipCode)) {
      this.formGroup.get('zipCodeId').disable();
      this.getColumn('zipCodeId').selectArr = [];
      return;
    }
    this.zipCodeService
      .get({ filters: { zipCode: zipCode } })
      .subscribe((data) => {
        if (data.totalElements < 1) {
          this.snackBar.open('Please provide a valid Zip Code.');
          this.getColumn('zipCodeId').selectArr = [];
          return;
        }
        this.getColumn('zipCodeId').selectArr = data.results.map((city) => {
          return {
            name: city.cityName,
            value: city.id,
          }
        });
        if (data.totalElements === 1) {
          this.formGroup.get('zipCodeId').setValue(data.results[0].id);
          this.formGroup.get('zipCodeId').disable();
        } else {
          this.formGroup.get('zipCodeId').enable();
        }
      });
  }

  private getColumn(columnId: string): ColumnDefinitionInterface {
    return this.displayFields.find(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );
  }

  public save(): void {
    const address = this.formGroup.getRawValue();
    this.dialogRef.close(address);
  }
}
