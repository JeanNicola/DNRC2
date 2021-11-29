import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WaterRightStatusesService } from 'src/app/modules/shared/services/water-right-statuses.service';
import { WaterRightsService } from '../../services/water-rights.service';
import { InsertWaterRightComponent } from '../insert-water-right/insert-water-right.component';
import { WaterRightsUpdateDialogComponent } from '../water-rights-update-dialog/water-rights-update-dialog.component';

export interface WaterRightType {
  value: string;
  description: string;
}

@Component({
  selector: 'app-water-rights-table',
  templateUrl: './water-rights-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [WaterRightsService, WaterRightStatusesService],
})
export class WaterRightsTableComponent extends BaseCodeTableComponent {
  private _appTypeCode: string;
  @Input() set appTypeCode(str: string) {
    this._appTypeCode = str;
    this.checkStatusEditable();
  }
  @Output()
  numWaterRightsChanged = new EventEmitter<DataQueryParametersInterface>();

  public title = '';
  zHeight = 2;
  searchable = false;
  public containerStyles = {
    width: '100%',
    marginTop: '20px',
    background: 'rgb(250, 250, 250)',
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'message',
      title: 'Message',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'id',
      title: 'WR ID',
      type: FormFieldTypeEnum.Input,
      list: [
        {
          columnId: 'basin',
          title: 'Basin',
          type: FormFieldTypeEnum.Input,
        },
        {
          columnId: 'waterRightNumber',
          title: 'WR #',
          type: FormFieldTypeEnum.Input,
        },
        {
          columnId: 'ext',
          title: 'Ext',
          type: FormFieldTypeEnum.Input,
        },
        {
          columnId: 'version',
          title: 'Version',
          type: FormFieldTypeEnum.Input,
        },
        {
          columnId: 'waterRightTypeDescription',
          title: 'WR Type',
          type: FormFieldTypeEnum.Input,
          displayInSearch: false,
        },
        {
          columnId: 'waterRightStatusCode',
          title: 'WR Status',
          type: FormFieldTypeEnum.Input,
          displayInSearch: false,
          displayInTable: false,
        },
        {
          columnId: 'waterRightStatusDescription',
          title: 'WR Status',
          type: FormFieldTypeEnum.Input,
          displayInSearch: false,
        },
        // noSort, displayInTable, displayInSearch
      ],
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'waterRightNumber',
      // Name length reduced for more space per Susan
      // title: 'Water Right #',
      title: 'WR #',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      dblClickable: true,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'typeCode',
      title: 'WR Type',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'typeDescription',
      // Name length reduced for more space per Susan
      // title: 'Water Right Type',
      title: 'WR Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'version',
      // title: 'WR Version',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      dblClickable: true,
    },
    {
      columnId: 'versionTypeCode',
      title: 'Version Type',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'versionTypeDescription',
      // Name length reduced for more space per Susan
      // title: 'WR Version Type',
      title: 'Version Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'versionStatusCode',
      title: 'Version Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
    },
    {
      columnId: 'versionStatusDescription',
      // Name length reduced for more space per Susan
      // title: 'WR Version Status',
      title: 'Version Status',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'statusCode',
      title: 'WR Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
    },
    {
      columnId: 'statusDescription',
      title: 'WR Status',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'scanned',
      title: 'Scanned?',
      type: FormFieldTypeEnum.Checkbox,
      displayInEdit: false,
    },
  ];
  public primarySortColumn = 'waterRightNumber';
  public sortDirection = 'desc';

  initFunction(): void {
    this._get();
    if (
      ['102', '607', '608', '610', '617', '618', '630', '631', '638'].includes(
        this._appTypeCode
      )
    ) {
      this.permissions.canPUT = false;
    }
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  _getHelperFunction(data: any): any {
    const newData = { ...data.get };
    newData.results.forEach((row) => {
      // If water right number matches application id, disable delete for that row
      if (row.waterRightNumber === this.idArray[0]) {
        row.disableDelete = true;
      } else {
        row.disableDelete = false;
      }
    });
    return newData;
  }

  constructor(
    public service: WaterRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private waterRightStatusesService: WaterRightStatusesService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Every time the incoming appTypeCode changes, set the
  // editable property for statusCode and versionStatusCode
  checkStatusEditable(): void {
    this._getColumn('statusCode').editable = true;
    this._getColumn('versionStatusCode').editable = true;
    if (
      ['102', '607', '608', '610', '617', '618', '630', '631', '638'].includes(
        this._appTypeCode
      )
    ) {
      this._getColumn('statusCode').editable = false;
      this._getColumn('versionStatusCode').editable = false;
    } else if (
      ['606', '626', '634', '635', '644'].includes(this._appTypeCode)
    ) {
      this._getColumn('statusCode').editable = false;
    }
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.service.insert(newRow, ...this.idArray).subscribe(
      (dto) => {
        let messages = ['Record successfully added.'];
        if (!!dto.messages) {
          messages = [...dto.messages, ...messages];
        }
        this.snackBar.open(messages.join('\n'));
        this._get();
        this.numWaterRightsChanged.emit(null);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot insert new record. ';
        message += errorBody.userMessage || ErrorMessageEnum.POST;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this._displayInsertDialog(newRow);
      }
    );
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertWaterRightComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title} Record`,
        columns: this._getColumn('id').list,
        values: data,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert({
          id: result.waterRightId,
          version: result.version,
        });
      }
    });
  }

  protected _update(updatedRow: any): void {
    this.service
      .update(updatedRow, ...this.idArray, updatedRow.id, updatedRow.version)
      .subscribe(
        (dto) => {
          let messages = ['Record successfully updated.'];
          if (!!dto.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
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

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    const waterRightTypeCode = data.typeCode;
    this.waterRightStatusesService
      .get(this.queryParameters, waterRightTypeCode)
      .subscribe((waterRightStatuses: { results: WaterRightType[] }) => {
        const statuses = waterRightStatuses.results
          .filter((type: WaterRightType) => type.value !== 'N/A')
          .map((type: WaterRightType) => ({
            name: type.description,
            value: type.value,
          }));
        this._getColumn('statusCode').selectArr = statuses;
        this._getColumn('versionStatusCode').selectArr = statuses;

        // Open the dialog
        const dialogRef = this.dialog.open(WaterRightsUpdateDialogComponent, {
          width: this.dialogWidth,
          data: {
            title: `Update ${this.title} Record`,
            columns: this.columns,
            values: data,
            applicationType: this._appTypeCode,
          },
        });
        dialogRef.afterClosed().subscribe((result) => {
          if (result !== null && result !== undefined) {
            this._update({
              ...result,
              id: data.id,
              version: data.version,
            });
          }
        });
      });
  }

  /*
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this.service
      .delete(...this.idArray, this.rows[row].id, this.rows[row].version)
      .subscribe(
        () => {
          this._get();
          this.snackBar.open('Record successfully deleted.');
          this.numWaterRightsChanged.emit(null);
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
  }

  public cellDblClick(data: any): void {
    if (data.columnId === 'version') {
      void this.router.navigate([
        'wris',
        'water-rights',
        this.data.results[data.row].id,
        'versions',
        this.data.results[data.row].version,
      ]);
    } else {
      void this.router.navigate([
        'wris',
        'water-rights',
        this.data.results[data.row].id,
      ]);
    }
  }
}

export interface WaterRightStatusType {
  description: string;
  value: string;
}
