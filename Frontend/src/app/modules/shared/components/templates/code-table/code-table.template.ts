import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  ContentChild,
  OnDestroy,
  Input,
  OnInit,
  TemplateRef,
  ViewChild,
  ViewChildren,
  QueryList,
  AfterViewInit,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ErrorMessageEnum } from '../../../../features/code-tables/enums/error-message.enum';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { takeUntil } from 'rxjs/operators';
import { forkJoin, ReplaySubject, Subject } from 'rxjs';
import { DataQueryParametersInterface } from '../../../interfaces/data-query-parameters.interface';
import { PermissionsInterface } from '../../../interfaces/permissions.interface';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { SearchDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/search-dialog.component';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { DataTableComponent } from '../../data-table/data-table';
import { MatButton } from '@angular/material/button';
import { ValidatorFn } from '@angular/forms';

@Component({
  template: '',
})
export class BaseCodeTableComponent
  implements OnInit, OnDestroy, AfterViewInit
{
  protected unsubscribe = new Subject();

  public data: DataPageInterface<any> = null;
  public title = 'Base Code Table';
  public columns: ColumnDefinitionInterface[] = [];
  public primarySortColumn: string;
  public sortDirection: string;
  public isInMain = true;
  // This contains id's to be used in http requests
  // related to the specific resource.
  // Ex. ['grandParentId', 'parentId'];
  // If the table has no parents, the array is empty.
  protected _idArray: string[] = [];
  @Input() set idArray(id: string[]) {
    this._idArray = id;
  }

  get idArray(): string[] {
    return this._idArray;
  }

  public paging = true;
  protected hideActions = false;
  public hideEdit = false;
  public hideInsert = false;
  public hideDelete = false;
  public hideHeader = false;
  public hideTable = false;
  public zHeight = 8;
  protected searchable = true;
  protected clickableRow = false;
  public highlightOneRow = false;
  public highlightFirstRowOnInit = false;
  protected dblClickableRow = false;
  protected dialogWidth = null;
  protected enableMoreInfo: boolean;
  public containerStyles = {};
  public titleStyles = {};
  public selectedRowStyles;
  public validators: ValidatorFn[];

  // These get the elements for setting focus
  // The initialFocus is updated when all elements are available. Then we
  // assume the other elements are available at that time.
  @ViewChild('firstSearch', { static: false }) firstSearch: MatButton;
  @ViewChild('firstInsert', { static: false }) firstInsert: MatButton;
  @ViewChild('dataTable', { static: false }) dataTable: DataTableComponent;
  @ViewChildren('initialFocus') initialFocusElements: QueryList<any>;

  // For sending components into the expansion panel
  @ContentChild(TemplateRef) expandRef: TemplateRef<any>;

  // The page size options in the paging component
  protected pageSizeOptions: number[] = [25, 50, 100];
  protected url: string = null;

  // Data rows returned from the database
  public rows: any[];
  public areDropdownsPopulated = false;
  // permissions - set to a default to prevent erroneous error later
  public permissions: PermissionsInterface = {
    canGET: false,
    canPOST: false,
    canDELETE: false,
    canPUT: false,
  };

  // String that indicates the status of loading and retrieved data.
  public dataMessage = 'Loading...';

  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  // protected observables: { [key: string]: Observable<object> } = {};
  protected observables: { [key: string]: ReplaySubject<unknown> } = {};

  constructor(
    public service: BaseDataService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {}

  // Once the view is initialized, set the inital focus
  public ngAfterViewInit(): void {
    this.setInitialFocus();
  }

  public ngOnInit(): void {
    this.setPermissions();
    this.populateDropdowns();
    this.initFunction();

    if (Object.keys(this.observables).length > 0) {
      forkJoin({ ...this.observables }).subscribe(() => {
        this.areDropdownsPopulated = true;
      });
    } else {
      this.areDropdownsPopulated = true;
    }
  }

  /*
   * Set the initial focus to either the search or insert button
   * NOTE: TO disable initial focus, override this function
   *
   * This function depends on the @ViewChildren finding view elements with the '#initialFocus' attribute.
   * Once these have been initialized in teh view, then the initial focus can be set.
   * If initial focus is not working as expected, make sure the initial buttons have '#initialFcus' and then
   * either '#firstInsert' or '#firstSerarch' attributes set. Also check the values of this.searchable and the permissions.
   */
  protected setInitialFocus(): void {
    // If the button have not yeet ben created, wait for them then set focus
    if (this.initialFocusElements.length === 0) {
      this.initialFocusElements.changes
        .pipe(takeUntil(this.unsubscribe))
        .subscribe((item) => {
          this._setInitialButtonFocus();
        });
    } else {
      // Otherwise the buttons already exist, so set focus
      this._setInitialButtonFocus();
    }
  }

  // Focus either the search button or the insert button
  // If the both are enabled, this assumes the search will be enabled
  protected _setInitialButtonFocus(): void {
    setTimeout(() => {
      if (this.searchable && this.firstSearch) {
        this.firstSearch.focus();
      } else if (this.permissions?.canPOST && this.firstInsert) {
        this.firstInsert.focus();
      }
    }, 0);
  }

  // Set the initial row focus on the data table
  // NOTE: TO disable this focus, override this function
  protected setTableFocus(): void {
    // Focus first row of datfa table. Timeout solves race condition
    setTimeout(() => {
      if (
        this.rows?.length > 0 &&
        (this.clickableRow || this.dblClickableRow || this.highlightOneRow)
      ) {
        /*
         * Seems to be a timing issue if the current page navigates away
         * If the focus is not getting set as expected, the view element related to
         * this.dataTable may not be properly initialized. It all depends on where the
         * setTableFocus() function is inserted in the subclass code. Currently it is
         * only called from _get() so the view should already be properly initialized.
         */
        if (this.dataTable) {
          this.dataTable.focusFirstElement();
        }
      }
    }, 0);
  }

  protected focusRowByIndex(index: number): void {
    setTimeout(() => {
      this.dataTable.focusRowByIndex(index);
    }, 0);
  }

  public ngOnDestroy(): void {
    // Close any open dialogs
    this.dialog.closeAll();
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  protected populateDropdowns(): void {
    /*
     * Here only to be overridden
     *
     * Example:
     * create a separate observable that only emits the one value
     * this way, the http request and the selectArr
     * population only happens once
    this.observables.programs = new ReplaySubject(1);
    this.service.getPrograms().subscribe((programs: any) => {
      this._getColumn('program').selectArr = programs.results.map(
        (program) => ({
          settimer
          name: program.description,
          value: program.value,
        })
      );

      this.observables.programs.next(programs);
      this.observables.programs.complete();
    });
    *
    */
  }

  protected initFunction(): void {}

  /*
   * setPermissions
   *
   * Gets the permissions from the REST endpoint service stores them for later use in the component
   */
  protected setPermissions(): void {
    this.permissions = {
      canGET: this.endpointService.canGET(this.service.url),
      canPOST: this.endpointService.canPOST(this.service.url),
      canDELETE: this.endpointService.canDELETE(this.service.url),
      canPUT: this.endpointService.canPUT(this.service.url),
    };
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.queryParameters.sortColumn = sort.active.toUpperCase();
      this.queryParameters.sortDirection = sort.direction.toUpperCase();
      this._get();
    }
  }

  /*
   * Event handler for paging
   */
  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.queryParameters.pageSize = pagingOptions.pageSize;
      this.queryParameters.pageNumber = pagingOptions.pageIndex + 1;
      this._get();
    }
  }

  // Handle the onSearch event
  public onSearch(): void {
    this.displaySearchDialog();
  }

  // Handle the onInsert event
  public onInsert(): void {
    this._displayInsertDialog(null);
  }

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    this._displayEditDialog(updatedData);
  }

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(event: Event, data: any): void {}

  // Handle the onRowClick event
  public rowClick(data: any): void {}

  // Handle the onCellClick event
  public cellClick(data: any): void {}

  // Handle the onDblCellClick event
  public cellDblClick(data: any): void {}

  // Handle the moreInfo event
  public moreInfoHandler(row: number): void {}

  // Handle the onDelete event
  public onDelete(row: number): void {
    this._displayDeleteDialog(row);
  }

  /*
   * Get the data using the data service
   */
  protected _get(): void {
    this.dataMessage = 'Loading...';

    const service = this._getService();

    // Data is subscribed here so page does not "flicker" to "Loading" each time new page is requested
    forkJoin({
      get: service.get(this.queryParameters, ...this.idArray),
      ...this.observables,
    }).subscribe(
      (data) => {
        this.data = this._getHelperFunction(data);
        this.rows = this.data.results;

        if (data?.get?.results?.length) {
          this.dataMessage = null;
        } else {
          this.dataMessage = 'No data found';
        }

        // Focus the first row in the table

        this.setTableFocus();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        const message = errorBody.userMessage || ErrorMessageEnum.GET;
        this.snackBar.open(message);
      },
      () => {
        this.dataMessage = 'No data found';
      }
    );
  }

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    return data.get;
  }

  /*
   * A function to modify search parameters and get
   * the appropriate service before each _get
   */
  protected _getService(): BaseDataService {
    return this.service;
  }

  protected _buildInsertIdArray(dto: any): string[] {
    return [...this.idArray];
  }

  protected _buildInsertDto(dto: any): any {
    return dto;
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

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, dto.code];
  }

  protected _getUpdateService(): BaseDataService {
    return this.service;
  }

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any, originalData?: any): void {
    this._getUpdateService()
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
          this._displayEditDialog({ ...originalData, ...updatedRow });
        }
      );
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].code];
  }

  protected _getDeleteService(): BaseDataService {
    return this.service;
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
    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result !== null && result !== undefined) {
        this.queryParameters.filters = { ...result };
        this.queryParameters.pageNumber = 1;
        this._get();
      } else {
        this.firstSearch.focus();
      }
    });
  }

  protected getInsertDialogTitle() {
    return `Add New ${this.title} Record`;
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getInsertDialogTitle(),
        columns: this.columns,
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

  protected getEditDialogTitle() {
    return `Update ${this.title} Record`;
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getEditDialogTitle(),
        columns: this.columns,
        values: data,
        validators: this.validators,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }

  protected _buildEditDto(originalData: any, editedData: any): any {
    return editedData;
  }

  /*
   * Display the Delete dialog
   */
  protected _displayDeleteDialog(row: number): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._delete(row);
      }
    });
  }

  protected _getColumn(columnId: string): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = this.columns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }
}
