import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  ContentChild,
  Input,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from '../../../../features/code-tables/enums/error-message.enum';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { forkJoin, ReplaySubject } from 'rxjs';
import { DataQueryParametersInterface } from '../../../interfaces/data-query-parameters.interface';
import { PermissionsInterface } from '../../../interfaces/permissions.interface';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import {
  ButtonPositions,
  RowButtonDefinition,
} from '../../../interfaces/row-button-interface';
import { MatButton } from '@angular/material/button';

@Component({
  template: '',
})
export class DataRowComponent implements OnInit, OnDestroy {
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

  public showLoading = true;

  // Data from the database
  public data: { [key: string]: any } = null;
  // Data to be displayed
  public displayData: { [key: string]: any } = null;
  public title = '';
  public containerStyles = {};
  public titleStyles = {};
  public columns: ColumnDefinitionInterface[] = [];
  public displayedColumns: ColumnDefinitionInterface[];
  public paging = true;
  public disableEdit = false;
  public showEdit = true;
  public hideEdit = false;
  public hideHeader = false;
  protected searchable = true;
  protected dialogWidth = null;
  protected rowButtons: RowButtonDefinition[] = [];

  @ViewChild('editButton', { static: false }) editButton: MatButton;
  // For sending components into the expansion panel
  @ContentChild(TemplateRef) expandRef: TemplateRef<any>;

  // The page size options in the paging component
  protected pageSizeOptions: number[] = [25, 50, 100];
  protected url: string = null;

  // Data rows returned from the database
  public rows: any[];

  // protected observables: { [key: string]: Observable<object> } = {};
  protected observables: { [key: string]: ReplaySubject<any> } = {};
  public areDropdownsPopulated = false;

  readonly buttonPositions = ButtonPositions;

  // permissions
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

  constructor(
    public service: BaseDataService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {}

  public ngOnInit(): void {
    this.populateDropdowns();
    // Set the REST API URL on the data service then get the first page of data
    // this.service.url = this.url;
    this.initFunction();

    // Set the table columns to display and add the "Action Button" column
    this.displayedColumns = this.columns
      .filter((item) =>
        item?.displayInTable == null ? true : item?.displayInTable
      )
      .map((item) => item);

    if (Object.keys(this.observables).length > 0) {
      forkJoin({ ...this.observables }).subscribe(() => {
        this.areDropdownsPopulated = true;
      });
    } else {
      this.areDropdownsPopulated = true;
    }

    // this.setupSelects();

    this.setPermissions();
  }

  ngOnDestroy() {
    // Close any open dialogs
    this.dialog.closeAll();
  }

  protected populateDropdowns(): void {
    // here only to be overridden
  }

  protected initFunction(): void {}

  protected setPermissions(): void {
    this.permissions = {
      canGET: this.endpointService.canGET(this.service.url),
      canPOST: this.endpointService.canPOST(this.service.url),
      canDELETE: this.endpointService.canDELETE(this.service.url),
      canPUT: this.endpointService.canPUT(this.service.url),
    };
  }

  // Handle the onEdit event
  public onEdit(): void {
    this._displayEditDialog(this.data);
  }

  protected _onGetSuccessHandler(data: any) {
    // Post-process "get" data
    this.data = this._getHelperFunction(data);

    // Post process data to display
    this.displayData = this._getDisplayData(this.data);

    if (data.get.results?.length) {
      this.dataMessage = null;
    } else {
      this.dataMessage = 'No data found';
    }
  }

  protected _onGetErrorHandler(error: HttpErrorResponse) {}

  /*
   * Get the data using the data service
   */
  protected _get(): void {
    this.dataMessage = 'Loading...';
    // Data is subscribed here so page does not "flicker" to "Loading" each time new page is requested
    forkJoin({
      get: this.service.get(this.queryParameters, ...this.idArray),
      ...this.observables,
    }).subscribe({
      next: this._onGetSuccessHandler.bind(this),
      error: this._onGetErrorHandler.bind(this),
    });
  }

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): { [key: string]: any } {
    return { ...data.get };
  }

  /*
   * A function to modify GET data values for display.
   */
  protected _getDisplayData(data: any): { [key: string]: any } {
    // this allows columnId to behave appropriately for a counter button
    // Later, the column definition should changed so we only use columnId, not counterRef
    return Object.entries(this.columns)
      .map(([k, v], i) => {
        if (v.showCounter) {
          return [v.columnId, data[v.counterRef]];
        }
        return [v.columnId, data[v.columnId]];
      })
      .reduce((obj, x) => {
        obj[x[0]] = x[1];
        return obj;
      }, {});
  }

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    this.service.update(updatedRow, ...this.idArray).subscribe(
      () => {
        this._get();
        this.snackBar.open('Record successfully updated.', null);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update record. ';
        message += errorBody.userMessage || ErrorMessageEnum.PUT;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this._displayEditDialog(updatedRow);
      }
    );
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data?: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title}`,
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(this._buildEditDto(data, result));
      }
      this.editButton.focus();
    });
  }

  protected _buildEditDto(originalData: any, editedData: any): any {
    return editedData;
  }

  protected clickCell(column: ColumnDefinitionInterface) {}

  protected _getColumn(columnId: string): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = this.columns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }
}
