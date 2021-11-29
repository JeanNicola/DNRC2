import { Component, Inject } from '@angular/core';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as moment from 'moment';
import { GeocodePipe } from 'src/app/modules/features/water-rights/pipes/geocode.pipe';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';

@Component({
  selector: 'app-insert-geocodes-dialog',
  templateUrl: './insert-geocodes-dialog.component.html',
  styleUrls: ['./insert-geocodes-dialog.component.scss'],
  providers: [GeocodePipe],
})
export class InsertGeocodesDialogComponent extends InsertDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      tableValues: any[];
      idArray: string[];
    },
    private geocodePipe: GeocodePipe
  ) {
    super(dialogRef, data);
  }

  public geocodes = [];

  public permissions: PermissionsInterface = {
    canDELETE: true,
    canPUT: false,
    canPOST: false,
    canGET: true,
  };

  private baseValidator = this.data.columns[1].validators;

  public addGeocode(): void {
    const geocode = this.formGroup.getRawValue();
    geocode.formattedGeocode = this.geocodePipe.transform(geocode.geocodeId);
    if (geocode.comments) {
      geocode.comments = geocode.comments.toUpperCase();
    }
    this.geocodes.push(geocode);
    // This forces change detection for the array since Angualr won't notice it
    this.geocodes = this.geocodes.slice();
    this.formGroup.reset();
    this.formGroup.get('beginDate').setValue(this.getToday());
    this.formGroup
      .get('geocodeId')
      .setValidators([...this.baseValidator, this.preventDuplicate()]);
  }

  protected initFunction(): void {
    if (this.data?.values !== null) {
      this.geocodes.unshift(
        ...this.data.values.map((geocode) => {
          geocode.formattedGeocode = this.geocodePipe.transform(
            geocode.geocodeId
          );
          return geocode;
        })
      );
    }
  }

  private preventDuplicate(): ValidatorFn {
    const gs = this.geocodes.map((g) => g.formattedGeocode);
    return (control: AbstractControl): ValidationErrors | null => {
      if (gs.includes(control.value)) {
        return {
          errorMessage: `${control.value} is already being added`, // this is just for the custom messaging
        };
      }
      return null;
    };
  }

  public onDelete(row: number): void {
    this.geocodes.splice(row, 1);
    // This forces change detection for the array since Angualr won't notice it
    this.geocodes = this.geocodes.slice();
    this.formGroup
      .get('geocodeId')
      .setValidators([...this.baseValidator, this.preventDuplicate()]);
    this.formGroup.get('geocodeId').updateValueAndValidity();
  }

  public save(): void {
    const cleanGeocodes = this.geocodes.map((g) => {
      delete g.isExpanded; // if the user doubleclicks on the row this property is added (bug in the table)
      return g;
    });

    this.dialogRef.close(cleanGeocodes);
  }

  _onBlur($event: any): void {
    const geocodeControl = this.formGroup.get('geocodeId');
    if ($event.fieldName === 'geocodeId' && geocodeControl.valid) {
      const formattedGeocode = this.geocodePipe.transform(geocodeControl.value);
      geocodeControl.setValue(formattedGeocode);
    }
  }

  public copyGeocodeId() {
    if (this.geocodes.length) {
      this.formGroup
        .get('geocodeId')
        .setValue(this.geocodes[this.geocodes.length - 1].geocodeId);
    }
  }

  public copyComment() {
    if (this.geocodes.length) {
      this.formGroup
        .get('comments')
        .setValue(this.geocodes[this.geocodes.length - 1].comments);
    }
  }

  private getToday(): moment.Moment {
    return moment().set({
      hour: 0,
      minute: 0,
      second: 0,
      millisecond: 0,
    });
  }

  receiveShiftTab($event: KeyboardEvent, i: number): void {}

  public copyData(data: any): void {
    this.formGroup.get('geocodeId').setValue(data.formattedGeocode);
    this.formGroup
      .get('comments')
      .setValue(data.comments ? data.comments : null);

    // Set the4 focus to the geocodeId which is the first field
    document.querySelector('input').focus();
  }
}
