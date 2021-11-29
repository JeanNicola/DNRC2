import { Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { RemarkCodeService } from '../remarks-table/services/remark-code.service';

export interface InsertRemarkInterface extends DataManagementDialogInterface {
  waterRightId: number;
  formColumns: ColumnDefinitionInterface[];
}

@Component({
  selector: 'app-insert-remark',
  templateUrl: './insert-remark.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.scss',
    './insert-remark.component.scss',
  ],
  providers: [RemarkCodeService],
})
export class InsertRemarkComponent extends SearchSelectDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: InsertRemarkInterface,
    public dialogRef: MatDialogRef<InsertRemarkComponent>,
    public service: RemarkCodeService
  ) {
    super(data, dialogRef, service);
  }

  public title = 'Add New Remark';
  public searchTitle = 'Search for a Remark Code';
  public selectTitle = 'Select a Remark Code';
  public addTooltip = 'Create New Remark';
  public inputDataFormGroup: FormGroup = new FormGroup({});

  public queryParameters: DataQueryParametersInterface & {
    waterRightId: number;
  };
  public initFunction(): void {
    this.queryParameters = {
      sortDirection: this.sortDirection,
      sortColumn: this.sortColumn || this.displayFields[0].columnId,
      pageSize: 25,
      pageNumber: 1,
      filters: {},
      waterRightId: this.data.waterRightId,
    };
  }

  // select row and move to next step
  public onRowDoubleClick(idx: number): void {
    this.row = this.rows[idx];
    this.stepper.next();
  }

  public save(): void {
    this.dialogRef.close({
      ...this.row,
      ...this.inputDataFormGroup.getRawValue(),
    });
  }
}
