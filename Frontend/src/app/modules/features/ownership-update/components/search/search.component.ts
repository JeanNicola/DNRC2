import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { OwnershipUpdateBuyersService } from 'src/app/modules/shared/services/ownership-update-buyers.service';
import { OwnershipUpdateSellersService } from 'src/app/modules/shared/services/ownership-update-sellers.service';
import { ErrorMessageEnum } from '../../../code-tables/enums/error-message.enum';
import { OwnershipUpdateForContactService } from '../../../contacts/components/edit/components/ownership-update/services/ownership-update.service';
import { SearchTypes } from '../../interfaces/ownership-update';
import { OwnershipUpdateTypeService } from '../../services/ownership-update-type.service';
import { OwnershipUpdateService } from '../../services/ownership-update.service';
import { getOwnershipUpdateColumns } from '../../shared/ownership-update-columns';
import { CreateComponent } from '../create/create.component';
import { RecalculateFeeDueComponent } from '../edit/components/dor-payments/components/recalculate-fee-due/recalculate-fee-due.component';
import { CalculateFeeDueService } from '../edit/components/dor-payments/services/calculate-fee-due.service';
import { OwnershipUpdateDialogComponent } from './components/ownership-update-dialog/ownership-update-dialog.component';
import { OwnershipupdateSearchDialogComponent } from './components/ownershipupdate-search-dialog/ownershipupdate-search-dialog.component';
import { ReviewInformationDialogComponent } from './components/review-information-dialog/review-information-dialog.component';
import { WaterRightsDialogComponent } from './components/water-rights-dialog/water-rights-dialog.component';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../shared/components/templates/code-table/code-table.template.scss',
    './search.component.scss',
  ],
  providers: [
    OwnershipUpdateBuyersService,
    OwnershipUpdateSellersService,
    OwnershipUpdateTypeService,
    OwnershipUpdateService,
    OwnershipUpdateForContactService,
    CalculateFeeDueService,
  ],
})
export class SearchComponent extends BaseCodeTableComponent {
  constructor(
    public service: OwnershipUpdateService,
    public calculateFeeDueService: CalculateFeeDueService,
    public ownershipUpdateSellersService: OwnershipUpdateSellersService,
    public ownershipUpdateBuyersService: OwnershipUpdateBuyersService,
    public ownershupUpdateForContactService: OwnershipUpdateForContactService,
    public ownershipUpdateTypeService: OwnershipUpdateTypeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Ownership Updates';
  public hideActions = false;
  public hideDelete = true;
  public hideEdit = true;
  public dblClickableRow = true;
  public clickableRow = true;
  public currentService: BaseDataService;
  public lastSearchType: SearchTypes = SearchTypes.OWNERSHIPUPDATE;

  // permissions - set to a default to prevent erroneous error later
  public permissions: PermissionsInterface = {
    canGET: false,
    canPOST: true,
    canDELETE: false,
    canPUT: false,
  };

  public ownershipUpdateColumns: ColumnDefinitionInterface[] =
    getOwnershipUpdateColumns();

  public sellerAndBuyerColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'name',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ownershipUpdates',
      title: 'Ownership Updates',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'count',
      width: 100,
      noSort: true,
    },
  ];

  public initFunction(): void {
    this.dataMessage = 'Search for or Create a New Ownership Update';
  }

  /*
   * Display the Search dialog and, if data is returned, call the get function
   */
  protected displaySearchDialog(): void {
    // Open the dialog
    const dialogRef = this.dialog.open(OwnershipupdateSearchDialogComponent, {
      data: {
        title: 'Search Ownership Updates',
        columns: [],
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
        this.setInitialFocus();
      }
    });
  }

  // This function is called when you get one Seller/Buyer with only one Ownership Update
  public getOwnershipUpdateForContactAndRedirect(contactId) {
    const queryParameters: DataQueryParametersInterface = {
      sortDirection: '',
      sortColumn: '',
      pageSize: 25,
      pageNumber: 1,
      filters: {ownershipUpdateRole: this.lastSearchType==='buyer'?'BUY':'SEL'},
    };

    this.ownershupUpdateForContactService
      .get(queryParameters, contactId)
      .subscribe((response) => {
        // Get the first result and redirect
        const ownershipUpdate = response.results[0];
        if (!ownershipUpdate?.ownerUpdateId) {
          return;
        }
        this.redirectToEditScreen(ownershipUpdate.ownerUpdateId);
      });
  }

  private redirectToEditScreen(ownershipUpdateId: number) {
    // Redirect to Ownership Update Edit Screen
    void this.router.navigate([ownershipUpdateId], {
      relativeTo: this.route,
    });
  }

  protected getColumn(columnId: string, columns): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = columns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }

  public _getHelperFunction(data?: any): any {
    // Reditect to Ownership Update Edit Screen if only one Onweship Update was found
    if (
      data.get.results.length === 1 &&
      data.get.results[0].ownershipUpdateId &&
      data.get.currentPage === 1
    ) {
      this.redirectToEditScreen(data.get.results[0].ownershipUpdateId);
    }
    // Redirect to Ownership Update Edit Screen if only one Seller/Buyer with only one Ownership Update was found
    if (
      data.get.results.length === 1 &&
      data.get.results[0].count === 1 &&
      data.get.currentPage === 1
    ) {
      // Search for the unique Onwership Update and Redirect
      this.getOwnershipUpdateForContactAndRedirect(
        data.get.results[0].contactId
      );
    }

    return {
      ...data.get,
      results: data.get.results.map((ou) => ({
        ...ou,
        receivedDate: ou.receivedDate || ou.saleDate,
      })),
    };
  }

  // Handle the moreInfo event
  public moreInfoHandler(row: number): void {
    this._displayMoreInfoDialog(row);
  }

  protected _displayMoreInfoDialog(row: number): void {
    // Open the dialog
    this.dialog.open(ReviewInformationDialogComponent, {
      width: '950px',
      data: {
        values: {
          ownershipUpdateId: this.rows[row].ownershipUpdateId,
          ownershipUpdateTypeValue: this.rows[row].ownershipUpdateTypeValue,
          receivedDate: this.rows[row].receivedDate,
          dateProcessed: this.rows[row].dateProcessed,
          dateTerminated: this.rows[row].dateTerminated,
        },
      },
    });
  }

  // Set the corresponding properties for the Sellers/Buyers List
  private configureListForSellerOrBuyerSearch(title: string) {
    this.title = title;
    this.columns = this.sellerAndBuyerColumns;
    this.primarySortColumn = 'name';
    this.sortDirection = 'asc';
    this.dblClickableRow = false;
  }

  // Translate Seller/Buyer search properties into generic Customer search properties
  private getContactFiltersFromSellerOrBuyer(filters, searchType: SearchTypes) {
    if (searchType === SearchTypes.SELLER) {
      return {
        contactId: filters.sellerContactId,
        lastName: filters.sellerLastName,
        firstName: filters.sellerFirstName,
      };
    } else if (searchType === SearchTypes.BUYER) {
      return {
        contactId: filters.buyerContactId,
        lastName: filters.buyerLastName,
        firstName: filters.buyerFirstName,
      };
    }
  }

  /*
   * Get the data using the data service
   */
  protected _get(): void {
    this.dataMessage = 'Loading...';
    this.enableMoreInfo = false;
    this.hideActions = true;
    // Get the search type
    const searchType: any = this.queryParameters.filters.searchType;

    // Property to know whether or not then list should be reseted
    let changing = false;

    if (searchType === SearchTypes.SELLER) {
      this.currentService = this.ownershipUpdateSellersService;
      // Prepare the list to receive the Sellers
      if (
        this.primarySortColumn !== 'name' ||
        this.lastSearchType !== searchType
      ) {
        changing = true;
      }
      this.configureListForSellerOrBuyerSearch('Sellers');
      // Set the corresponding filters
      this.queryParameters.filters = {
        ...this.getContactFiltersFromSellerOrBuyer(
          this.queryParameters.filters,
          SearchTypes.SELLER
        ),
      };
    } else if (searchType === SearchTypes.BUYER) {
      this.currentService = this.ownershipUpdateBuyersService;
      if (
        this.primarySortColumn !== 'name' ||
        this.lastSearchType !== searchType
      ) {
        changing = true;
      }
      // Prepare the list to receive the Buyers
      this.configureListForSellerOrBuyerSearch('Buyers');
      // Set the corresponding filters
      this.queryParameters.filters = {
        ...this.getContactFiltersFromSellerOrBuyer(
          this.queryParameters.filters,
          SearchTypes.BUYER
        ),
      };
    } else if (searchType === SearchTypes.OWNERSHIPUPDATE) {
      if (this.primarySortColumn !== 'ownershipUpdateId') {
        changing = true;
      }

      // Prepare the list to receive the ownership updates
      this.currentService = this.service;
      this.columns = this.ownershipUpdateColumns;
      this.enableMoreInfo = true;
      this.hideActions = false;
      this.primarySortColumn = 'ownershipUpdateId';
      this.sortDirection = 'asc';
      this.title = 'Ownership Updates';
      this.dblClickableRow = true;
    }

    // Reset List
    if (changing) {
      this.data = null;
      this.rows = null;
      this.queryParameters = {
        sortDirection: '',
        sortColumn: '',
        pageSize: 25,
        pageNumber: 1,
        filters: this.queryParameters.filters,
      };
    }

    // This property is used in OwnershipUpdateTableComponent to know which service should be used
    if (searchType) {
      this.lastSearchType = searchType;
    }

    // Get the data
    forkJoin({
      get: this.currentService.get(this.queryParameters, ...this.idArray),
    }).subscribe(
      (data) => {
        this.data = this._getHelperFunction(data);
        this.rows = this.data.results;

        if (data?.get?.results?.length) {
          this.dataMessage = null;
        } else {
          this.dataMessage = 'No data found';
        }
        this.setTableFocus();
      },
      () => {
        this.dataMessage = 'No data found';
      }
    );
  }

  private handleFeeDueUpdateSuccess(savedResult) {
    this.snackBar.open('Fee Due successfully updated.', null);
    this.redirectToEditScreen(savedResult.ownershipUpdateId);
  }

  private handleFeeDueErrorOnUpdate(err: HttpErrorResponse, savedResult) {
    const errorBody = err.error;
    let message = 'Cannot update Fee Due. ';
    message += errorBody.userMessage || ErrorMessageEnum.PUT;
    this.snackBar.open(message);
    this.redirectToEditScreen(savedResult.ownershipUpdateId);
  }

  private recalculateFeeDue(savedResult) {
    const recalculateFeeDueDialog = this.dialog.open(
      RecalculateFeeDueComponent,
      {
        width: '500px',
      }
    );
    recalculateFeeDueDialog.afterClosed().subscribe((result) => {
      if (result === 'yes') {
        this.calculateFeeDueService
          .update({}, savedResult.ownershipUpdateId)
          .subscribe({
            next: () => this.handleFeeDueUpdateSuccess(savedResult),
            error: (err) => this.handleFeeDueErrorOnUpdate(err, savedResult),
          });
      } else {
        this.redirectToEditScreen(savedResult.ownershipUpdateId);
      }
    });
  }

  protected _displayInsertDialog(data: any): void {
    const dialogRef = this.dialog.open(CreateComponent, {
      width: '880px',
      data: {
        title: this.title,
        columns: null,
        values: {},
      },
    });

    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result !== null && result !== undefined) {
        this.service.insert(result).subscribe((savedResult) => {
          this.snackBar.open('Ownership Update saved successfully!');
          if (['DOR 608', '608'].includes(savedResult.ownershipUpdateType)) {
            this.recalculateFeeDue(savedResult);
          } else {
            this.redirectToEditScreen(savedResult.ownershipUpdateId);
          }
        });
      } else {
        this.firstInsert.focus();
      }
    });
  }

  public cellClick(data: any): void {
    if (data?.columnId === 'ownershipUpdates') {
      // Open Ownership Updates Dialog
      const ownerDialog = this.dialog.open(OwnershipUpdateDialogComponent, {
        width: '900px',
        data: {
          title: 'Ownership Updates',
          contactId: this.rows[data.row].contactId,
          name: `${this.rows[data.row].name}, ${this.rows[data.row].contactId}`,
          customerType: this.lastSearchType, // Property to know whether the customer is a Seller or a Buyer
        },
      });
      ownerDialog.afterClosed().subscribe((ownershipUpdateId: number) => {
        if (typeof ownershipUpdateId === 'number') {
          // If one Ownership Update was clicked, redirect
          this.redirectToEditScreen(ownershipUpdateId);
        } else {
          // If not set the focus back to the corresponding row
          this.focusRowByIndex(data.row);
        }
      });
    }

    if (data?.columnId === 'waterRights') {
      // Open Water Rights Dialog
      const waterRightsDialog = this.dialog.open(WaterRightsDialogComponent, {
        data: {
          title: 'Water Rights',
          ownershipUpdateId: this.rows[data.row].ownershipUpdateId,
        },
      });
      waterRightsDialog.afterClosed().subscribe(() => {
        // Reset focus
        this.focusRowByIndex(data.row);
      });
    }
  }

  public onRowDoubleClick(data: any): void {
    if (typeof data?.ownershipUpdateId === 'number') {
      this.redirectToEditScreen(data.ownershipUpdateId);
    }
  }

  public populateDropdowns(): void {
    this.observables.ownershipUpdateType = new ReplaySubject(1);

    this.ownershipUpdateTypeService
      .get(this.queryParameters)
      .subscribe((ownershipUpdateTypes) => {
        ownershipUpdateTypes.results.unshift({
          value: null,
          description: null,
        });
        this.getColumn(
          'ownershipUpdateType',
          this.ownershipUpdateColumns
        ).selectArr = ownershipUpdateTypes.results.map(
          (ownershipUpdateType: { value: string; description: string }) => ({
            name: ownershipUpdateType.description,
            value: ownershipUpdateType.value,
          })
        );
        this.observables.ownershipUpdateType.next(ownershipUpdateTypes);
        this.observables.ownershipUpdateType.complete();
      });
  }

  protected setPermissions(): void {}
}
