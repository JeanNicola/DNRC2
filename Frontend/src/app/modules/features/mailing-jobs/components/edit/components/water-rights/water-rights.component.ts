import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { ImportWaterRightService } from '../../../../services/import-water-right.service';
import { MailingJobWaterRightsService } from '../../../../services/mailing-job-water-rights.service';
import { WaterRightSelectDialogComponent } from '../water-right-select-dialog/water-right-select-dialog.component';

@Component({
  selector: 'app-water-rights',
  templateUrl: './water-rights.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [MailingJobWaterRightsService, ImportWaterRightService],
})
export class WaterRightsComponent extends BaseCodeTableComponent {
  @Output() waterRightsChanged = new EventEmitter();

  constructor(
    public service: MailingJobWaterRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public router: Router,
    public importService: ImportWaterRightService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterRightTypeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterRightStatusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
    },
  ];
  public title = '';
  public hideEdit = true;
  public searchable = false;

  public searchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];

  public importColumn: ColumnDefinitionInterface[] = [
    {
      columnId: 'file',
      title: 'Excel File',
      type: FormFieldTypeEnum.File,
      validators: [
        WRISValidators.uploadFileType(
          'Excel (.xlsx)',
          'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        ),
        WRISValidators.uploadFileMaxSize('1MB'),
      ],
      fileMimeType:
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    },
  ];

  protected initFunction(): void {
    this._get();
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.service
      .insert(this._buildInsertDto(newRow), ...this._buildInsertIdArray(newRow))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.waterRightsChanged.next();
          this._get();
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
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this._getDeleteService()
      .delete(...this._buildDeleteIdArray(row))
      .subscribe(
        () => {
          this._get();
          this.snackBar.open('Record successfully deleted.');
          this.waterRightsChanged.next();
          this._setInitialButtonFocus();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
  }

  public onFileUpload(data): void {
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: '500px',
      data: {
        title: 'Import Water Rights',
        columns: this.importColumn,
        values: data ?? null,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result != null) {
        this.importWaterRights(result);
      }
    });
  }

  private importWaterRights(data: any): void {
    this.importService.uploadFiles(data, ...this.idArray).subscribe(
      (dto) => {
        if (dto?.userMessage) {
          this.snackBar.open(dto.userMessage);
        }
        this._get();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot insert new record. ';
        message += errorBody.userMessage || ErrorMessageEnum.POST;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this.onFileUpload(data);
      }
    );
  }

  protected _buildInsertDto(dto: any): any {
    return { waterRightId: dto.waterRightId };
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(WaterRightSelectDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getInsertDialogTitle(),
        columns: this.searchColumns,
        values: data,
        mode: DataManagementDialogModes.Insert,
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

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].waterRightId];
  }

  public rowClick(data: any): void {
    void this.router.navigate(['wris', 'water-rights', data.waterRightId]);
  }

  // Override the initial focus
  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}
}
