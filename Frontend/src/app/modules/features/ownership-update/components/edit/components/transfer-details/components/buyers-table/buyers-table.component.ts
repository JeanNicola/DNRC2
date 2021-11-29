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
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { OwnershipUpdateTypes } from 'src/app/modules/features/ownership-update/interfaces/ownership-update';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { InsertSellerBuyerComponent } from '../../../../../create/components/insert-seller-buyer/insert-seller-buyer.component';
import { AssociateBuyersService } from '../../../../../../../../shared/services/associate-buyers.service';

@Component({
  selector: 'app-buyers-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [AssociateBuyersService],
})
export class BuyersTableComponent
  extends BaseCodeTableComponent
  implements OnChanges
{
  constructor(
    public service: AssociateBuyersService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Events
  @Output() dataLoaded = new EventEmitter();

  @Input() ownerUpdateId;
  @Input() ouWasProcessed = false;
  @Input() readOnly = false;

  @Input() clickableRow = true;
  @Input() dblClickableRow = true;
  @Input() hideActions = false;
  @Input() hideInsert = false;
  @Input() hideTitle = false;
  public isInMain = false;
  protected searchable = false;
  public hideEdit = true;
  public title = '';
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
      width: 120,
    },
    {
      columnId: 'name',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },

    {
      columnId: 'startDate',
      title: 'Start Date',
      type: FormFieldTypeEnum.Date,
      displayInSearch: false,
      displayInInsert: false,
      width: 120,
    },
    {
      columnId: 'lastName',
      title: 'Last Name',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
      noSort: true,
    },
    {
      columnId: 'firstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
      noSort: true,
    },
  ];

  protected initFunction(): void {
    this.title = this.hideTitle ? '' : 'Buyers';
    this.idArray = [this.ownerUpdateId];
    this._get();
  }

  public ngOnChanges(changes: SimpleChanges): void {
    if (
      changes.ouWasProcessed?.currentValue ||
      changes.readOnly?.currentValue
    ) {
      this.hideInsert = true;
      this.hideDelete = true;
      this.hideActions = true;

      // If the idArray is set, then get data
      if (this.idArray.length > 0) {
        this._get();
      }
    } else if (changes.ouWasProcessed?.currentValue === false) {
      this.hideInsert = false;
      this.hideDelete = false;
      this.hideActions = false;
    }
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].id];
  }

  protected _getHelperFunction(data: any): any {
    // Emits event when the data is
    // Emits event when the data is loaded, but only if the OU has not been processed
    if (!this.ouWasProcessed) {
      this.dataLoaded.emit(data);
    }
    return {
      ...data.get,
      results: data.get.results.map((result) => ({ ...result })),
    };
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}

  // Handle the onInsert event
  public onInsert(): void {
    this._displayInsertBuyerDialog();
  }

  private _displayInsertBuyerDialog(): void {
    const columns = [...this.columns];
    const dialogRef = this.dialog.open(InsertSellerBuyerComponent, {
      data: {
        title: 'Add New Buyer',
        columns,
        values: {
          type: OwnershipUpdateTypes.BUYER,
        },
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const contactIds = this.rows.map((buyer) => buyer.contactId);
        // Filter results so we don't insert duplicates
        const newBuyers = result
          .filter((contact) => contactIds.indexOf(contact.contactId) === -1)
          .map((contact) => contact.contactId);
        const duplicates = result.length - newBuyers.length;
        if (duplicates) {
          this.snackBar.open(
            duplicates === 1
              ? `${duplicates} Duplicate buyer was ignored.`
              : `${duplicates} Duplicate buyers were ignored.`,
            null,
            2000
          );
        }
        // Send Insert request
        if (newBuyers?.length) {
          this._insert({ contactIds: newBuyers });
        }
      }
      if (this.rows?.length) {
        this.focusRowByIndex(0);
      }
    });
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

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    if (!this.dblClickableRow) {
      return;
    }
    this.router.navigate(['wris', 'contacts', data.contactId]);
  }
}
