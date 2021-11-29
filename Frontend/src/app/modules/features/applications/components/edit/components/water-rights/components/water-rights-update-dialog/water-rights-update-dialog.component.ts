import { AfterViewInit, Component, Inject, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';

@Component({
  selector: 'app-water-rights-update-dialog',
  templateUrl:
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class WaterRightsUpdateDialogComponent
  extends UpdateDialogComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    public dialogRef: MatDialogRef<WaterRightsUpdateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    super(dialogRef, data);
  }
  title = 'Update Status';
  private unsubscribe = new Subject();

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  ngAfterViewInit(): void {
    if (
      this.data.values.numVersions === 1 &&
      !['606', '634', '635', '644'].includes(this.data.applicationType)
    ) {
      this.formGroup
        .get('statusCode')
        .valueChanges.pipe(takeUntil(this.unsubscribe))
        .subscribe((statusCode) => {
          this.formGroup.patchValue(
            { versionStatusCode: statusCode },
            { emitEvent: false }
          );
        });
      this.formGroup
        .get('versionStatusCode')
        .valueChanges.pipe(takeUntil(this.unsubscribe))
        .subscribe((versionStatusCode) => {
          this.formGroup.patchValue(
            { statusCode: versionStatusCode },
            { emitEvent: false }
          );
        });
    }
  }
}
