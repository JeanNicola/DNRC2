import { AfterViewInit, Component, Inject } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { DitchesService } from '../../services/ditches.service';

@Component({
  selector: 'app-ditch-select-dialog',
  templateUrl: './ditch-select-dialog.component.html',
  styleUrls: ['./ditch-select-dialog.component.scss'],
  providers: [DitchesService],
})
export class DitchSelectDialogComponent
  extends SearchSelectDialogComponent
  implements AfterViewInit
{
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      selectArrays: { [key: string]: SelectionInterface[] };
      canInsert: Boolean;
    },
    public dialogRef: MatDialogRef<SearchSelectDialogComponent>,
    public service: DitchesService
  ) {
    super(data, dialogRef, service);
  }

  public searchTitle = 'Search';
  public selectTitle = 'Pick a Ditch';
  public addTooltip = 'Confirm';

  public mainColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'ditchName',
      title: 'Diversion/Ditch Name',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(100)],
      formWidth: 550,
    },
    {
      columnId: 'ditchTypeCode',
      title: 'Ditch Type',
      type: FormFieldTypeEnum.Select,
      displayInSearch: false,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'legalLandDescription',
      title: 'Legal Land Description',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      displayInInsert: false,
    },
  ];
  public searchColumns = this.mainColumns.filter(
    (item) => item?.displayInSearch ?? true
  );
  public displayFields = this.mainColumns.filter(
    (item) => item?.displayInTable ?? true
  );
  public createFields = this.mainColumns.filter(
    (item) => item?.displayInInsert ?? true
  );

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

  public ditchCharacteristicColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'capacity',
      title: 'Capacity (CFS)',
      formWidth: 150,
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
    },
    {
      columnId: 'depth',
      title: 'Depth (Feet)',
      formWidth: 150,
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(2, 1)],
    },
    {
      columnId: 'width',
      title: 'Width (Feet)',
      formWidth: 150,
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(2, 1)],
    },
  ];
  public secondDitchCharacteristicColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'length',
      title: 'Length (Feet)',
      formWidth: 150,
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(3, 1)],
    },
    {
      columnId: 'slope',
      title: 'Slope (%)',
      formWidth: 150,
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(2, 2)],
    },
    {
      columnId: 'valid',
      title: 'Valid Diversion',
      formWidth: 150,
      type: FormFieldTypeEnum.Checkbox,
    },
  ];
  public searchMode = DataManagementDialogModes.Search;
  public mode = DataManagementDialogModes.Insert;
  public formGroup: FormGroup = new FormGroup({});
  public createFormGroup: FormGroup = new FormGroup({});
  public initialized = false;
  public tabIndex = Number(this.data.values != null);

  public ngAfterViewInit(): void {
    setTimeout(() => {
      this.initialized = true;
    });
    // populating the dropdowns by looking for the columnId associated with each selectArray
    for (const columnId in this.data.selectArrays) {
      const column = this._getColumn(columnId);
      if (column) {
        column.selectArr = this.data.selectArrays[columnId];
      }
    }
  }

  public clearOtherTab(event: any): void {
    if (event.index === 0) {
      this.createFormGroup.reset();
    } else {
      this.row = null;
      this.stepper.previous();
    }
  }

  public save(): void {
    if (this.row) {
      this.dialogRef.close(this.row);
    } else {
      this.dialogRef.close(this.createFormGroup.getRawValue());
    }
  }

  private _getColumn(columnId: string) {
    return [
      ...this.mainColumns,
      ...this.firstLegalColumns,
      ...this.secondLegalColumns,
      ...this.ditchCharacteristicColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }
}
