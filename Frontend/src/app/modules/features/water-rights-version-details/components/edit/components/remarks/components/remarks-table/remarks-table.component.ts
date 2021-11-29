import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import * as moment from 'moment';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { InsertRemarkComponent } from '../insert-remark/insert-remark.component';
import { RemarksForVersionService } from './services/remarks-for-version.service';
import { RemarksService } from './services/remarks.service';

@Component({
  selector: 'app-remarks-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './remarks-table.component.scss',
  ],
  providers: [RemarksForVersionService, RemarksService],
})
export class RemarksTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: RemarksForVersionService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public remarksService: RemarksService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Output() selectRemark = new EventEmitter<any>();
  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined)) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public primarySortColumn = 'remarkCode';
  public title = '';
  public searchable = false;
  public isInMain = false;
  public dblClickableRow = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'remarkCode',
      title: 'Remark Code',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
      displayInEdit: false,
    },
    {
      columnId: 'addedDate',
      title: 'Added Date',
      type: FormFieldTypeEnum.Date,
      displayInInsert: false,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
    },
    {
      columnId: 'remarkCategoryDescription',
      title: 'Remark Category',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'remarkTypeDescription',
      title: 'Remark Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'remarkStatusDescription',
      title: 'Status',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
  ];

  public rowClick(data: any): void {
    this.selectRemark.emit(data.remarkId);
  }

  protected _getHelperFunction(data: any) {
    if (data.get?.results?.length) {
      this.selectRemark.emit(data.get.results[0].remarkId);
    } else {
      this.selectRemark.emit(null);
    }

    return data.get;
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.remarksService.url) && this.canEdit,
      canPUT:
        this.endpointService.canPUT(this.remarksService.url) && this.canEdit,
    };
  }

  protected searchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'remarkCode',
      title: 'Remark Code',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'remarkCategoryDescription',
      title: 'Remark Category',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'remarkTypeDescription',
      title: 'Remark Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'elementTypeDescription',
      title: 'Element Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];

  public formColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'addedDate',
      title: 'Added Date',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
    },
  ];

  protected _buildInsertDto(dto: any) {
    return { remarkCode: dto.remarkCode, addedDate: dto.addedDate };
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

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertRemarkComponent, {
      data: {
        title: `Add New ${this.title}`,
        columns: this.searchColumns,
        formColumns: this.formColumns,
        values: {
          addedDate: moment(),
        },
        waterRightId: this.idArray[0],
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });
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

  protected _getUpdateService(): BaseDataService {
    return this.remarksService;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [originalData.remarkId];
  }

  protected _getDeleteService(): BaseDataService {
    return this.remarksService;
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [this.rows[rowNumber].remarkId];
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
