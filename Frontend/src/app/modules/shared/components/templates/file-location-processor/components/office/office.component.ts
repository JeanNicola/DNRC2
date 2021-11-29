import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
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
import { Office } from '../../file-location-processor.component';

@Component({
  selector: 'app-office',
  templateUrl: '../../../code-table/code-table.template.html',
  styleUrls: ['../../../code-table/code-table.template.scss'],
  providers: [BaseDataService],
})
export class OfficeComponent extends BaseCodeTableComponent {
  @Input() set comparisonDate(value: string) {
    this.setValidators(this.data);
  }
  @Input() officeSubject: ReplaySubject<any>;
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
      columnId: 'officeId',
      title: 'Office',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      editable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'officeDescription',
      title: 'Office',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'receivedDate',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
      validators: [],
    },
    {
      columnId: 'sentDate',
      title: 'Sent Date',
      type: FormFieldTypeEnum.Date,
      displayInInsert: false,
      customErrorMessages: {
        required: 'Required: only one Office can have no Sent Date',
      },
      validators: [],
    },
  ];
  public title = 'Offices';
  public searchable = false;
  public primarySortColumn = 'sentDate';
  public sortDirection = 'desc';

  initFunction(): void {
    this._get();
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  private setValidators(data: any): void {
    const latestSentDateValidator =
      data?.latestSentDate != null
        ? [WRISValidators.afterDate(data.latestSentDate)]
        : [];

    this._getColumn('receivedDate').validators = [
      Validators.required,
      WRISValidators.dateBeforeToday,
      WRISValidators.beforeOtherField('sentDate', 'Sent Date'),
      ...latestSentDateValidator,
    ];

    this._getColumn('sentDate').validators = [
      WRISValidators.dateBeforeToday,
      WRISValidators.afterOtherField('receivedDate', 'Received Date'),
    ];
  }

  protected _getHelperFunction(data: any): any {
    const newData = { ...data.get };
    newData.results.forEach((row) => {
      row.disableDelete = row.isSystemGenerated;
      row.disableEdit = !!row.sentDate;
    });
    this.setValidators(newData);
    return newData;
  }

  protected populateDropdowns(): void {
    this.observables.offices = new ReplaySubject(1);
    this.officeSubject.subscribe((offices: { results: Office[] }) => {
      this._getColumn('officeId').selectArr = offices.results.map(
        (office: Office) => ({
          name: office.description,
          value: office.officeId,
        })
      );
      this.observables.offices.next(offices);
      this.observables.offices.complete();
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
        'A Sent Date is required for all Offices before a new one can be added'
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
    if (data.receivedDate) {
      this._getColumn('receivedDate').editable = false;
    } else {
      this._getColumn('sentDate').editable = false;
    }

    if (data.sentDate) {
      this._getColumn('sentDate').editable = false;
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
      this._getColumn('receivedDate').editable = true;
      this._getColumn('sentDate').editable = true;
    });
  }
}
