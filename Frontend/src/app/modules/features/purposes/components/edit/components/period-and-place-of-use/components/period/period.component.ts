import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import * as moment from 'moment';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { EditMessageComponent } from 'src/app/modules/shared/components/dialogs/edit-message/edit-message.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { InsertUpdatePeriodComponent } from '../../../../../create/components/insert-update-period/insert-update-period.component';
import { PurposeDropdownsService } from '../../../edit-header/services/purpose-dropdowns.service';
import { CopyPeriodOfDiversionService } from '../../services/copy-period-of-diversion.service';
import { PeriodService } from '../../services/period.service';
import { PeriodsByPurposeService } from '../../services/periods-by-purpose.service';

@Component({
  selector: 'app-period',
  templateUrl: './period.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './period.component.scss',
  ],
  providers: [
    PeriodService,
    CopyPeriodOfDiversionService,
    PeriodsByPurposeService,
  ],
})
export class PeriodComponent extends BaseCodeTableComponent {
  constructor(
    public service: PeriodsByPurposeService,
    public dropdownService: PurposeDropdownsService,
    public periodsService: PeriodService,
    public copyPeriodOfDiversionService: CopyPeriodOfDiversionService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() dataChanged: EventEmitter<void> = new EventEmitter<void>();
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

  @Input() has650Application = null;
  @Input() waterRightTypeCode = null;
  @Input() waterRightStatusCode = null;
  @Input() versionNumber = null;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = false;

  get idArray(): string[] {
    return super.idArray;
  }

  public primarySortColumn = 'periodBegin';
  public title = 'Period of Use';
  public searchable = false;
  public isInMain = false;
  public executingCopy = false;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'periodBegin',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
      validators: [
        Validators.required,
        WRISValidators.beforeOtherField('endDate', 'End Date'),
      ],
      displayInTable: false,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      validators: [
        Validators.required,
        WRISValidators.afterOtherField('beginDate', 'Begin Date'),
      ],
      displayInTable: false,
    },
    {
      columnId: 'periodEnd',
      title: 'End Date',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'elementOrigin',
      title: 'Period Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'elementOriginDescription',
      title: 'Period Origin',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
  ];
  protected initFunction(): void {
    if (this.has650Application) {
      this.columns = [
        ...this.columns,
        {
          columnId: 'leaseYear',
          title: 'Lease Year',
          type: FormFieldTypeEnum.Select,
        },
      ];
      this._getColumn('leaseYear').selectArr =
        this.dropdownService.leaseYearValues;
    }
  }

  protected _getHelperFunction(data: any): any {
    if (this.executingCopy && !data.get?.results?.length) {
      this.snackBar.open('No Period Of Diversion records found to copy.');
    }

    if (this.rows) {
      this.dataChanged.emit(null);
    }

    this.executingCopy = false;

    return {
      ...data.get,
      results: data.get.results.map((result) => ({
        ...result,
        periodBegin: moment(result.beginDate).format(
          this.has650Application ? 'MM/DD/YYYY' : 'MM/DD'
        ),
        periodEnd: moment(result.endDate).format(
          this.has650Application ? 'MM/DD/YYYY' : 'MM/DD'
        ),
      })),
    };
  }

  protected populateDropdowns(): void {
    // Origins
    let selectArray;

    if (this.waterRightTypeCode !== 'CMPT') {
      selectArray = this.dropdownService.ownerOrigins.filter(
        (option) => option.value !== 'CMPT'
      );
    } else if (
      this.waterRightTypeCode === 'CMPT' &&
      this.waterRightStatusCode !== 'N/A'
    ) {
      selectArray = this.dropdownService.ownerOrigins.filter(
        (option) => option.value === 'CMPT'
      );
    } else {
      selectArray = this.dropdownService.ownerOrigins;
    }
    this._getColumn('elementOrigin').selectArr = selectArray;
  }

  public _displayInsertDialog(data): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdatePeriodComponent, {
      data: {
        title: 'Add New Period Record',
        width: '500px',
        mode: DataManagementDialogModes.Insert,
        columns: this.columns,
        values: {
          ...data,
          has650Application: this.has650Application,
        },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      }
    });
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [originalData.periodId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [this.rows[rowNumber].periodId];
  }

  public _displayEditDialog(data): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdatePeriodComponent, {
      data: {
        title: 'Update Period Record',
        width: '500px',
        mode: DataManagementDialogModes.Update,
        columns: this.columns,
        values: {
          ...data,
          has650Application: this.has650Application,
        },
      },
    });
    // Get the input data and peform the update
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(result, data);
      }
    });
  }

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPOST, canDELETE, and canPUT values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.periodsService.url) && this.canEdit,
      canPUT:
        this.endpointService.canPUT(this.periodsService.url) && this.canEdit,
    };
  }

  // Handle the copyPeriodOfDiversion event
  public copyPeriodOfDiversion(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._doCopyPeriodOfDiversion.bind(this)
    );
  }

  private _doCopyPeriodOfDiversion() {
    const confirmDialog = this.dialog.open(EditMessageComponent, {
      width: '500px',
      data: {
        title: 'Copy POU Data',
        message:
          'This will copy the first Period Of Diversion to Period Of Use. Do you want to continue?',
      },
    });

    confirmDialog.afterClosed().subscribe((r) => {
      if (r === 'continue') {
        this.copyPeriodOfDiversionService
          .insert(null, this.idArray[0])
          .subscribe(
            () => {
              this.executingCopy = true;
              this._get();
            },
            (err: HttpErrorResponse) => {
              const errorBody = err.error as ErrorBodyInterface;
              const message = errorBody.userMessage;
              this.snackBar.open(message);
            }
          );
      }
    });
  }

  protected _getUpdateService(): BaseDataService {
    return this.periodsService;
  }

  protected _getDeleteService(): BaseDataService {
    return this.periodsService;
  }
}
