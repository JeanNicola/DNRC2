import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { idToStateCountyNumber } from 'src/app/modules/shared/utilities/id-to-state-county-number';
import { stateCountyNumberToId } from 'src/app/modules/shared/utilities/state-county-number-to-id';

@Component({
  selector: 'code-table-subdivision-codes-insert-dialog',
  templateUrl:
    '../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class SubdivisionCodesInsertDialogComponent
  extends InsertDialogComponent
  implements OnInit, OnDestroy
{
  private unsubscribe = new Subject();

  constructor(
    public dialogRef: MatDialogRef<SubdivisionCodesInsertDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    super(dialogRef, data);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this._connectCodeAndCountyId();
    // this.formGroup.controls['code'].
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  // Stores the previous row for connecting the fields.
  priorNewRow: any;

  // // Function will detect newRow changes and automatically keep code and county in sync
  // // id and countyId - DB value, incremental, primary key
  // // stateCountyId - 2 digit string representing the county
  // // code - 5 character string, first two digits are the stateCountyId
  _connectCodeAndCountyId(): void {
    this.formGroup.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((row) => {
        const newCountyId = row.countyId as unknown;
        const oldCountyId = this.priorNewRow?.countyId;
        const newCode = row.code;
        const oldCode = this.priorNewRow?.code;
        // If countyId was changed, is a number, and is a known good value, and Code has not changed
        // and first two digits of Code do not match the equivalent stateCountyId, then replace Code with the stateCountyId
        if (
          !isNaN(newCountyId as number) &&
          newCountyId !== null &&
          newCountyId !== '' &&
          newCountyId !== oldCountyId &&
          (!newCode ||
            idToStateCountyNumber(
              newCountyId as number,
              this.data.countiesArr
            ) !== newCode.substring(0, 2)) &&
          this.data.columns[3].selectArr.filter((x) => x.value === newCountyId)
            .length
        ) {
          this.formGroup.patchValue({
            code: idToStateCountyNumber(
              newCountyId as number,
              this.data.countiesArr
            ),
          });
        }
        // If Code was changed, is a known good value, and does not match
        // the countyId's equivalent stateCountyId, then replace countyId with the appropriate id
        else if (newCode != null) {
          if (
            newCode.length >= 2 &&
            oldCode !== newCode &&
            oldCountyId === newCountyId
          ) {
            const id = stateCountyNumberToId(
              newCode.substring(0, 2),
              this.data.countiesArr
            );
            if (id && id !== newCountyId) {
              this.formGroup.patchValue({ countyId: id }, { emitEvent: false });
            }
          }
        }
        this.priorNewRow = row;
      });
  }
}
