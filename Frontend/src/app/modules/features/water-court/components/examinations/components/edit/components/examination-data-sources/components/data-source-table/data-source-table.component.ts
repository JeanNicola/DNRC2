import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Observable, ReplaySubject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataSourceTypesService } from '../../services/data-source-types.service';
import { DataSourceService } from '../../services/data-source.service';
import { PhotoTypesService } from '../../services/photo-types.service';
import { WaterSurveyCountiesService } from '../../services/water-survey-counties.service';
import { AerialPhotosTableComponent } from '../aerial-photos-table/aerial-photos-table.component';
import { DataSourceTypes } from '../constants/DataSourceTypes';
import { CreateDataSourceDialogComponent } from './components/create-data-source-dialog/create-data-source-dialog.component';

@Component({
  selector: 'app-data-source-table',
  templateUrl: './data-source-table.component.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    DataSourceService,
    PhotoTypesService,
    DataSourceTypesService,
    WaterSurveyCountiesService,
  ],
})
export class DataSourceTableComponent
  extends BaseCodeTableComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    public service: DataSourceService,
    public dataSourceTypesService: DataSourceTypesService,
    public photoTypesService: PhotoTypesService,
    public waterSurveyCountiesService: WaterSurveyCountiesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() onDataSourceSelect = new EventEmitter<any>();
  @Input() reloadDataObservable: Observable<any> = null;
  private reloadData$: Subscription;
  private selectedDataSourceIndex = 0;

  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public isInMain = false;
  public hideEdit = true;
  public title = 'Data Sources';
  public primarySortColumn = 'sourceTypeDescription';
  public searchable = false;
  public keepFocus = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'sourceTypeDescription',
      title: 'Data Source',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'totalExaminedAcres',
      title: 'Total Examined Acres',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
      noSort: true,
    },
    {
      columnId: 'sourceType',
      title: 'Data Source',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
  ];

  public ngAfterViewInit(): void {
    if (this.reloadDataObservable) {
      this.reloadData$ = this.reloadDataObservable.subscribe(() => {
        this.keepFocus = true;
        this._get();
      });
    }
  }

  public ngOnDestroy() {
    if (this.reloadData$) {
      this.reloadData$.unsubscribe();
    }
  }

  // Usgs Quad Map properties
  private usgsColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'name',
      title: 'USGS Quad Map Name',
      type: FormFieldTypeEnum.Input,
    },
  ];

  // Aerial Photo properties
  public aerialPhotoColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'typeCode',
      title: 'Photo Source',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'aerialPhotoNumber',
      title: 'Photo Number',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(35)],
    },
    {
      columnId: 'aerialPhotoDate',
      title: 'Photo Date',
      type: FormFieldTypeEnum.Input,
      validators: [
        Validators.required,
        Validators.maxLength(10),
        AerialPhotosTableComponent.aerialPhotoDateValidator(),
      ],
    },
  ];

  // Water Survey properties
  public fieldInvestigationColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'investigationDate',
      title: 'Field Investigation Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  // Water Survey properties
  public waterSurveyColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'surveyId',
      title: 'County',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
  ];

  protected initFunction() {
    this.idArray = [this.route.snapshot.params.examinationId];
    this._get();
  }

  protected _getHelperFunction(data: any) {
    // We need to Update Total Examined Acres field whenever the parcels change.
    // If one of the parcels change we set keepFocus = TRUE
    // On the other hand if we sort the table keepFocus will be FALSE, and we must reset the focus
    if (data.get.results?.length && !this.keepFocus) {
      this.onDataSourceSelect.emit(data.get.results[0]);
      this.selectedDataSourceIndex = 0;
    } else if (!this.keepFocus) {
      // Table is empty
      this.onDataSourceSelect.emit(null);
      this.selectedDataSourceIndex = null;
    }
    // Set focus back to the previous index
    if (this.keepFocus && this.selectedDataSourceIndex != null) {
      const previousIndex = this.selectedDataSourceIndex;
      this.selectedDataSourceIndex = null;
      setTimeout(() => {
        // Restore index
        this.selectedDataSourceIndex = previousIndex;
      });
    }
    this.keepFocus = false;

    return data.get;
  }

  protected populateDropdowns(): void {
    // Data Source Types
    this.observables.dataSourceTypes = new ReplaySubject(1);
    this.dataSourceTypesService.get(this.queryParameters).subscribe((types) => {
      const selectArray = types.results.map(
        (type: { value: string; description: string }) => ({
          name: type.description,
          value: type.value,
        })
      );
      this._getColumn('sourceType').selectArr = selectArray;

      this.observables.dataSourceTypes.next(types);
      this.observables.dataSourceTypes.complete();
    });
    // Photo Types
    this.observables.photoTypes = new ReplaySubject(1);
    this.photoTypesService.get(this.queryParameters).subscribe((photoTypes) => {
      const selectArray = photoTypes.results.map(
        (type: { value: string; description: string }) => ({
          name: type.description,
          value: type.value,
        })
      );
      this._getColumn('typeCode').selectArr = selectArray;

      this.observables.photoTypes.next(photoTypes);
      this.observables.photoTypes.complete();
    });

    // Water Survey Counties
    this.observables.counties = new ReplaySubject(1);
    this.waterSurveyCountiesService
      .get(this.queryParameters)
      .subscribe((counties) => {
        const selectArray = counties.results.map(
          (county: { name: string; surveyId: string; yr: string }) => ({
            name: `${county.name} - ${county.yr}`,
            value: county.surveyId,
          })
        );

        this._getColumn('surveyId').selectArr = selectArray;

        this.observables.counties.next(counties);
        this.observables.counties.complete();
      });
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    const invalidItems = this.data.results.map((item) => item.sourceType);
    const columnsCopy = this.columns.map((column) => {
      if (column.columnId === 'sourceType') {
        // Create a copy from the column
        column = { ...column, selectArr: column.selectArr.slice() };
        // Filter duplicated items that aren't of FLD type
        column.selectArr = column.selectArr.filter((item) => {
          if (
            item.value !== DataSourceTypes.FIELD_INVESTIGATION &&
            invalidItems.includes(item.value)
          ) {
            return false;
          }
          return true;
        });
      }
      return column;
    });
    // Open the dialog
    const dialogRef = this.dialog.open(CreateDataSourceDialogComponent, {
      width: '500px',
      data: {
        title: this.getInsertDialogTitle(),
        columns: columnsCopy,
        usgsColumns: this.usgsColumns,
        aerialPhotoColumns: this.aerialPhotoColumns,
        waterSurveyColumns: this.waterSurveyColumns,
        fieldInvestigationColumns: this.fieldInvestigationColumns,
        values: data,
        validators: this.validators,
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

  public onInsert(): void {
    this._displayInsertDialog(null);
  }

  public onDataSourceClick(data: any, index: number): void {
    this.selectedDataSourceIndex = index;
    this.onDataSourceSelect.emit(data);
  }

  protected getInsertDialogTitle() {
    return 'Add New Data Source Record';
  }

  protected getEditDialogTitle() {
    return `Update Data Source Record`;
  }

  protected _getColumn(columnId: string) {
    return [
      ...this.columns,
      ...this.aerialPhotoColumns,
      ...this.usgsColumns,
      ...this.waterSurveyColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [this.route.snapshot.params.examinationId, originalData.pexmId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [
      this.route.snapshot.params.examinationId,
      this.rows[rowNumber].pexmId,
    ];
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
