import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { SearchService } from 'src/app/modules/shared/services/search.service';
import { ErrorMessageEnum } from '../../../code-tables/enums/error-message.enum';
import { ApplicationsService } from '../../services/applications.service';
import { CreateComponent } from '../create/create.component';
import { ApplicationSearchDialogComponent } from './application-search-dialog/application-search-dialog.component';
import { OwnersApplicationDialogComponent } from './owners-application-dialog/owners-application-dialog.component';
import { RepsApplicationDialogComponent } from './reps-application-dialog/reps-application-dialog.component';
import { SearchApplicantService } from './services/search-applicant.service';
import { SearchRepresentativeService } from './services/search-representative.service';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    ApplicationsService,
    SearchService,
    SearchApplicantService,
    SearchRepresentativeService,
  ],
})
export class SearchComponent extends BaseCodeTableComponent {
  constructor(
    public searchService: SearchService,
    private applicantService: SearchApplicantService,
    private repService: SearchRepresentativeService,
    private applicationService: ApplicationsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(searchService, endpointService, dialog, snackBar);
  }

  public title = 'Applications';
  public appColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      editable: false,
      validators: [Validators.minLength(3), Validators.maxLength(4)],
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'applicationTypeCode',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'applicationTypeDescription',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
      displayInSearch: false,
    },
    {
      columnId: 'dateTimeReceived',
      title: 'Date/Time Received',
      type: FormFieldTypeEnum.DateTime,
      displayInSearch: false,
    },
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      noSort: true,
    },
    {
      columnId: 'ownerName',
      title: 'Applicant',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      noSort: true,
    },
  ];
  public ownerColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ownerName',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'more',
      title: 'Applications',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'appCount',
      noSort: true,
    },
  ];
  public repColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'repContactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'repName',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'more',
      title: 'Applications',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'appCount',
      noSort: true,
    },
  ];

  public primarySortColumn = 'applicationId';
  public sortDirection = 'desc';
  public hideActions = true;
  protected clickableRow = false;
  protected dblClickableRow = true;

  initFunction(): void {
    this.dataMessage = 'Search for or Create a New Application';
  }

  public cellClick(data: any): void {
    if (
      data?.columnId === 'more' &&
      this.rows[data.row].contactId != undefined
    ) {
      const ownerDialog = this.dialog.open(OwnersApplicationDialogComponent, {
        data: {
          applications: this.rows[data.row].applications,
          contactId: this.rows[data.row].contactId,
          name: this.rows[data.row].ownerName,
        },
      });
      ownerDialog.afterClosed().subscribe((applicationId: number) => {
        if (applicationId != null) {
          this.redirectToApplicationEdit(applicationId);
        }
      });
    } else if (
      data?.columnId === 'more' &&
      this.rows[data.row].repContactId != undefined
    ) {
      const repDialog = this.dialog.open(RepsApplicationDialogComponent, {
        data: {
          applications: this.rows[data.row].applications,
          repContactId: this.rows[data.row].repContactId,
          name: this.rows[data.row].repName,
        },
      });
      repDialog.afterClosed().subscribe((applicationId) => {
        if (applicationId != null) {
          this.redirectToApplicationEdit(applicationId);
        }
      });
    }
  }

  protected _getHelperFunction(data: any): any {
    // If the current page is the 1st, and it has only one record, automactically go to that record
    if (data.get.currentPage === 1 && data.get.results.length === 1) {
      // If the results do not have an "application" property, it was a specific application query,
      // Else it was either a contact or a representative query
      // Use the apporpriate application id
      if (data.get.results[0].applications === undefined) {
        this.redirectToApplicationEdit(data.get.results[0].applicationId);
      } else if (data.get.results[0]?.applications.results.length === 1) {
        this.redirectToApplicationEdit(
          data.get.results[0].applications.results[0].applicationId
        );
      }
    }

    data.get.results.map((owner) => {
      if (owner.applications != null) {
        owner.appCount = owner.applications.totalElements;
      }
    });

    data.get.results.map((app) => {
      if (
        app.applicationTypeCode !== undefined &&
        app.applicationTypeDescription !== undefined
      ) {
        app.applicationTypeDescription = `${app.applicationTypeCode} - ${app.applicationTypeDescription}`;
      }
      return app;
    });

    return data.get;
  }

  /*
   * Set up the query based on which Search tab the user used
   */
  private _setUpQuery(): void {
    let changing = false;
    if (
      this.queryParameters.filters.ownerContactId != null ||
      this.queryParameters.filters.ownerLastName != null ||
      this.queryParameters.filters.ownerFirstName != null
    ) {
      changing = this.primarySortColumn !== 'contactId';
      this.service = this.applicantService;
      this.columns = this.ownerColumns;
      this.primarySortColumn = 'contactId';
      this.sortDirection = 'desc';
      this.dblClickableRow = false;
      this.clickableRow = false;
      this.title = 'Applicants';
    } else if (
      this.queryParameters.filters.repContactId != null ||
      this.queryParameters.filters.repLastName != null ||
      this.queryParameters.filters.repFirstName != null
    ) {
      changing = this.primarySortColumn !== 'repContactId';

      this.service = this.repService;
      this.columns = this.repColumns;
      this.primarySortColumn = 'repContactId';
      this.sortDirection = 'desc';
      this.dblClickableRow = false;
      this.clickableRow = false;
      this.title = 'Representatives for Applicants';
    } else {
      changing = this.primarySortColumn !== 'applicationId';
      this.service = this.searchService;
      this.columns = this.appColumns;
      this.dblClickableRow = true;
      this.clickableRow = true;
      this.primarySortColumn = 'applicationId';
      this.sortDirection = 'desc';
      this.title = 'Applications';
    }
    // back to the loading screen
    if (changing) {
      this.data = null;
      this.rows = null;
    }
  }

  private redirectToApplicationEdit(applId: number) {
    void this.router.navigate([applId], {
      relativeTo: this.route,
    });
  }

  public onRowDoubleClick(data: any): void {
    if (this.dblClickableRow) {
      this.redirectToApplicationEdit(data.applicationId);
    }
  }

  /*
   * Display the Search dialog and, if data is returned, call the get function
   */
  protected displaySearchDialog(): void {
    // Open the dialog
    const dialogRef = this.dialog.open(ApplicationSearchDialogComponent, {
      data: {
        title: `Search ${this.title}`,
        columns: [],
        values: {},
      },
    });

    // Add the filters to the queryParameters, call the get function
    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result !== null && result !== undefined) {
        // Need to reset the sort and query parameters since new certieria has been selected
        this.queryParameters.filters = { ...result };
        this.queryParameters.pageNumber = 1;
        this.queryParameters.sortColumn = '';
        this.queryParameters.sortDirection = '';
        this._setUpQuery();
        this._get();
      } else {
        this.setInitialFocus();
      }
    });
  }

  protected _displayInsertDialog(): void {
    const dialogRef = this.dialog.open(CreateComponent, {
      data: {
        title: 'Create New Application',
        values: {},
      },
    });

    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result !== null && result !== undefined) {
        this.applicationService.insert(result).subscribe(
          (ret: any) => {
            const applId = ret?.applicationId as number;
            this.redirectToApplicationEdit(applId);
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error as ErrorBodyInterface;
            let message = 'Cannot insert new record. ';
            message += errorBody.userMessage || ErrorMessageEnum.POST;
            this.snackBar.open(message);
          }
        );
      } else {
        this.firstInsert.focus();
      }
    });
  }
}
