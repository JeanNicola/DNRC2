import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import {
  WaterRightStatus,
  WaterRightStatusesService,
} from 'src/app/modules/shared/services/water-right-statuses.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { StandardsUpdateService } from '../../../../services/standards-update.service';
import { StandardsService } from '../../../../services/standards.service';
import { FirstVersionService } from '../../../../services/version-first.service';
import { VersionTypesService } from '../../../../services/version-types.service';
import { VersionService } from '../../../../services/version.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

@Component({
  selector: 'app-water-right-versions',
  templateUrl: './water-right-versions.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    VersionService,
    VersionTypesService,
    StandardsService,
    StandardsUpdateService,
    WaterRightStatusesService,
    FirstVersionService,
  ],
})
export class WaterRightVersionsComponent extends BaseCodeTableComponent {
  @Input() set waterRightTypeCode(value: string) {
    this.observables.statuses = new ReplaySubject(1);
    this.statusService
      .get(this.queryParameters, value)
      .subscribe((statuses: { results: WaterRightStatus[] }) => {
        this.firstVersionColumns[0].selectArr = statuses.results
          .filter((type: WaterRightStatus) => type.value !== 'N/A')
          .map((type: WaterRightStatus) => ({
            name: type.description,
            value: type.value,
          }));

        this.observables.statuses.next(statuses);
        this.observables.statuses.complete();
      });
  }
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;

  @Output() reloadEvent: EventEmitter<void> = new EventEmitter<void>();
  constructor(
    public service: VersionService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public router: Router,
    public firstService: FirstVersionService,
    public typeService: VersionTypesService,
    public statusService: WaterRightStatusesService,
    public standardsService: StandardsService,
    public standardUpdateService: StandardsUpdateService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'version',
      title: 'Version #',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'scanned',
      title: 'Scanned',
      type: FormFieldTypeEnum.Checkbox,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'operatingAuthority',
      title: 'Operating Authority',
      type: FormFieldTypeEnum.Date,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'versionTypeCode',
      title: 'Version Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'versionTypeDescription',
      title: 'Version Type',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'versionStatusDescription',
      title: 'Version Status',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'flowRate',
      title: 'Max. Flow Rate',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'volume',
      title: 'Max. Volume',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'acres',
      title: 'Acres',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'enforceablePriorityDate',
      title: 'Enforceable Priority Date',
      type: FormFieldTypeEnum.DateTime,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'standardsUpdated',
      title: 'Standards Updated',
      type: FormFieldTypeEnum.Checkbox,
      displayInInsert: false,
    },
  ];

  public firstVersionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'versionStatusCode',
      title: 'Version Status',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'operatingAuthority',
      title: 'Operating Authority',
      type: FormFieldTypeEnum.Date,
    },
  ];

  public title = '';
  public hideDelete = true;
  public dialogWidth = '400px';
  public disableStandards = true;
  public dblClickableRow = true;

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  protected initFunction(): void {
    this._get();
  }

  public populateDropdowns(): void {
    this.observables.types = new ReplaySubject(1);
    this.typeService
      .get(this.queryParameters)
      .subscribe((types: { results: any[] }) => {
        this._getColumn('versionTypeCode').selectArr = types.results.map(
          (type) => ({
            value: type.value,
            name: type.description,
          })
        );
        this.observables.types.next(types);
        this.observables.types.complete();
      });
  }

  // Handle the OnInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  protected _displayInsertDialog(data: any): void {
    if (this.data.totalElements > 0) {
      // Open the dialog
      const dialogRef = this.dialog.open(InsertDialogComponent, {
        width: this.dialogWidth,
        data: {
          title: 'Add New Version',
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
    } else {
      const dialogRef = this.dialog.open(InsertDialogComponent, {
        width: this.dialogWidth,
        data: {
          title: 'Add New Version',
          columns: this.firstVersionColumns,
          values: data,
        },
      });

      // Get the input data and peform the insert
      dialogRef.afterClosed().subscribe((result) => {
        if (result !== null && result !== undefined) {
          this._insertFirst(result);
        }
      });
    }
  }

  protected _insertFirst(newRow: any): void {
    this.firstService
      .insert(this._buildInsertDto(newRow), ...this._buildInsertIdArray(newRow))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this._get();
          this.reloadEvent.next();
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

  protected _getHelperFunction(data: any) {
    const dataGet = { ...data.get };
    dataGet.results.map((version) => {
      version.disableEdit = !version.standardsUpdated;
    });
    this.disableStandards =
      dataGet.anyStandardsApplied || dataGet.totalElements == 0;

    // Reset permissions based on data
    let canEdit = true;
    if (
      !this.isEditableIfDecreed &&
      ['ORIG', 'REXM', 'POST', 'SPPD'].includes(dataGet.versionTypeCode) &&
      this.isDecreed
    ) {
      canEdit = false;
    }

    setTimeout(() => {
      this.permissions = {
        ...this.permissions,
        canPUT: this.endpointService.canPUT(this.service.url) && canEdit,
      };
    }, 0);

    return dataGet;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.version];
  }

  // Handle the onEdit event
  public onEdit(data): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, data)
    );
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Version',
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

  // Handle the onStandards event
  protected onStandards(): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._runStandards.bind(this)
    );
  }

  private _runStandards(): void {
    this.standardsService.insert({}, ...this.idArray).subscribe(
      () => {
        this.snackBar.open('Run Standards is complete');
        this._get();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        const message = errorBody.userMessage;
        this.snackBar.open(message);
      }
    );
  }

  protected _update(updatedRow: any, originalData?: any): void {
    this.standardUpdateService
      .update(updatedRow, ...this._buildEditIdArray(updatedRow, originalData))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully updated.'];
          if (!!dto?.messages) {
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

  public onRowDoubleClick(data: any): void {
    void this.router.navigate([
      'wris',
      'water-rights',
      data.waterRightId,
      'versions',
      data.version,
    ]);
  }
}
