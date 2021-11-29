import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { AfterViewInit, Component, Inject, OnDestroy } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { CompactSearchService } from 'src/app/modules/shared/services/compact-search.service';

@Component({
  selector: 'app-insert-version-compact',
  templateUrl: './insert-version-compact.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.scss',
    './insert-version-compact.component.scss',
  ],
  providers: [CompactSearchService],
})
export class InsertVersionCompactComponent
  extends SearchSelectDialogComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      formColumns: ColumnDefinitionInterface[];
    },
    public dialogRef: MatDialogRef<InsertVersionCompactComponent>,
    public service: CompactSearchService
  ) {
    super(data, dialogRef, service);
  }

  private unsubscribe = new Subject();

  public title = 'Add New Compact';
  public searchTitle = 'Search for Sub-Compact';
  public selectTitle = 'Select Sub-Compact';
  public inputDataFormGroup: FormGroup = new FormGroup({});

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public ngAfterViewInit(): void {
    // If Exempt or Allocation are true then blm is cleared and disabled
    this.inputDataFormGroup
      .get('exemptCompact')
      .valueChanges.pipe(takeUntil(this.unsubscribe))
      .subscribe((exempt: boolean) => {
        if (exempt || this.inputDataFormGroup.get('allocation').value) {
          this.disableAndClearField('blm');
        } else {
          this.enableField('blm');
        }
      });

    this.inputDataFormGroup
      .get('allocation')
      .valueChanges.pipe(takeUntil(this.unsubscribe))
      .subscribe((allocation: boolean) => {
        if (allocation || this.inputDataFormGroup.get('exemptCompact').value) {
          this.disableAndClearField('blm');
        } else {
          this.enableField('blm');
        }
      });
  }

  protected lookup(): void {
    this.service.get(this.queryParameters).subscribe((data) => {
      this.queryResult = this.postLookup(data);

      this.rows = data.results;
      this.row = data.totalElements > 0 ? this.rows[0] : null;
      this.dataFound = data.totalElements > 0;
    });
  }

  // If the user walks backwards from the last page, clear out the choices
  public stepping(step: StepperSelectionEvent): void {
    super.stepping(step);
    // Sinced they returned to the selection screen, unset the current selection
    if (step.selectedIndex < 2) {
      this.row = null;
    }
  }

  // select row and move to next step
  public onRowDoubleClick(idx: number): void {
    this.row = this.rows[idx];
    this.stepper.next();
  }

  public save(): void {
    const data = {
      ...this.row,
      ...this.inputDataFormGroup.getRawValue(),
    };
    delete data.isExpanded;
    this.dialogRef.close(data);
  }

  private enableField(columnId: string) {
    if (this.inputDataFormGroup.get(columnId).enabled) {
      return;
    }
    this.inputDataFormGroup.get(columnId).enable();
  }

  private disableAndClearField(columnId: string) {
    if (this.inputDataFormGroup.get(columnId).disabled) {
      return;
    }
    this.inputDataFormGroup.get(columnId).setValue(false);
    this.inputDataFormGroup.get(columnId).disable();
  }
}
