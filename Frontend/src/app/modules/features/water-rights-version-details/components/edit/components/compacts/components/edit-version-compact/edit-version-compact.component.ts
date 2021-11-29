import { AfterViewInit, Component, Inject, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';

@Component({
  selector: 'app-edit-version-compact',
  templateUrl:
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class EditVersionCompactComponent
  extends UpdateDialogComponent
  implements AfterViewInit, OnDestroy
{
  private unsubscribe = new Subject();

  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      canCompact: boolean;
    }
  ) {
    super(dialogRef, data);
  }

  private enableField(columnId: string) {
    if (columnId === 'allocation' && !this.data.canCompact) {
      return;
    }
    if (this.formGroup.get(columnId).enabled) {
      return;
    }
    this.formGroup.get(columnId).enable();
  }

  private disableAndClearField(columnId: string) {
    if (this.formGroup.get(columnId).disabled) {
      return;
    }
    this.formGroup.get(columnId).setValue(false);
    this.formGroup.get(columnId).disable();
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public ngAfterViewInit(): void {
    // If Exempt or Allocation are true then blm is cleared and disabled
    this.formGroup
      .get('exemptCompact')
      .valueChanges.pipe(takeUntil(this.unsubscribe))
      .subscribe((exempt: boolean) => {
        if (exempt || this.formGroup.get('allocation').value) {
          this.disableAndClearField('blm');
        } else {
          this.enableField('blm');
        }
      });

    this.formGroup
      .get('allocation')
      .valueChanges.pipe(takeUntil(this.unsubscribe))
      .subscribe((allocation: boolean) => {
        if (allocation || this.formGroup.get('exemptCompact').value) {
          this.disableAndClearField('blm');
        } else {
          this.enableField('blm');
        }
      });

    // Set the default view
    if (
      this.formGroup.get('exemptCompact').value ||
      this.formGroup.get('allocation').value
    ) {
      this.disableAndClearField('blm');
    }
  }
}
