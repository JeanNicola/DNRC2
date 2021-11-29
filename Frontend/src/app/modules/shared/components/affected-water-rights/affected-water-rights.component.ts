/* eslint-disable max-len */
import { HttpErrorResponse } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { ResetSellersDialogComponent } from 'src/app/modules/features/ownership-update/components/create/components/reset-sellers-dialog/reset-sellers-dialog.component';
import { RecalculateFeeDueComponent } from 'src/app/modules/features/ownership-update/components/edit/components/dor-payments/components/recalculate-fee-due/recalculate-fee-due.component';
import { CalculateFeeDueService } from 'src/app/modules/features/ownership-update/components/edit/components/dor-payments/services/calculate-fee-due.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from '../../interfaces/data-query-parameters.interface';
import { ErrorBodyInterface } from '../../interfaces/error-body.interface';
import { UpdateDialogComponent } from '../dialogs/data-management/components/update-dialog.component';
import { DeleteDialogComponent } from '../dialogs/delete-dialog/delete-dialog.component';
import { InsertWaterRightComponent } from './components/insert-water-right/insert-water-right.component';
import { AffectedWaterRightsService } from './services/affected-water-rights.service';

@Component({
  selector: 'app-affected-water-rights',
  templateUrl: '../templates/code-table/code-table.template.html',
  styleUrls: ['../templates/code-table/code-table.template.scss'],
  providers: [AffectedWaterRightsService, CalculateFeeDueService],
})
export class AffectedWaterRightsComponent
  extends BaseCodeTableComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    public service: AffectedWaterRightsService,
    public calculateFeeDueService: CalculateFeeDueService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Events
  @Output() dataLoaded = new EventEmitter();
  @Output() waterRightDelete = new EventEmitter();
  @Output() feeDueChanged = new EventEmitter();

  // Table Structure Info

  @Input() currentSellersData = [];
  @Input() currentAppsData = [];
  @Input() showDividedOwnership = true;
  @Input() showStatus = false;
  @Input() showGeoCodesValid = false;
  @Input() showSevered = false;
  @Input() containerStyles = {
    width: '100%',
    height: 'fit-content',
    margin: '20px 0',
  };
  @Input() titleStyles = {
    fontSize: '16px',
  };
  @Input() hideActions = false;
  @Input() hideEdit = false;
  @Input() hideDelete = false;
  @Input() hideInsert = false;
  // Set the title properly because two different locations using this component
  @Input() title = 'Affected Water Rights';
  public searchable = false;
  public isInMain = false;
  @Input() ownershipUpdateType: string = null;

  // Table Behavior Info
  public refreshWaterRightsData$: Subscription;
  @Input() refreshWaterRightsData: Observable<any>;
  @Input() focusFirstElementOnInit = false;
  @Input() clickableRow = false;
  @Input() dblClickableRow = true;
  private _ouWasProcessed = false;
  @Input() set ouWasProcessed(flag: boolean) {
    this.hideEdit = flag;
    this.hideInsert = flag;
    this.hideDelete = flag;
    this.hideActions = flag;
    this._ouWasProcessed = flag;

    // If the OU was processed, reload the data
    if (flag) {
      this._get();
    }
  }
  // Override parent because the database get needs to happen
  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined)) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  // Table Data Info
  @Input() rows = null;
  @Input() data = null;

  public currentWaterRightSelectedForEdit;
  public primarySortColumn = 'completeWaterRightNumber';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'completeWaterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public sortDirection = 'asc';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      width: 150,
    },

    {
      columnId: 'typeDescription',
      title: 'Water Right Type Description',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
    },
    {
      columnId: 'statusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      width: 180,
    },
    {
      columnId: 'dividedOwnship',
      title: 'Divided Ownership',
      type: FormFieldTypeEnum.Checkbox,
      noSort: true,
      displayInSearch: false,
      displayInInsert: false,
    },
    {
      columnId: 'severed',
      title: 'Severed',
      type: FormFieldTypeEnum.Checkbox,
      noSort: true,
      displayInEdit: false,
      displayInSearch: false,
      displayInInsert: false,
    },
    {
      columnId: 'validGeocode',
      title: 'GeoCodes Valid',
      type: FormFieldTypeEnum.Checkbox,
      noSort: true,
      displayInEdit: false,
      displayInSearch: false,
      displayInInsert: false,
    },
  ];

  protected initFunction(): void {
    if (!this.showStatus) {
      this._getColumn('statusDescription').displayInTable = false;
    }
    if (!this.showDividedOwnership) {
      this._getColumn('dividedOwnship').displayInTable = false;
    }

    if (!this.showGeoCodesValid) {
      this._getColumn('validGeocode').displayInTable = false;
    }
    if (!this.showSevered) {
      this._getColumn('severed').displayInTable = false;
    }
  }

  public ngAfterViewInit(): void {
    setTimeout(() => {
      if (this.refreshWaterRightsData) {
        this.refreshWaterRightsData$ = this.refreshWaterRightsData.subscribe(
          () => {
            this._get();
          }
        );
      }
    });
  }

  public ngOnDestroy(): void {
    if (this.refreshWaterRightsData$) {
      this.refreshWaterRightsData$.unsubscribe();
    }
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].waterRightId];
  }

  protected _getHelperFunction(data: any): any {
    // Emits event when the data is loaded, but only if the OU has not been processed
    if (!this._ouWasProcessed) {
      this.dataLoaded.emit(data.get.results);
    }

    if (data.get.results.length === 1 && !this.hideEdit) {
      this.hideDelete = true;
    } else if (!this.hideEdit) {
      this.hideDelete = false;
    }

    return {
      ...data.get,
      results: data.get.results,
    };
  }

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    if (this.dblClickableRow) {
      this.router.navigate(['wris', 'water-rights', data.waterRightId]);
    }
  }

  protected _buildEditIdArray(dto: any): string[] {
    return [
      ...this.idArray,
      this.currentWaterRightSelectedForEdit.waterRightId,
    ];
  }

  private handleFeeDueUpdateSuccess() {
    this.snackBar.open('Fee Due successfully updated.', null);
    this.feeDueChanged.emit(null);
    this._get();
  }

  private handleFeeDueErrorOnUpdate(err: HttpErrorResponse) {
    const errorBody = err.error;
    let message = 'Cannot update Fee Due. ';
    message += errorBody.userMessage || ErrorMessageEnum.PUT;
    this.snackBar.open(message);
  }

  private recalculateFeeDue() {
    if (['DOR 608', '608'].includes(this.ownershipUpdateType)) {
      const recalculateFeeDueDialog = this.dialog.open(
        RecalculateFeeDueComponent,
        {
          width: '500px',
        }
      );
      recalculateFeeDueDialog.afterClosed().subscribe((result) => {
        if (result === 'yes') {
          this.calculateFeeDueService.update({}, this.idArray[0]).subscribe({
            next: this.handleFeeDueUpdateSuccess.bind(this),
            error: this.handleFeeDueErrorOnUpdate.bind(this),
          });
        }
      });
    }
  }

  protected _insert(newRow: any): void {
    this.service.insert(newRow, ...this.idArray).subscribe(
      (dto) => {
        let messages = ['Record successfully added.'];
        if (!!dto.messages) {
          messages = [...dto.messages, ...messages];
        }
        this.snackBar.open(messages.join('\n'));
        this.recalculateFeeDue();
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

  /*
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this.service.delete(...this._buildDeleteIdArray(row)).subscribe(
      () => {
        this._get();
        this.snackBar.open('Record successfully deleted.');
        this.waterRightDelete.emit();
        this.recalculateFeeDue();
        this.setInitialFocus();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot delete record. ';
        message += errorBody.userMessage || ErrorMessageEnum.DELETE;
        this.snackBar.open(message);
      }
    );
  }

  protected _displayInsertDialog(data: any): void {
    const dialogRef = this.dialog.open(InsertWaterRightComponent, {
      data: {
        title: 'Add New Water Right',
        values: data,
        columns: this.columns,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const waterRightIds = this.rows.map(
          (waterRight) => waterRight.waterRightId
        );
        const resultsToInsert = result
          .filter((wr) => waterRightIds.indexOf(wr.waterRightId) === -1)
          .map((wr) => wr.waterRightId);

        const duplicates = result.length - resultsToInsert.length;
        if (duplicates) {
          this.snackBar.open(
            duplicates === 1
              ? `${duplicates} Duplicate water right was ignored.`
              : `${duplicates} Duplicate water rights were ignored.`,
            null,
            2000
          );
        }

        if (resultsToInsert.length) {
          this._insert({ waterRightIds: resultsToInsert });
        }
      }
      if (this.rows?.length) {
        this.focusRowByIndex(0);
      }
    });
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    if (
      this.currentSellersData?.length > 0 ||
      this.currentAppsData?.length > 0
    ) {
      const clearSellerDialog = this.dialog.open(ResetSellersDialogComponent, {
        width: '500px',
        data: {
          values: {
            mode: 'Transfer',
          },
        },
      });
      clearSellerDialog.afterClosed().subscribe((clearSellersResult) => {
        if (clearSellersResult === 'reset') {
          this._delete(row);
        }
        if (this.rows?.length) {
          this.focusRowByIndex(row);
        }
      });
    } else {
      const dialogRef = this.dialog.open(DeleteDialogComponent, {
        width: '500px',
      });
      dialogRef.afterClosed().subscribe((result) => {
        if (result === 'delete') {
          this._delete(row);
        }
        if (this.rows?.length) {
          this.focusRowByIndex(row);
        }
      });
    }
  }

  protected _buildEditDto(originalData: any, editedData: any): any {
    return {
      dividedOwnship: editedData.dividedOwnship,
    };
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  public displayEditDialog(data: any, index): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title} Record`,
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result));
      }
      if (this.rows?.length) {
        this.focusRowByIndex(index);
      }
    });
  }

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    this.currentWaterRightSelectedForEdit = updatedData;
    this.displayEditDialog(updatedData, index);
  }

  // Handle the onInsert event
  public onInsert(): void {
    this._displayInsertDialog(null);
  }
}
