import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, Component, Inject } from '@angular/core';
import { Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { SourceService } from '../../services/source.service';
import { SourceSelectDialogComponent } from '../source-select-dialog/source-select-dialog.component';

@Component({
  selector: 'app-pod-source-update-dialog',
  templateUrl: './pod-source-update-dialog.component.html',
  styleUrls: ['./pod-source-update-dialog.component.scss'],
  providers: [SourceService],
})
export class PodSourceUpdateDialogComponent
  extends UpdateDialogComponent
  implements AfterViewInit
{
  constructor(
    public dialogRef: MatDialogRef<any>,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      selectArrays: { [key: string]: SelectionInterface[] };
    },
    public snackBar: SnackBarService,
    public endpointService: EndpointsService,
    public sourceService: SourceService
  ) {
    super(dialogRef, data);
  }

  public firstColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'sourceOriginCode',
      title: 'Source Origin',
      type: FormFieldTypeEnum.Select,
      formWidth: 300,
      validators: [Validators.required],
    },
    {
      columnId: 'unnamedTributary',
      title: 'Unnamed Tributary',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];
  public secondColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'sourceId',
      title: 'Source/Fork *',
      type: FormFieldTypeEnum.Select,
      formWidth: 350,
    },
  ];
  public thirdColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'majorTypeCode',
      title: 'Major Type',
      type: FormFieldTypeEnum.Select,
      formWidth: 300,
      validators: [Validators.required],
    },
    {
      columnId: 'minorTypeCode',
      title: 'Minor Type',
      type: FormFieldTypeEnum.Select,
    },
  ];
  public changedSource = false;

  protected initFunction(): void {
    if (this.data.values?.sourceName) {
      this._getColumn('sourceId').selectArr = [
        {
          name: this.data.values.sourceName,
          value: this.data.values.sourceId,
        },
      ];
    }
    // populating the dropdowns by looking for the columnId associated with each selectArray
    for (const columnId in this.data.selectArrays) {
      const column = this._getColumn(columnId);
      if (column) {
        column.selectArr = this.data.selectArrays[columnId];
      }
    }
  }

  public ngAfterViewInit(): void {
    setTimeout(() => {
      this.formGroup.get('sourceId').disable();
    });
  }

  public onSourceInsert(values: any): void {
    const dialogRef = this.dialog.open(SourceSelectDialogComponent, {
      width: '500px',
      data: {
        title: 'Create New Source',
        columns: [],
        values,
        canInsert: this.endpointService.canPOST(this.sourceService.url),
      },
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      // searched
      if (result?.sourceId) {
        this._getColumn('sourceId').selectArr = [
          {
            name: result.sourceName,
            value: result.sourceId,
          },
        ];
        this.formGroup.get('sourceId').setValue(result.sourceId);
        this.changedSource = true;
      } else if (result?.sourceName) {
        // created
        this.sourceService.insert(result).subscribe(
          (savedSource) => {
            const messages = ['Record successfully added.'];
            this.snackBar.open(messages.join('\n'));
            this._getColumn('sourceId').selectArr = [
              {
                name: savedSource.sourceName,
                value: savedSource.sourceId,
              },
            ];
            this.formGroup.get('sourceId').setValue(savedSource.sourceId);
            this.changedSource = true;
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error as ErrorBodyInterface;
            let message = 'Cannot insert new record. ';
            message += errorBody.userMessage || ErrorMessageEnum.POST;
            this.snackBar.open(message);
            this.onSourceInsert(result);
          }
        );
      }
    });
  }
  public save(): void {
    const sources = this._getColumn('sourceId').selectArr;
    const saveObject = {
      ...this.formGroup.getRawValue(),
      source: sources != null ? sources[0].name : undefined,
    };
    saveObject.unnamedTributary = saveObject.unnamedTributary ?? false;
    this.dialogRef.close(saveObject);
  }

  private _getColumn(columnId: string) {
    return [
      ...this.firstColumns,
      ...this.secondColumns,
      ...this.thirdColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }
}
