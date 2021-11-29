import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, Component, Inject } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { SourceService } from '../../services/source.service';
import { SourceSelectDialogComponent } from '../source-select-dialog/source-select-dialog.component';

@Component({
  selector: 'app-insert-pod-dialog',
  templateUrl: './insert-pod-dialog.component.html',
  styleUrls: ['./insert-pod-dialog.component.scss'],
  providers: [SourceService],
})
export class InsertPodDialogComponent
  extends InsertDialogComponent
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

  public legalFormGroup: FormGroup = new FormGroup({});
  public sourceFormGroup: FormGroup = new FormGroup({});
  public firstLegalColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'governmentLot',
      title: 'Govt Lot',
      type: FormFieldTypeEnum.Input,
      formWidth: 160,
      validators: [WRISValidators.isNumber(3, 0)],
    },
    {
      columnId: 'description40',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      formWidth: 100,
    },
    {
      columnId: 'description80',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      formWidth: 100,
    },
    {
      columnId: 'description160',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      formWidth: 100,
    },
    {
      columnId: 'description320',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      formWidth: 100,
    },
    {
      columnId: 'section',
      title: 'Sec',
      type: FormFieldTypeEnum.Input,
      formWidth: 120,
      validators: [WRISValidators.isNumber(2, 0), Validators.required],
    },
  ];
  public secondLegalColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'township',
      title: 'Twp',
      type: FormFieldTypeEnum.Input,
      formWidth: 130,
      validators: [WRISValidators.isNumber(3, 1), Validators.required],
    },
    {
      columnId: 'townshipDirection',
      title: 'N/S',
      type: FormFieldTypeEnum.Select,
      formWidth: 80,
      validators: [Validators.required],
    },
    {
      columnId: 'range',
      title: 'Rge',
      type: FormFieldTypeEnum.Input,
      formWidth: 110,
      validators: [WRISValidators.isNumber(3, 1), Validators.required],
    },
    {
      columnId: 'rangeDirection',
      title: 'E/W',
      type: FormFieldTypeEnum.Select,
      formWidth: 80,
      validators: [Validators.required],
    },
    {
      columnId: 'countyId',
      title: 'County',
      type: FormFieldTypeEnum.Autocomplete,
      formWidth: 230,
      validators: [Validators.required],
    },
  ];
  public originColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'podOriginCode',
      title: 'POD Origin',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
  ];
  public sourceColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'sourceOriginCode',
      title: 'Source Origin',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'unnamedTributary',
      title: 'Unnamed Tributary',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'sourceId',
      title: 'Source/Fork *',
      type: FormFieldTypeEnum.Select,
      width: 150,
      validators: [Validators.required],
      editable: false,
    },
    {
      columnId: 'majorTypeCode',
      title: 'Major Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
  ];
  public meansColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'meansOfDiversionCode',
      title: 'Means of Diversion',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'podTypeCode',
      title: 'POD Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
  ];
  public initialized = false;
  public hasSource = false;

  public ngAfterViewInit(): void {
    // this to prevent ExpressionChangedAfterItHasBeenCheckedErrorExpress
    setTimeout(() => {
      this.initialized = true;
      this.sourceFormGroup.get('sourceId').disable();
    });
  }

  protected initFunction(): void {
    if (this.data.values?.sourceName) {
      this._getColumn('sourceId').selectArr = [
        {
          name: this.data.values.sourceName,
          value: this.data.values.sourceId,
        },
      ];
    }
    if (this.data.values != null) {
      this.data.values.majorTypeCode = this.data.values.majorTypeCode ?? 'G'; // Groundwater
      this.data.values.podTypeCode = this.data.values.podTypeCode ?? 'PRIM'; // Primary
      this.data.values.sourceOriginCode =
        this.data.values.sourceOriginCode ?? 'ISSU'; // As Issued
    }
    // populating the dropdowns by looking for the columnId associated with each selectArray
    for (const columnId in this.data.selectArrays) {
      const column = this._getColumn(columnId);
      if (
        Object.prototype.hasOwnProperty.call(
          this.data.selectArrays,
          columnId
        ) &&
        column
      ) {
        column.selectArr = this.data.selectArrays[columnId];
      }
    }
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
        this.sourceFormGroup.get('sourceId').setValue(result.sourceId);
        this.hasSource = true;
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
            this.sourceFormGroup.get('sourceId').setValue(savedSource.sourceId);
            this.hasSource = true;
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

  private _getColumn(columnId: string) {
    return [
      ...this.firstLegalColumns,
      ...this.secondLegalColumns,
      ...this.originColumns,
      ...this.sourceColumns,
      ...this.meansColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }

  public save(): void {
    const saveObject = {
      ...this.legalFormGroup.getRawValue(),
      ...this.sourceFormGroup.getRawValue(),
      ...this.formGroup.getRawValue(),
      sourceName: this._getColumn('sourceId').selectArr[0].name,
    };
    saveObject.unnamedTributary = saveObject.unnamedTributary ?? false;
    this.dialogRef.close(saveObject);
  }

  public stepping(step: StepperSelectionEvent): void {}
}
