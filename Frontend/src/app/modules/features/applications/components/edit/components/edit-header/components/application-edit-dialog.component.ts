import { AfterViewInit, Component, Inject, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
@Component({
  selector: 'app-edit-dialog',
  templateUrl:
    '../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class ApplicationEditDialogComponent
  extends UpdateDialogComponent
  implements AfterViewInit, OnDestroy
{
  private previousTypeCode: string = null;
  private unsubscribe = new Subject();

  constructor(
    public dialogRef: MatDialogRef<ApplicationEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {
    super(dialogRef, data);
  }

  public ngAfterViewInit(): void {
    // If the value is 600P or 606P, set up subscription to
    // enable/disable date time picker element. If the type
    // value changes, enable/disable accordingly
    if (['600P', '606P'].includes(this.data.values.applicationTypeCode)) {
      this.formGroup
        .get('applicationTypeCode')
        .valueChanges.pipe(takeUntil(this.unsubscribe))
        .subscribe((applType) => {
          // If the current applType doesn't match the previous, set previous to the new appType
          if (this.previousTypeCode !== applType) {
            const dtField = this.formGroup.get('dateTimeReceived');
            this.previousTypeCode = applType;

            if (applType !== this.data.values.applicationTypeCode) {
              // If value doesn't end in P, enable
              dtField.enable({ emitEvent: false });
              dtField.setValue(null);
            } else {
              // otherwise the value ends in P, disable and revert back to original value
              dtField.disable({ emitEvent: false });
              dtField.setValue(this.data.values.dateTimeReceived);
              // Set these fields as "untouched" for validation purposes
              dtField.markAsPristine();
              this.formGroup.get('applicationTypeCode').markAsPristine();
            }
          }
        });
    }
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
