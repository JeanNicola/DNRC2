import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'app-payments-dialog',
  templateUrl: './payments-dialog.component.html',
  styleUrls: ['./payments-dialog.component.scss'],
})
export class PaymentsDialogComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<PaymentsDialogComponent>
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
  public displayFields = this.getDisplayFields(this.data.columns);

  private getDisplayFields(columns) {
    return columns.filter((item) => {
      if (this.mode === DataManagementDialogModes.Insert) {
        return item?.displayInInsert == null ? true : item?.displayInInsert;
      }

      if (this.mode === DataManagementDialogModes.Update) {
        return item?.displayInEdit == null ? true : item?.displayInEdit;
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

  public _onBlur($event: any) {
    if (
      $event?.fieldName == 'trackingNumber' &&
      this.mode === DataManagementDialogModes.Insert
    ) {
      // check whether the value of trackingNumber starts with a number or letter
      const startsWithNumber = /^[0-9](.+)?/.test($event?.event?.target?.value);
      const startsWithLetter = /^([A-Z]|[a-z])(.+)?/.test(
        $event?.event?.target?.value
      );

      if (startsWithNumber) {
        if (this.data.values.type === 'ownershipUpdate') {
          this.getColumn('origin').valueObservable$.next('OUID');
        } else {
          this.getColumn('origin').valueObservable$.next('APPL');
        }
      }

      if (startsWithLetter) {
        if (
          ($event?.event?.target?.value as string)
            .substring(0, 3)
            .toUpperCase() === 'REF'
        ) {
          this.getColumn('origin').valueObservable$.next('RFND');
        } else {
          this.getColumn('origin').valueObservable$.next('TLMS');
        }
      }
    }
  }
}
