import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { BasinsService } from 'src/app/modules/shared/services/basins.service';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { ErrorMessageEnum } from '../../../code-tables/enums/error-message.enum';
import { GeocodeService } from '../../services/geocode.service';
import { WaterRightByVersionService } from '../../services/water-right-by-version.service';
import { WaterRightCreationTypesService } from '../../services/water-right-creation-types.service';
import { WaterRightTypesService } from '../../services/water-right-types.service';
import { WaterRightService } from '../../services/water-right.service';
import { VersionDialogComponent } from './components/version-dialog/version-dialog.component';
import { WaterRightInsertDialogComponent } from './components/water-right-insert-dialog/water-right-insert-dialog.component';
import { WaterRightSearchDialogComponent } from './components/water-right-search-dialog/water-right-search-dialog.component';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: [
    '../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    GeocodeService,
    WaterRightCreationTypesService,
    BasinsService,
    WaterRightService,
    WaterRightTypesService,
    WaterRightByVersionService,
  ],
})
export class SearchComponent extends BaseCodeTableComponent {
  constructor(
    public service: WaterRightService,
    public geocodeService: GeocodeService,
    public versionService: WaterRightByVersionService,
    private waterRightCreationTypesService: WaterRightCreationTypesService,
    private basinsService: BasinsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private waterRightTypesService: WaterRightTypesService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public data: GeocodeDataPageInterface<any>;
  public title = 'Water Rights';
  public hideActions = true;
  public dblClickableRow = true;
  public clickableRow = false;
  public dataMessage = 'Search for or Create a New Water Right';
  private waterRightTypes;
  public primarySortColumn = 'waterRightNumber';

  public columns: ColumnDefinitionInterface[] = [];
  public waterColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightId',
      title: 'Water Right Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Autocomplete,
      validators: [Validators.required],
    },
    {
      columnId: 'subBasin',
      title: 'Sub Basin',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'typeCode',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Select,
      displayInInsert: false,
    },
    {
      columnId: 'statusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'contactIds',
      title: 'Owners',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
      displayInSearch: false,
      displayInTable: false,
      list: [
        {
          columnId: 'contactId',
          title: 'Contact ID',
          type: FormFieldTypeEnum.Input,
        },
        {
          columnId: 'lastName',
          title: 'Last Name',
          type: FormFieldTypeEnum.Input,
          displayInTable: false,
        },
        {
          columnId: 'firstName',
          title: 'First Name',
          type: FormFieldTypeEnum.Input,
          displayInTable: false,
        },
        {
          columnId: 'name',
          title: 'Name',
          type: FormFieldTypeEnum.Input,
          displayInSearch: false,
        },
      ],
    },
  ];
  public versionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightId',
      title: 'Water Right Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Autocomplete,
      validators: [Validators.required],
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Select,
      displayInInsert: false,
    },
    {
      columnId: 'statusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'more',
      title: 'Versions',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      showCounter: true,
      counterRef: 'versionCount',
      noSort: true,
    },
  ];

  public conservationColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightId',
      title: 'Water Right Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'conservationDistrictNumber',
      title: 'Conservation District #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'conservationDistrictDate',
      title: 'Conservation District Internal Priority Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'waterReservationId',
      title: 'Water Reservation #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
  ];

  public geocodeColumn: ColumnDefinitionInterface = {
    columnId: 'geocodeId',
    title: 'Geocode',
    type: FormFieldTypeEnum.Input,
    width: 210,
  };

  public geocodeColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightId',
      title: 'Water Right Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'typeCode',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'statusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'valid',
      title: 'Valid',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'comments',
      title: 'Comments',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected _getService(): BaseDataService {
    let service: BaseDataService;
    const oldColumns = this.columns;
    if (this.queryParameters.filters.geocodeId != null) {
      service = this.geocodeService;
      this.columns = this.geocodeColumns;
      const geocodeId = this.queryParameters.filters.geocodeId.replace(
        /\-/g,
        ''
      );

      this.idArray = [geocodeId];
    } else if (
      this.queryParameters.filters.conservationDistrictNumber != null ||
      this.queryParameters.filters.waterReservationId != null
    ) {
      service = this.service;
      this.columns = this.conservationColumns;
      this.idArray = [];
    } else if (
      this.queryParameters.filters.waterRightId != null ||
      this.queryParameters.filters.version != null ||
      this.queryParameters.filters.versionType != null
    ) {
      service = this.versionService;
      this.columns = this.versionColumns;
      this.idArray = [];
    } else {
      service = this.service;
      this.columns = this.waterColumns;
      this.idArray = [];
    }

    // pull up a loading screen if we're changing
    // the table columns
    if (this.columns !== oldColumns) {
      this.data = null;
      this.rows = null;
    }
    return service;
  }

  protected populateDropdowns(): void {
    this.observables.types = new ReplaySubject(1);
    this.waterRightCreationTypesService
      .get(this.queryParameters)
      .subscribe((waterRightTypes) => {
        this._getColumn('typeCode').selectArr = waterRightTypes.results.map(
          (type: { value: string; description: string }) => ({
            name: type.description,
            value: type.value,
          })
        );
        this.observables.types.next(waterRightTypes);
        this.observables.types.complete();
      });
    this.observables.basins = new ReplaySubject(1);
    this.basinsService.getAll().subscribe((basins) => {
      this._getColumn('basin').selectArr = basins.results.map((basin) => ({
        value: basin.code,
        name: `${basin.code} - ${basin.description}`,
      }));
      this._getColumn('basin').validators.push(
        WRISValidators.matchToSelectArray(this._getColumn('basin').selectArr)
      );
      this.observables.basins.next(basins);
      this.observables.basins.complete();
    });
    this.observables.types = new ReplaySubject(1);
    this.waterRightTypesService.get(this.queryParameters).subscribe((types) => {
      this.waterRightTypes = types.results.map((type) => ({
        name: type.description,
        value: type.value,
      }));
      this.observables.types.next(types);
      this.observables.types.complete();
    });
  }

  protected _getHelperFunction(data?: any): any {
    if (data.get?.totalElements === 1) {
      void this.router.navigate([
        'wris',
        'water-rights',
        data.get.results[0].waterRightId,
      ]);
    }
    return {
      ...data.get,
    };
  }

  protected displaySearchDialog(): void {
    const dialogRef = this.dialog.open(WaterRightSearchDialogComponent, {
      data: {
        title: `Search ${this.title}`,
        columns: [],
        values: {},
        typeCodes: this.waterRightTypes,
      },
    });

    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result !== null && result !== undefined) {
        this.queryParameters.filters = { ...result };
        this.queryParameters.pageNumber = 1;
        this.queryParameters.pageSize = 25;
        this.queryParameters.sortColumn = 'waterRightNumber';
        this.queryParameters.sortDirection = 'asc';
        this._get();
      } else {
        this.setInitialFocus();
      }
    });
  }

  protected _insert(newRow: any): void {
    this.service.insert(newRow, ...this.idArray).subscribe(
      (dto) => {
        let messages = ['Record successfully added.'];
        if (!!dto.messages) {
          messages = [...dto.messages, ...messages];
        }
        this.snackBar.open(messages.join('\n'));
        void this.router.navigate(['wris', 'water-rights', dto.waterRightId]);
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

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(WaterRightInsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Create New Water Right',
        columns: this.waterColumns,
        values: data,
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

  public cellClick(data: any): void {
    if (
      data?.columnId === 'more' &&
      this.rows[data.row].waterRightId != undefined
    ) {
      this.dialog.open(VersionDialogComponent, {
        data: {
          waterRightId: this.rows[data.row].waterRightId,
          basin: this.rows[data.row].basin,
          waterRightNumber: this.rows[data.row].waterRightNumber,
          ext: this.rows[data.row].ext,
        },
      });
    }
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'water-rights', data.waterRightId]);
  }

  protected _getColumn(columnId: string): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = this.waterColumns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }
}

export interface GeocodeDataPageInterface<T> extends DataPageInterface<T> {
  formattedGeocode: string;
  results: T[] | any;
}
