import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Observable, ReplaySubject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { OwnershipUpdateTypes } from 'src/app/modules/features/ownership-update/interfaces/ownership-update';
import { AffectedWaterRightsService } from 'src/app/modules/shared/components/affected-water-rights/services/affected-water-rights.service';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { AssociateSellersService } from 'src/app/modules/shared/services/associate-sellers.service';
import { InsertSellerBuyerComponent } from '../../../../../create/components/insert-seller-buyer/insert-seller-buyer.component';
import { ContractForDeedValuesService } from './services/contract-for-deed-values.service';

@Component({
  selector: 'app-sellers-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    AssociateSellersService,
    ContractForDeedValuesService,
    AffectedWaterRightsService,
  ],
})
export class SellersTableComponent
  extends BaseCodeTableComponent
  implements OnChanges
{
  constructor(
    public service: AssociateSellersService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public waterRightsService: AffectedWaterRightsService,
    public contractForDeedValuesService: ContractForDeedValuesService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Events
  @Output() dataLoaded = new EventEmitter();

  // Inputs
  @Input() onWaterRightDeleteObservable: Observable<any>;
  @Input() ownerUpdateId;

  @Input() ouWasProcessed = false;
  @Input() readOnly = false;

  public onWaterRightDeleteSub$: Subscription;

  @Input() clickableRow = true;
  @Input() dblClickableRow = true;
  @Input() hideActions = false;
  @Input() hideInsert = false;
  @Input() hideTitle = false;
  protected searchable = false;
  public isInMain = false;
  public title = '';
  public dialogWidth = 600;
  public primarySortColumn = 'name';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'name',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 120,
    },
    {
      columnId: 'name',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
    },

    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
      displayInInsert: false,
      displayInSearch: false,
      width: 120,
    },

    {
      columnId: 'contractForDeedRle',
      title: 'Contract for Deed/RLE',
      type: FormFieldTypeEnum.Select,
      displayInInsert: false,
      displayInSearch: false,
      displayInTable: false,
      noSort: true,
    },
    {
      columnId: 'contractForDeedRleVal',
      title: 'Contract for Deed/RLE',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInSearch: false,
      displayInEdit: false,
      noSort: true,
    },
    {
      columnId: 'lastName',
      title: 'Last Name',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
      displayInInsert: false,
      noSort: true,
    },
    {
      columnId: 'firstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
      displayInInsert: false,
      noSort: true,
    },
  ];

  protected initFunction(): void {
    this.title = this.hideTitle ? '' : 'Sellers';
    this.idArray = [this.ownerUpdateId];
    if (this.onWaterRightDeleteObservable) {
      // Sub used to clear the sellers whenever a water right gets removed
      this.onWaterRightDeleteSub$ = this.onWaterRightDeleteObservable.subscribe(
        () => {
          this.service.deleteAll(...this.idArray).subscribe(() => {
            this._get();
          });
        }
      );
    }
    this._get();
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  public ngOnChanges(changes: SimpleChanges): void {
    if (
      changes.ouWasProcessed?.currentValue ||
      changes.readOnly?.currentValue
    ) {
      this.hideEdit = true;
      this.hideInsert = true;
      this.hideDelete = true;
      this.hideActions = true;

      // If the idArray is set, then get data
      if (this.idArray.length > 0) {
        this._get();
      }
    } else if (changes.ouWasProcessed?.currentValue === false) {
      this.hideEdit = false;
      this.hideInsert = false;
      this.hideDelete = false;
      this.hideActions = false;
    }
  }

  public ngOnDestroy() {
    if (this.onWaterRightDeleteSub$) {
      this.onWaterRightDeleteSub$.unsubscribe();
    }
  }

  protected _getHelperFunction(data: any): any {
    // Emits event when the data is loaded, but only if the OU has not been processed
    if (!this.ouWasProcessed) {
      this.dataLoaded.emit(data);
    }
    return {
      ...data.get,
      results: data.get.results,
    };
  }

  private getColumnIndexByColumnId(columnId: string): number {
    let index = -1;
    this.columns.forEach((column, i) => {
      if (column.columnId === columnId) {
        index = i;
      }
    });
    return index;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.id];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].id];
  }

  // Handle the onInsert event
  public onInsert(): void {
    this._displayInsertSellerDialog('Add New Seller');
  }

  private _displayInsertSellerDialog(title: string): void {
    const columns = [...this.columns];
    columns.splice(this.getColumnIndexByColumnId('contractForDeedRleVal'), 1);
    columns.splice(this.getColumnIndexByColumnId('endDate'), 1);
    const dialogRef = this.dialog.open(InsertSellerBuyerComponent, {
      data: {
        title,
        columns,
        values: {
          type: OwnershipUpdateTypes.SELLER,
          ownershipUpdateId: this.ownerUpdateId,
          mode: DataManagementDialogModes.Update,
        },
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const contactIds = this.rows.map((seller) => seller.contactId);
        // Filter results so we don't insert duplicates
        const newSellers = result
          .filter((contact) => contactIds.indexOf(contact.contactId) === -1)
          .map((contact) => contact.contactId);
        // Send Insert request
        if (newSellers?.length) {
          this._insert({ contactIds: newSellers });
        }
      }
      if (this.rows?.length) {
        this.focusRowByIndex(0);
      }
    });
  }

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    this.displayEditDialog(updatedData, index);
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
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

  public displayEditDialog(data: any, index): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      data: {
        title: 'Update Sellers Record',
        columns: this.columns,
        values: data,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(
          {
            ...this._buildEditDto(data, result),
            ownershipUpdateId: this.ownerUpdateId,
          },
          data
        );
      }
      if (this.rows?.length) {
        this.focusRowByIndex(index);
      }
    });
  }

  public populateDropdowns(): void {
    this.observables.contractForDeedValues = new ReplaySubject(1);

    this.contractForDeedValuesService
      .get(this.queryParameters)
      .subscribe((results) => {
        results.results.unshift({
          value: null,
          description: null,
        });

        this._getColumn('contractForDeedRle').selectArr = results.results.map(
          (result: { value: string; description: string }) => ({
            name: result.description,
            value: result.value,
          })
        );
        this.observables.contractForDeedValues.next(results);
        this.observables.contractForDeedValues.complete();
      });
  }

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    if (!this.dblClickableRow) {
      return;
    }
    this.router.navigate(['wris', 'contacts', data.contactId]);
  }
}
