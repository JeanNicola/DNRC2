import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import * as moment from 'moment';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { Reference } from 'src/app/modules/shared/interfaces/reference.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import {
  MeasurementRemarkCodesService,
  RemarkCode,
} from '../../services/measurement-remark-codes.service';
import { MeasurementReportsService } from '../../services/measurement-reports.service';
import { ReportTypesService } from '../../services/report-types.service';

@Component({
  selector: 'app-reports-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './reports-table.component.scss',
  ],
  providers: [
    MeasurementReportsService,
    ReportTypesService,
    MeasurementRemarkCodesService,
  ],
})
export class ReportsTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: MeasurementReportsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public reportTypesService: ReportTypesService,
    public remarkCodesService: MeasurementRemarkCodesService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() operatingAuthorityDate: string;
  @Output() selectReport = new EventEmitter<any>();

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'remarkCode',
      title: 'Remark Code',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'reportTypeCode',
      title: 'Report Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'reportTypeDescription',
      title: 'Report Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'effectiveDate',
      title: 'Effective Date',
      type: FormFieldTypeEnum.Date,
      // Validators are defined in  initFunction()
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      // Validators are defined in  initFunction()
    },
  ];

  public title = '';
  public searchable = false;
  public dblClickableRow = true;
  public clickableRow = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public disableFocus = true;
  public createRemarkCodes: any[];
  public oldRemarkCodes: any[];
  public dialogWidth = '600px';

  public initFunction(): void {
    this._getColumn('effectiveDate').validators = [
      WRISValidators.beforeOtherField('endDate', 'End Date'),
      WRISValidators.minimumYear(moment(this.operatingAuthorityDate).year()),
      WRISValidators.maximumYear(moment().year()),
      Validators.required,
    ];

    this._getColumn('endDate').validators = [
      WRISValidators.afterOtherField('effectiveDate', 'Effective Date'),
      WRISValidators.minimumYear(moment(this.operatingAuthorityDate).year()),
      WRISValidators.maximumYear(moment().year()),
    ];
    this._get();
  }

  public rowClick(data: any): void {
    this.selectReport.emit(data.remarkId);
  }

  protected _getHelperFunction(data: any) {
    if (data.get?.results?.length) {
      this.selectReport.emit(data.get.results[0].remarkId);
    } else {
      this.selectReport.emit(null);
    }

    return data.get;
  }

  protected populateDropdowns(): void {
    this.observables.reportTypes = new ReplaySubject(1);
    this.reportTypesService
      .get(this.queryParameters)
      .subscribe((types: { results: Reference[] }) => {
        this._getColumn('reportTypeCode').selectArr = types.results
          .filter(
            (type: Reference) => type.description != "DON'T USE THIS CODE"
          )
          .map((type: Reference) => ({
            name: type.description,
            value: type.value,
          }));
        this.observables.reportTypes.next(types);
        this.observables.reportTypes.complete();
      });
    this.observables.reportCodes = new ReplaySubject(1);
    this.remarkCodesService
      .get(this.queryParameters)
      .subscribe((types: { results: RemarkCode[] }) => {
        this.createRemarkCodes = types.results
          .filter((type: RemarkCode) => type.createable)
          .map((type: RemarkCode) => ({
            name: type.description,
            value: type.value,
          }));
        this.oldRemarkCodes = types.results
          .filter((type: RemarkCode) => !type.createable)
          .map((type: RemarkCode) => ({
            name: type.description,
            value: type.value,
          }));
        this._getColumn('remarkCode').selectArr = this.createRemarkCodes;
        this.observables.reportCodes.next(types);
        this.observables.reportCodes.complete();
      });
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url) && this.canEdit,
    };
  }

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.remarkId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].remarkId];
  }

  private buildSelectArr(data: any): void {
    this._getColumn('remarkCode').selectArr = this.createRemarkCodes
      .concat(
        this.oldRemarkCodes.filter((code) => code.value == data.remarkCode)
      )
      .sort((a, b) => (a.value < b.value ? -1 : a.value > b.value ? 1 : 0));
  }

  protected _displayEditDialog(data: any): void {
    this.buildSelectArr(data);
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
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }

  protected _update(updatedRow: any, originalData?: any): void {
    this.service
      .update(updatedRow, ...this._buildEditIdArray(updatedRow, originalData))
      .subscribe(
        (dto) => {
          let messages = ['Record sucessfully updated.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this._getColumn('remarkCode').selectArr = this.createRemarkCodes;
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot update record. ';
          message += errorBody.userMessage || ErrorMessageEnum.PUT;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayEditDialog(updatedRow);
        }
      );
  }

  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
