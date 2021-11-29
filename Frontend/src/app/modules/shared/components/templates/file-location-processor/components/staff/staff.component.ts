import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { InsertDialogComponent } from '../../../../dialogs/data-management/components/insert-dialog.component';
import { UpdateDialogComponent } from '../../../../dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from '../../../code-table/code-table.template';
import { Staff } from '../../file-location-processor.component';

@Component({
  selector: 'app-staff',
  templateUrl: '../../../code-table/code-table.template.html',
  styleUrls: [
    '../../../code-table/code-table.template.scss',
    'staff.component.scss',
  ],
  providers: [BaseDataService],
})
export class StaffComponent extends BaseCodeTableComponent {
  @Input() set comparisonDate(value: string) {
    this.setValidators(this.data);
  }
  @Input() staffSubject: ReplaySubject<any>;

  constructor(
    public service: BaseDataService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
  public data: any;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'id',
      title: 'Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'staffId',
      title: 'Staff',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      editable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'name',
      title: 'Staff',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
      validators: [],
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      displayInInsert: false,
      customErrorMessages: {
        required: 'Required: only one Staff can have no Sent Date',
      },
      validators: [],
    },
  ];
  public title = 'Staff';
  public searchable = false;
  public primarySortColumn = 'endDate';
  public sortDirection = 'desc';

  initFunction(): void {
    this._get();
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  private setValidators(data: any): void {
    const latestEndDateValidator =
      data?.latestEndDate != null
        ? [WRISValidators.afterDate(data.latestEndDate)]
        : [];

    this._getColumn('beginDate').validators = [
      Validators.required,
      WRISValidators.dateBeforeToday,
      WRISValidators.beforeOtherField('endDate', 'End Date'),
      ...latestEndDateValidator,
    ];

    this._getColumn('endDate').validators = [
      WRISValidators.dateBeforeToday,
      WRISValidators.afterOtherField('beginDate', 'Begin Date'),
    ];
  }

  protected _getHelperFunction(data: any): any {
    const newData = { ...data.get };
    newData.results.forEach((row) => {
      row.disableDelete = row.isSystemGenerated;
      row.disableEdit = !!row.endDate;
    });
    this.setValidators(newData);
    return newData;
  }

  protected populateDropdowns(): void {
    this.observables.staffMembers = new ReplaySubject(1);
    this.staffSubject.subscribe((staffMembers: { results: Staff[] }) => {
      this._getColumn('staffId').selectArr = staffMembers.results.map(
        (office: Staff) => ({
          name: office.name,
          value: office.staffId,
        })
      );
      this.observables.staffMembers.next(staffMembers);
      this.observables.staffMembers.complete();
    });
  }

  protected _buildEditIdArray(dto: any): string[] {
    return [...this.idArray, dto.id];
  }

  protected _buildEditDto(originalData: any, editedData: any): any {
    return { id: originalData.id, ...editedData };
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].id];
  }

  protected _displayInsertDialog(data: any): void {
    if (!this.data?.canInsert) {
      this.snackBar.open(
        'An End Date is required for all Staff before a new one can be added'
      );
      return;
    }

    // Open the dialog
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Add New ${this.title} Record`,
        columns: this.columns,
        values: data,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      }
    });
  }

  protected _displayEditDialog(data: any): void {
    if (data.beginDate) {
      this._getColumn('beginDate').editable = false;
    }

    if (data.endDate) {
      this._getColumn('endDate').editable = false;
    }

    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title} Record`,
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result));
      }
      this._getColumn('beginDate').editable = true;
      this._getColumn('endDate').editable = true;
    });
  }
}
