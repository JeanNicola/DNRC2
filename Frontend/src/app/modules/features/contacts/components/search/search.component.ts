import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { SearchDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/search-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ContactStatusService } from '../../services/contact-status.service';
import { ContactTypesService } from '../../services/contact-types.service';
import { ContactsService } from '../../services/contacts.service';
import { formatAddressForDisplay } from '../../shared/format-address';
import { CreateComponent } from '../create/create.component';
import { AddressDialogComponent } from './address-dialog/address-dialog.component';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../shared/components/templates/code-table/code-table.template.scss',
    './search.component.scss',
  ],
  providers: [ContactsService, ContactStatusService, ContactTypesService],
})
export class SearchComponent extends BaseCodeTableComponent implements OnInit {
  constructor(
    public service: ContactsService,
    public contactStatusService: ContactStatusService,
    public contactTypesService: ContactTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Contacts';
  public hideActions = true;
  public primarySortColumn = 'name';
  public sortDirection = 'asc';
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'name',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'address',
      title: 'Address',
      type: FormFieldTypeEnum.Input,
      noSort: true,
    },

    {
      columnId: 'more',
      title: 'More Addresses',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'addressCount',
      displayInEdit: false,
      displayInInsert: false,
      displayInSearch: false,
      noSort: true,
    },
  ];

  private searchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'lastName',
      title: 'Last Name/Corporation',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'firstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'contactType',
      title: 'Contact Type',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'contactStatus',
      title: 'Contact Status',
      type: FormFieldTypeEnum.Select,
    },
  ];

  initFunction(): void {
    this.dataMessage = 'Search for or Create a New Contact';
  }

  // Handle the onCellClick event
  public cellClick(data: any): void {
    if (data?.columnId === 'more' && this.rows[data.row]?.addresses?.length) {
      const addrDialog = this.dialog.open(AddressDialogComponent, {
        data: {
          contactId: this.rows[data.row]?.contactId,
          addresses: this.rows[data.row]?.addresses,
        },
      });
      addrDialog.afterClosed().subscribe((result: any) => {
        // If not set the focus back to the corresponding row
        this.focusRowByIndex(data.row);
      });
    }
  }

  protected _getSearchColumn(columnId: string): ColumnDefinitionInterface {
    let index: number;
    for (let i = 0; i < this.searchColumns.length; i++) {
      if (this.searchColumns[i].columnId === columnId) {
        index = i;
      }
    }
    return this.searchColumns[index];
  }

  public populateDropdowns(): void {
    this.observables.contactStatus = new ReplaySubject(1);
    this.contactStatusService
      .get(this.queryParameters)
      .subscribe((contactStatuses) => {
        contactStatuses.results.unshift({ value: null, description: null });
        this._getSearchColumn('contactStatus').selectArr =
          contactStatuses.results.map(
            (contactStatus: { value: string; description: string }) => ({
              name: contactStatus.description,
              value: contactStatus.value,
            })
          );
        this.observables.contactStatus.next(contactStatuses);
        this.observables.contactStatus.complete();
      });

    this.observables.contactTypes = new ReplaySubject(1);
    this.contactTypesService
      .get(this.queryParameters)
      .subscribe((contactTypes) => {
        contactTypes.results.unshift({ value: null, description: null });
        this._getSearchColumn('contactType').selectArr =
          contactTypes.results.map(
            (contactType: { value: string; description: string }) => ({
              name: contactType.description,
              value: contactType.value,
            })
          );
        this.observables.contactTypes.next(contactTypes);
        this.observables.contactTypes.complete();
      });
  }

  private redirectToContactEdit(contactId: number) {
    void this.router.navigate([contactId], {
      relativeTo: this.route,
    });
  }

  protected _getHelperFunction(data?: any): any {
    if (data.get?.results?.length === 1 && data.get.currentPage === 1) {
      this.redirectToContactEdit(data.get.results[0].contactId);
    }
    return {
      ...data.get,
      results: data.get.results.map((result) => {
        const addresses = result.addresses.map(formatAddressForDisplay);
        return {
          ...result,
          addresses,
          address: result.addresses?.length
            ? formatAddressForDisplay(result.addresses[0]).completeAddress
            : '',
        };
      }),
    };
  }

  protected displaySearchDialog(): void {
    // Open the dialog
    const dialogRef = this.dialog.open(SearchDialogComponent, {
      data: {
        title: `Search ${this.title}`,
        columns: this.searchColumns,
        values: {},
      },
    });

    // Add the filters to the queryParameters, call the get function
    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result) {
        this.queryParameters.filters = { ...result };
        this.queryParameters.pageNumber = 1;
        this._get();
      } else {
        this.setInitialFocus();
      }
    });
  }

  public _displayInsertDialog(): void {
    const dialogRef = this.dialog.open(CreateComponent, {
      minWidth: '650px',
      data: {
        title: `Create New ${this.title} Record`,
        columns: null,
        values: {},
      },
    });

    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result !== null && result !== undefined) {
        this.service.insert(result).subscribe((savedResult) => {
          this.snackBar.open('Contact saved successfully!');
          void this.router.navigate([savedResult.contactId], {
            relativeTo: this.route,
          });
        });
      } else {
        this.firstInsert.focus();
      }
    });
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToContactEdit(data.contactId);
  }
}
