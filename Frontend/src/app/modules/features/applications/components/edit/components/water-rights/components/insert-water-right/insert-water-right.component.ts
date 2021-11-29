import { StepperSelectionEvent } from '@angular/cdk/stepper';
import {
  AfterViewInit,
  Component,
  ContentChild,
  Inject,
  OnDestroy,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';
import { WaterRightVersionsService } from '../../services/water-right-versions.service';

@Component({
  selector: 'app-insert-water-right',
  templateUrl: './insert-water-right.component.html',
  // Check if css needs to be borrowed
  styleUrls: ['./insert-water-right.component.scss'],
  providers: [WaterRightVersionsService],
})
export class InsertWaterRightComponent
  extends DataManagementDialogComponent
  implements AfterViewInit, OnDestroy
{
  mode = DataManagementDialogModes.Insert;
  title = 'Add New Water Right';
  searchColumns = this.data.columns.filter((item) =>
    item?.displayInSearch == null ? true : item?.displayInInsert
  );
  displayFields = this.data.columns;
  public queryResult: any;
  public rows: any[];
  public dataFound = true;

  private unsubscribe = new Subject();

  protected pageSizeOptions: number[] = [25, 50, 100];
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'asc',
    sortColumn: 'waterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  // permissions - set to a default to prevent erroneous error later
  public permissions: PermissionsInterface = {
    canGET: false,
    canPOST: false,
    canDELETE: false,
    canPUT: false,
  };

  public hideActions = true;
  public hideEdit = true;
  public hideDelete = true;
  public hideHeader = false;
  public clickableRow = true;
  public dblClickableRow = true;

  @ContentChild(TemplateRef) expandRef: TemplateRef<any>;

  // selected row
  public row: any;

  // allow moving to second step
  @ViewChild('stepper') stepper: MatStepper;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<InsertWaterRightComponent>,
    public service: WaterRightVersionsService
  ) {
    super(dialogRef);
  }

  // search whenever moving to the second step
  public ngAfterViewInit(): void {
    this.stepper.selectionChange
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event: StepperSelectionEvent) => {
        if (event.selectedIndex == 1) {
          this.dataFound = true;
          this.rows = undefined;
          this.onSearch();
        }
      });
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public onSearch(): void {
    this.queryParameters.filters = { ...this.formGroup.value };
    this.queryParameters.pageNumber = 1;
    this._get();
  }

  private _get(): void {
    this.row = null;
    this.service.get(this.queryParameters).subscribe((data) => {
      this.queryResult = data;
      this.rows = data.results;
      if (this.rows.length === 0) {
        this.dataFound = false;
      }

      // if only one row is returned, automatically accept the row.
      if (this.rows.length === 1) {
        this.dialogRef.close(this.rows[0]);
      }
    });
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.queryParameters.sortColumn = sort.active;
      this.queryParameters.sortDirection = sort.direction;
      this._get();
    }
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.queryParameters.pageSize = pagingOptions.pageSize;
      this.queryParameters.pageNumber = pagingOptions.pageIndex + 1;
      this._get();
    }
  }

  public onRowClick(data: any): void {
    this.row = data;
  }

  // exit dialog on double click
  public onRowDoubleClick(data: any): void {
    this.row = data;
    this.dialogRef.close(this.row);
  }

  protected _getColumn(columnId: string): ColumnDefinitionInterface {
    let index: number;
    for (let i = 0; i < this.displayFields.length; i++) {
      if (this.displayFields[i].columnId === columnId) {
        index = i;
      }
    }
    return this.displayFields[index];
  }
}
