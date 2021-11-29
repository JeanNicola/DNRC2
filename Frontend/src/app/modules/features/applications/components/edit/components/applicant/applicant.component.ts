import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { BaseCodeTableComponent } from '../../../../../../shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
// eslint-disable-next-line max-len
import { ApplicationsApplicantsService } from 'src/app/modules/features/applications/components/edit/components/applicant/services/applications-applicants.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { RepresentativesDialogComponent } from './components/representatives-dialog/representatives-dialog.component';
import { concatenateNames } from './utilities/concatenate-names';
import { Validators } from '@angular/forms';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { InsertApplicantComponent } from 'src/app/modules/shared/components/insert-applicant/insert-applicant.component';

@Component({
  selector: 'app-applicant',
  templateUrl: './applicant.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [ApplicationsApplicantsService],
})
export class ApplicantComponent extends BaseCodeTableComponent {
  protected url = '/applications/{applicationId}/applicants';
  public title = 'Applicant';
  zHeight = 2;
  searchable = false;
  protected dblClickableRow = true;
  protected clickableRow = true;
  private _appData: any;
  // Logic check every time appData is passed in from parent
  @Input() set appData(obj: any) {
    this._appData = { ...obj };
    this.checkAppTypeCode(this.rows);

    // Set the end date validator to be at least the APPL date/time received
    this._getColumn('endDate').validators.push(
      WRISValidators.afterDate(this._appData.dateTimeReceived)
    );
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'lastName',
      title: 'Last name/Corporation',
      type: FormFieldTypeEnum.Input,
      editable: false,
      displayInTable: false,
      displayInEdit: false,
    },
    // First name is used for Contact Name in Edit Dialog
    {
      columnId: 'firstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      editable: false,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'middleInitial',
      title: 'MI',
      type: FormFieldTypeEnum.Input,
      editable: false,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'suffix',
      title: 'Suffix',
      type: FormFieldTypeEnum.Input,
      editable: false,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'fullName',
      title: 'Contact Name',
      type: FormFieldTypeEnum.TextArea,
      editable: false,
      displayInTable: true,
      displayInEdit: true,
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
      editable: false,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      validators: [WRISValidators.dateBeforeToday],
    },
    {
      columnId: 'ownerId',
      title: 'Owner Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
      displayInInsert: false,
      displayInSearch: false,
    },
    {
      columnId: 'contactIds',
      title: 'Applicants',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      displayInTable: false,
      list: [
        {
          columnId: 'contactId',
          title: 'Contact ID',
          type: FormFieldTypeEnum.Input,
          noSort: true,
        },
        {
          columnId: 'lastName',
          title: 'Last Name',
          type: FormFieldTypeEnum.Input,
          displayInTable: false,
        },
        {
          columnId: 'firstName',
          title: 'First Name',
          type: FormFieldTypeEnum.Input,
          displayInTable: false,
        },
        {
          columnId: 'name',
          title: 'Name',
          type: FormFieldTypeEnum.Input,
          displayInSearch: false,
        },
      ],
    },
  ];

  initFunction(): void {
    this._get();
  }

  // Delete is only allowed when more than one item is present
  // or if the Application Type is a P Type
  _getHelperFunction(data: any): any {
    this.checkAppTypeCode(data.get.results);

    // If records, concatenate the name together
    const results = data.get.results.map((row) => {
      row.fullName = concatenateNames(
        row.lastName,
        row?.firstName,
        row?.middleInitial,
        row?.suffix
      );
      return row;
    });

    return { ...data.get, ...results };
  }

  // This function looks at incoming data or existing row data to determine
  // if delete functionality should be disabled or eneabled based on the
  // number of rows and/or the Application Type Code.
  checkAppTypeCode(data: any) {
    if (data) {
      if (
        data.length < 2 ||
        !['600P', '606P'].includes(this._appData.applicationTypeCode)
      ) {
        this.hideDelete = true;
      } else {
        this.hideDelete = false;
      }
    }
  }

  constructor(
    public service: ApplicationsApplicantsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Handle the openRepresentatives event
  public onRepresentatives(row: any): void {
    const dialog = this.dialog.open(RepresentativesDialogComponent, {
      data: {
        idArray: [this._appData.applicationId, row.ownerId, row.contactId],
        appData: this._appData,
        applicantData: row,
      },
    });

    dialog.afterClosed().subscribe((result) => {
      // refresh the screen data
      this._get();
    });
  }

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    this.service
      .update(updatedRow, ...this.idArray, updatedRow.ownerId)
      .subscribe(
        () => {
          this._get();
          this.snackBar.open('Record successfully updated.', null);
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot update record. ';
          message += errorBody.userMessage || ErrorMessageEnum.PUT;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayEditDialog(updatedRow);
        }
      );
  }

  /*
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this.service.delete(...this.idArray, this.rows[row].ownerId).subscribe(
      () => {
        this._get();
        this.snackBar.open('Record successfully deleted.');
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot delete record. ';
        message += errorBody.userMessage || ErrorMessageEnum.DELETE;
        this.snackBar.open(message);
      }
    );
  }

  _displayInsertDialog(data: any): void {
    const dialog = this.dialog.open(InsertApplicantComponent, {
      data: {
        columns: this._getColumn('contactIds').list,
        values: data,
        beginDate: this._appData.dateTimeReceived,
      },
    });

    dialog.afterClosed().subscribe((result) => {
      if (result?.contactId != null) {
        this._insert({
          contactId: result.contactId,
          beginDate: this._appData.dateTimeReceived,
        });
      }
    });
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    // Not used in form but sent in payload
    const ownerId = data.ownerId;

    const validators = this._getColumn('endDate').validators;

    if (data.latestRepresentativeEndDate) {
      this._getColumn('endDate').validators = [
        ...validators,
        WRISValidators.afterDate(data.latestRepresentativeEndDate),
      ];
    }

    if (data.endDate) {
      this._getColumn('endDate').editable = false;
    }

    // data.beginDate = this._appData.dateTimeReceived;
    // Open the dialog
    const dialog = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title} Record`,
        columns: this.columns,
        values: data,
      },
    });

    dialog.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update({
          ownerId,
          contactId: result.contactId,
          beginDate: result.beginDate,
          endDate: result.endDate,
        });
      }

      this._getColumn('endDate').validators = validators;
      this._getColumn('endDate').editable = true;
    });
  }
}
