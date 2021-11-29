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
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { DitchesService } from '../../services/ditches.service';
import { DitchSelectDialogComponent } from '../ditch-select-dialog/ditch-select-dialog.component';

@Component({
  selector: 'app-pod-main-update-dialog',
  templateUrl: './pod-main-update-dialog.component.html',
  styleUrls: ['./pod-main-update-dialog.component.scss'],
  providers: [DitchesService],
})
export class PodMainUpdateDialogComponent
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
    public ditchService: DitchesService
  ) {
    super(dialogRef, data);
  }

  public originColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'podOriginCode',
      title: 'POD Origin',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
  ];

  public firstMeansColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'meansOfDiversionCode',
      title: 'Means of Diversion',
      formWidth: 400,
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
  ];
  public secondMeansColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'diversionTypeCode',
      title: 'Ditch Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ditchId',
      title: 'Diversion/Ditch Name',
      type: FormFieldTypeEnum.Select,
      formWidth: 300,
    },
  ];
  public thirdMeansColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'podTypeCode',
      title: 'POD Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'transitory',
      title: 'Transitory',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];
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
  ];
  public secondLegalColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'section',
      title: 'Sec',
      type: FormFieldTypeEnum.Input,
      formWidth: 120,
      validators: [WRISValidators.isNumber(2, 0), Validators.required],
    },
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
      formWidth: 350,
      validators: [Validators.required],
    },
  ];
  public thirdLegalColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'xCoordinate',
      title: 'X Coordinate',
      type: FormFieldTypeEnum.Input,
      validators: [
        WRISValidators.isNumber(7, 5),
        WRISValidators.requireOtherFieldIfNonNull('yCoordinate'),
      ],
    },
    {
      columnId: 'yCoordinate',
      title: 'Y Coordinate',
      type: FormFieldTypeEnum.Input,
      validators: [
        WRISValidators.isNumber(7, 5),
        WRISValidators.requireOtherFieldIfNonNull('xCoordinate'),
      ],
    },
    {
      columnId: 'modified',
      title: 'Modified in This Change',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];
  public changedDitch = false;

  protected initFunction(): void {
    // populating the dropdowns by looking for the columnId associated with each selectArray
    for (const columnId in this.data.selectArrays) {
      const column = this._getColumn(columnId);
      if (column) {
        column.selectArr = this.data.selectArrays[columnId];
      }
    }

    if (this.data.values?.ditchName) {
      this._getColumn('ditchId').selectArr = [
        {
          name: this.data.values.ditchName,
          value: this.data.values.ditchId,
        },
      ];
    }
  }

  public ngAfterViewInit(): void {
    setTimeout(() => {
      this.formGroup.get('diversionTypeCode').disable();
      this.formGroup.get('ditchId').disable();
    });
  }

  public onDitchInsert(values: any): void {
    const dialogRef = this.dialog.open(DitchSelectDialogComponent, {
      width: '650px',
      data: {
        title: 'Pick a Source',
        columns: [],
        values,
        selectArrays: {
          ...this.data.selectArrays,
        },
        canInsert: this.endpointService.canPOST(this.ditchService.url),
      },
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      // searched
      if (result?.ditchId) {
        this._getColumn('ditchId').selectArr = [
          {
            name: result.ditchName,
            value: result.ditchId,
          },
        ];

        this.formGroup.get('ditchId').setValue(result.ditchId);
        this.formGroup.get('diversionTypeCode').setValue(result.ditchType);
        this.changedDitch = true;
      } else if (result?.ditchName) {
        // created
        this.ditchService.insert(result).subscribe(
          (savedDitch) => {
            const messages = ['Record successfully added.'];
            this.snackBar.open(messages.join('\n'));
            this._getColumn('ditchId').selectArr = [
              {
                name: savedDitch.ditchName,
                value: savedDitch.ditchId,
              },
            ];
            this.formGroup.get('ditchId').setValue(savedDitch.ditchId);
            this.formGroup
              .get('diversionTypeCode')
              .setValue(savedDitch.ditchType);
            this.changedDitch = true;
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error;
            let message = 'Cannot insert new record. ';
            message += errorBody.userMessage || ErrorMessageEnum.POST;
            this.snackBar.open(message);
            this.onDitchInsert(result);
          }
        );
      }
    });
  }

  public save(): void {
    const ditches = this._getColumn('ditchId').selectArr;
    const saveObject = {
      ...this.formGroup.getRawValue(),
      ditchName: ditches != null ? ditches[0].name : undefined,
    };
    saveObject.transitory = saveObject.transitory ?? false;
    saveObject.modified = saveObject.modified ?? false;
    delete saveObject.diversionTypeCode;
    this.dialogRef.close(saveObject);
  }

  private _getColumn(columnId: string) {
    return [
      ...this.originColumns,
      ...this.firstMeansColumns,
      ...this.secondMeansColumns,
      ...this.thirdMeansColumns,
      ...this.firstLegalColumns,
      ...this.secondLegalColumns,
      ...this.thirdLegalColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }
}
