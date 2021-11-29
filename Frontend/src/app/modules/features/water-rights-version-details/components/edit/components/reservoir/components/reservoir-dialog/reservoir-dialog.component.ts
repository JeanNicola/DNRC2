import { AfterViewInit, Component, Inject, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { PodOption } from '../../services/version-all-pods.service';

@Component({
  selector: 'app-insert-reservoir-dialog',
  templateUrl: './reservoir-dialog.component.html',
  styleUrls: ['./reservoir-dialog.component.scss'],
})
export class ReservoirDialogComponent
  extends InsertDialogComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      podOptions: PodOption[];
      type: DataManagementDialogModes;
      legalLandDescriptionFields: string[];
      firstColumns: ColumnDefinitionInterface[];
      secondColumns: ColumnDefinitionInterface[];
      firstReservoirCharacteristicColumns: ColumnDefinitionInterface[];
      secondReservoirCharacteristicColumns: ColumnDefinitionInterface[];
      firstLegalLandDescriptionColumns: ColumnDefinitionInterface[];
      secondLegalLandDescriptionColumns: ColumnDefinitionInterface[];
    }
  ) {
    super(dialogRef, data);
    this.mode = data.type;
    if (this.mode === DataManagementDialogModes.Update) {
      this.tooltip = 'Update';
    }
  }

  private unsubscribe = new Subject();

  public displayFirstFields = this._getDisplayFields(this.data.firstColumns);
  public displaySecondFields = this._getDisplayFields(this.data.secondColumns);
  public displayFirstReservoirCharacteristicFields = this._getDisplayFields(
    this.data.firstReservoirCharacteristicColumns
  );
  public displaySecondReservoirCharacteristicFields = this._getDisplayFields(
    this.data.secondReservoirCharacteristicColumns
  );
  public displayFirstLegalLandDescriptionFields = this._getDisplayFields(
    this.data.firstLegalLandDescriptionColumns
  );
  public displaySecondLegalLandDescriptionFields = this._getDisplayFields(
    this.data.secondLegalLandDescriptionColumns
  );

  private legalLandDescriptionId: number;

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public ngAfterViewInit(): void {
    // only for Inserts
    if (this.mode === DataManagementDialogModes.Insert) {
      this.formGroup
        .get('podId')
        .valueChanges.pipe(takeUntil(this.unsubscribe))
        .subscribe((podId) => {
          // check that the Legal Land Description has not been overridden
          if (this.isLegalLandDescriptionPristine() && podId) {
            const selectedOption = this.data.podOptions.filter(
              (o: PodOption) => o.podId === podId
            )[0];
            this.legalLandDescriptionId = selectedOption.legalLandDescriptionId;
            this.data.legalLandDescriptionFields.forEach((c: string) => {
              this.formGroup.get(c).setValue(selectedOption[c]);
            });
          } else {
            this.legalLandDescriptionId = null;
          }
        });
    }
  }

  // if the legal land description was populated by the pod id, use the legal land description id
  public save(): void {
    const returnDto = this.formGroup.getRawValue();
    if (this.isLegalLandDescriptionPristine() && this.legalLandDescriptionId) {
      this.data.legalLandDescriptionFields.forEach((field: string) => {
        delete returnDto[field];
      });
      returnDto.legalLandDescriptionId = this.legalLandDescriptionId;
    }
    this.dialogRef.close(returnDto);
  }

  private isLegalLandDescriptionPristine() {
    return this.data.legalLandDescriptionFields.every(
      (field: string) =>
        this.formGroup.get(field).pristine || !this.formGroup.get(field).value
    );
  }

  protected _getDisplayFields(columns: ColumnDefinitionInterface[]) {
    const filterFunction =
      this.mode === DataManagementDialogModes.Update
        ? (field) => field?.displayInEdit ?? true
        : (field) => field?.displayInInsert ?? true;
    return columns.filter(filterFunction).map((item) => ({
      ...item,
    }));
  }
}
