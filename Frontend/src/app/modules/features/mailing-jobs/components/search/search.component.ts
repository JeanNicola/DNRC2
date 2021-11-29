import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { SearchDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/search-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { ErrorMessageEnum } from '../../../code-tables/enums/error-message.enum';
import { MailingJobsService } from '../../services/mailing-jobs.service';
import { ApplicationSelectDialogComponent } from '../edit/components/application-select-dialog/application-select-dialog.component';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [MailingJobsService],
})
export class SearchComponent extends BaseCodeTableComponent {
  constructor(
    public service: MailingJobsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'mailingJobNumber',
      title: 'Mail Job #',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'mailingJobHeader',
      title: 'Mail Label Header',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'generatedDate',
      title: 'Generated Date',
      type: FormFieldTypeEnum.Date,
      displayInSearch: false,
      displayInInsert: false,
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, WRISValidators.isNumber(10, 0)],
    },
    {
      columnId: 'applicationTypeDescription',
      title: 'Application Type Description',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      displayInInsert: false,
      width: 500,
    },
  ];
  public applicationSearchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationTypeDescription',
      title: 'Application Type Description',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];
  public title = 'Mailing Jobs';
  public dataMessage = 'Search for or Create a New Mailing Job';
  public dblClickableRow = true;
  public hideActions = true;
  public sortDirection = 'desc';

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'mailing-jobs', data.mailingJobNumber]);
  }

  protected _getHelperFunction(data: any): any {
    if (data.get?.totalElements == 1) {
      void this.router.navigate([
        'wris',
        'mailing-jobs',
        data.get.results[0].mailingJobNumber,
      ]);
    }
    return { ...data.get };
  }

  protected displaySearchDialog(): void {
    // Open the dialog
    const dialogRef = this.dialog.open(SearchDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Search ${this.title}`,
        columns: this.columns,
        values: {},
        mode: DataManagementDialogModes.Insert,
      },
    });

    // Add the filters to the queryParameters, call the get function
    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result !== null && result !== undefined) {
        this.queryParameters.filters = { ...result };
        this.queryParameters.pageNumber = 1;
        this.queryParameters.sortColumn = '';
        this.queryParameters.sortDirection = '';
        this._get();
      } else {
        this.firstSearch.focus();
      }
    });
  }

  protected getInsertDialogTitle() {
    return 'Add New Mailing Job';
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(ApplicationSelectDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Create a Mailing Job',
        columns: this.applicationSearchColumns,
        values: data,
        mode: DataManagementDialogModes.Insert,
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

  protected _buildInsertDto(data: any): any {
    return { applicationId: data.applicationId };
  }

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
          void this.router.navigate([
            'wris',
            'mailing-jobs',
            dto.mailingJobNumber,
          ]);
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
}
