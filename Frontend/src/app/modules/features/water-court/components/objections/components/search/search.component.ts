import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Moment } from 'moment';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { SearchDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/search-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { YesNoValuesService } from 'src/app/modules/shared/services/yes-no-values.service';
import { CreateComponent } from '../create/create.component';
import { ObjectionStatusesService } from './services/objection-statuses.service';
import { ObjectionsSearchService } from './services/objections-search.service';
import { OjectionTypesService } from './services/ojection-types.service';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    ObjectionsSearchService,
    YesNoValuesService,
    OjectionTypesService,
    ObjectionStatusesService,
  ],
})
export class SearchComponent extends BaseCodeTableComponent {
  constructor(
    public service: ObjectionsSearchService,
    private yesNoValuesService: YesNoValuesService,
    private ojectionTypesService: OjectionTypesService,
    private objectionStatusesService: ObjectionStatusesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Objections/Counter Objections';
  public dialogWidth = '450px';
  public primarySortColumn = 'objectionId';
  public sortDirection = 'asc';
  public hideActions = true;
  public hideEdit = true;
  public hideDelete = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'objectionId',
      title: 'Obj #',
      type: FormFieldTypeEnum.Input,
      searchValidators: [Validators.maxLength(10)],
    },
    {
      columnId: 'objectionType',
      title: 'Obj Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'objectionTypeDescription',
      title: 'Obj Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'filedDate',
      title: 'Filed Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'objectionLate',
      title: 'Obj Late',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'objectionStatus',
      title: 'Obj Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'objectionStatusDescription',
      title: 'Obj Status',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'basin',
      title: 'Decree Basin',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      searchValidators: [Validators.maxLength(4)],
    },
    {
      columnId: 'completeBasin',
      title: 'Decree Basin',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];

  public createColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'objectionType',
      title: 'Objection Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'filedDate',
      title: 'Filed Date',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required],
    },
    {
      columnId: 'objectionStatus',
      title: 'Status',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'objectionLate',
      title: 'Late',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
  ];

  public initFunction(): void {
    this.dataMessage = 'Search for or Create a New Objection/Counter Objection';
  }

  protected _getHelperFunction(data?: any): any {
    if (data.get?.results?.length === 1 && data.get.currentPage === 1) {
      this.redirectToObjectionsEditScreen(data.get.results[0].objectionId);
    }
    return data.get;
  }

  public populateDropdowns(): void {
    // YES/NO values
    this.observables.yesNoOptions = new ReplaySubject(1);
    this.yesNoValuesService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          const yesNoValues = data.results.map(
            (row: { value: string; description: string }) => ({
              name: row.description,
              value: row.value,
            })
          );
          const searchYesNoValues = [
            {
              name: '',
              value: '',
            },
            ...yesNoValues,
          ];
          this._getColumn('objectionLate').selectArr = searchYesNoValues;
          this._getCreateColumn('objectionLate').selectArr = yesNoValues;
        }

        this.observables.yesNoOptions.next(true);
        this.observables.yesNoOptions.complete();
      },
      error: () => {
        this.observables.yesNoOptions.error(false);
      },
    });
    // All objection types
    this.observables.objectionTypes = new ReplaySubject(1);

    this.ojectionTypesService
      .get({
        ...this.queryParameters,
        filters: {
          supported: '0',
        },
      })
      .subscribe((types) => {
        const typesArr = types.results.map(
          (row: { value: string; description: string }) => ({
            name: row.description,
            value: row.value,
          })
        );

        typesArr.unshift({
          value: null,
          description: null,
        });
        this._getColumn('objectionType').selectArr = typesArr;
        this.observables.objectionTypes.next(typesArr);
        this.observables.objectionTypes.complete();
      });

    // Supported objection types
    this.observables.objectionTypes = new ReplaySubject(1);

    this.ojectionTypesService
      .get({
        ...this.queryParameters,
        filters: {
          supported: '1',
        },
      })
      .subscribe((types) => {
        const typesArr = types.results.map(
          (row: { value: string; description: string }) => ({
            name: row.description,
            value: row.value,
          })
        );

        this._getCreateColumn('objectionType').selectArr = typesArr;
        this.observables.objectionTypes.next(typesArr);
        this.observables.objectionTypes.complete();
      });
    // Objection Statuses
    this.observables.objectionStatuses = new ReplaySubject(1);

    this.objectionStatusesService.get().subscribe((statuses) => {
      const statusesArr: any[] = statuses.results.map(
        (row: { value: string; description: string }) => ({
          name: row.description,
          value: row.value,
        })
      );
      const searchStatusesArr = [
        {
          value: null,
          description: null,
        },
        ...statusesArr,
      ];
      this._getColumn('objectionStatus').selectArr = searchStatusesArr;
      this._getCreateColumn('objectionStatus').selectArr = statusesArr;
      this.observables.objectionStatuses.next(statuses);
      this.observables.objectionStatuses.complete();
    });
  }

  private redirectToObjectionsEditScreen(objectionId: string) {
    void this.router.navigate([objectionId], { relativeTo: this.route });
  }

  /*
   * Display the Search dialog and, if data is returned, call the get function
   */
  protected displaySearchDialog(): void {
    // Open the dialog
    const dialogRef = this.dialog.open(SearchDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Search ${this.title}`,
        columns: this.columns,
        values: {},
      },
    });

    // Add the filters to the queryParameters, call the get function
    dialogRef.afterClosed().subscribe((result: { [key: string]: any }) => {
      if (result !== null && result !== undefined) {
        if (result.filedDate) {
          result.filedDate = (result.filedDate as Moment)
            .format('YYYY-MM-DD')
            .toString();
        }
        this.queryParameters.filters = { ...result };
        this.queryParameters.pageNumber = 1;
        this._get();
      } else {
        this.firstSearch.focus();
      }
    });
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(CreateComponent, {
      width: '700px',
      data: {
        title: 'Create Objections / Counter Objections',
        mode: DataManagementDialogModes.Insert,
        columns: this.createColumns,
        values: {
          ...data,
        },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
      // if (result !== null && result !== undefined) {
      //   this._insert(result);
      // } else {
      //   this.firstInsert.focus();
      // }
    });
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToObjectionsEditScreen(data?.objectionId);
  }

  protected _getCreateColumn(columnId: string): ColumnDefinitionInterface {
    return [...this.createColumns].find(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );
  }
}
