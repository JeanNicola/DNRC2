import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { AfterViewInit, Component, Inject } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { SourceService } from '../../services/source.service';

@Component({
  selector: 'app-source-select-dialog',
  templateUrl: './source-select-dialog.component.html',
  styleUrls: ['./source-select-dialog.component.scss'],
  providers: [SourceService],
})
export class SourceSelectDialogComponent
  extends SearchSelectDialogComponent
  implements AfterViewInit
{
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      canInsert: Boolean;
    },
    public dialogRef: MatDialogRef<SearchSelectDialogComponent>,
    public service: SourceService
  ) {
    super(data, dialogRef, service);
  }
  public searchTitle = 'Search';
  public selectTitle = 'Pick a Source';
  public addTooltip = 'Confirm';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'sourceName',
      title: 'Source Name',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
    },
    {
      columnId: 'forkName',
      title: 'Fork Name',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      displayInTable: false,
    },
    {
      columnId: 'knownAs',
      title: 'Also Known As Name',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      displayInTable: false,
    },
  ];
  public searchColumns = this.columns.filter(
    (item) => item?.displayInSearch ?? true
  );
  public displayFields = this.columns.filter(
    (item) => item?.displayInTable ?? true
  );
  public createFields = this.columns.filter(
    (item) => item?.displayInInsert ?? true
  );
  public searchMode = DataManagementDialogModes.Search;
  public mode = DataManagementDialogModes.Insert;
  public searchFormGroup: FormGroup = new FormGroup({});
  public formGroup: FormGroup = new FormGroup({});
  public initialized = false;
  public tabIndex = Number(this.data.values != null);

  public ngAfterViewInit(): void {
    setTimeout(() => {
      this.initialized = true;
    });
  }

  public clearOtherTab(event: any): void {
    if (event.index === 0) {
      this.formGroup.get('sourceName').patchValue(null);
      this.formGroup.get('forkName').patchValue(null);
      this.formGroup.get('knownAs').patchValue(null);
      this.formGroup.markAsPristine();
      this.formGroup.markAsUntouched();
    } else {
      this.row = null;
      this.stepper.previous();
    }
  }

  public stepping(step: StepperSelectionEvent): void {
    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      if (step.previouslySelectedIndex === 0) {
        this.rows = [];
      }

      this.stepper.steps.forEach((v, i) => {
        if (i > step.selectedIndex) {
          v.completed = false;
        }
      });
    } else {
      if (step.selectedIndex === 1) {
        this.queryParameters.filters = { ...this.searchFormGroup.value };
        this.queryParameters.pageNumber = 1;
        this.lookup();
      }
      this.stepper.selected.completed = true;
    }
  }

  public save(): void {
    if (this.row) {
      this.dialogRef.close(this.row);
    } else {
      this.dialogRef.close(this.formGroup.getRawValue());
    }
  }
}
