import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Inject,
  OnDestroy,
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';

@Component({
  selector: 'app-fee-summary-update-dialog',
  templateUrl:
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class FeeSummaryUpdateDialogComponent
  extends UpdateDialogComponent
  implements AfterViewInit, OnDestroy
{
  private unsubscribe = new Subject();

  constructor(
    public dialogRef: MatDialogRef<FeeSummaryUpdateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private cd: ChangeDetectorRef
  ) {
    super(dialogRef, data);
  }

  private fieldsUsed: string[];

  ngAfterViewInit(): void {
    // copy the columns so we can modify them freely
    this.data.columns = this.data.columns.map((col) => ({ ...col }));
    this.formGroup
      .get('feeWaived')
      .valueChanges.pipe(takeUntil(this.unsubscribe))
      .subscribe((feeWaived) => {
        let index;
        for (const i in this.data.columns) {
          if (this.data.columns[i].columnId === 'feeWaivedReason') {
            index = i;
            break;
          }
        }
        if (this.data.values.feeWaived === 'N') {
          this.data.columns[index].displayInEdit = feeWaived === 'Y';
          this.displayFields = this._getDisplayFields(this.data.columns);
          // prevent Expression CHange After it has been checked error, since we're updating the formGroup
          this.cd.detectChanges();
          if (feeWaived === 'N') {
            this.formGroup.removeControl('feeWaivedReason');
          }
        }
      });

    this.fieldsUsed = this.displayFields.map((field) => field.columnId);
    this.fieldsUsed.forEach((columnName) => {
      this.formGroup
        .get(columnName)
        .valueChanges.pipe(takeUntil(this.unsubscribe))
        .subscribe((value) => {
          if (value === 'Y') {
            this.changeOthersToNo(columnName);
          }
        });
    });
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  private changeOthersToNo(columnName: string): void {
    this.fieldsUsed
      .filter((col) => col !== columnName)
      .forEach((value) => {
        this.formGroup.get(value).patchValue('N');
      });
  }
}
