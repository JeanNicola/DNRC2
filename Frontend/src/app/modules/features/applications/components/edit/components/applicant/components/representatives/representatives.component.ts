import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { InsertRepresentativeComponent } from 'src/app/modules/shared/components/insert-representatives/insert-representative.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { ApplicationsApplicantsRepresentativesService } from '../../services/applications-applicants-representatives.service';
import { RoleTypesService } from '../../services/role-types.service';
import { concatenateNames } from '../../utilities/concatenate-names';

@Component({
  selector: 'app-representatives',
  templateUrl: './representatives.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [ApplicationsApplicantsRepresentativesService, RoleTypesService],
})
export class RepresentativesComponent extends BaseCodeTableComponent {
  public title = '';
  zHeight = 2;
  pageSizeOptions = [25];
  searchable = false;
  public clickableRow = true;
  @Input() appData: any;
  @Input() applicantData: any;
  @Input() readOnly: boolean = false;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'representativeId',
      title: 'Representative Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
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
      displayInEdit: false,
      displayInTable: false,
    },
    {
      columnId: 'firstName',
      title: 'Contact name',
      type: FormFieldTypeEnum.Input,
      editable: false,
      displayInEdit: false,
      displayInTable: false,
    },
    {
      columnId: 'middleInitial',
      title: 'MI',
      type: FormFieldTypeEnum.Input,
      editable: false,
      displayInEdit: false,
      displayInTable: false,
    },
    {
      columnId: 'suffix',
      title: 'Suffix',
      type: FormFieldTypeEnum.Input,
      editable: false,
      displayInEdit: false,
      displayInTable: false,
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
      // Validators are set in DisplayEditDialog function
    },
    {
      columnId: 'roleTypeCode',
      title: 'Role Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'roleTypeDescription',
      title: 'Role Type',
      type: FormFieldTypeEnum.Input,
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

  constructor(
    public service: ApplicationsApplicantsRepresentativesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private roleTypesService: RoleTypesService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  initFunction(): void {
    // Add validator to beginDate based on applicant beginDate
    if (this.applicantData?.beginDate != null) {
      this._getColumn('beginDate').validators.push(
        WRISValidators.afterDate(this.applicantData.beginDate)
      );
      this._getColumn('beginDate').validators.push(
        WRISValidators.beforeDate(this.applicantData.endDate)
      );
    }

    this.hideInsert = this.readOnly;
    this.hideEdit = this.readOnly;
    this.hideDelete = this.readOnly;
    this.hideActions = this.readOnly;

    // Delete is only for available for P types
    if (!['600P', '606P'].includes(this.appData.applicationTypeCode)) {
      this.hideDelete = true;
    }

    this._get();
  }

  // Concatenate the names into a single string
  _getHelperFunction(data: any): any {
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

  protected populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the http request and the selectArr
    // population only happens once
    this.observables.roleTypes = new ReplaySubject(1);
    // Get the list of application types
    this.roleTypesService
      .get(this.queryParameters)
      .subscribe((data: { results: any[] }) => {
        this._getColumn('roleTypeCode').selectArr = data.results.map(
          (item) => ({
            name: item.description,
            value: item.code,
          })
        );
        this.observables.roleTypes.next(data);
        this.observables.roleTypes.complete();
      });
  }

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    this.service
      .update(updatedRow, ...this.idArray, updatedRow.representativeId)
      .subscribe(
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
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this.service
      .delete(...this.idArray, this.rows[row].representativeId)
      .subscribe(
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
    const dialogRef = this.dialog.open(InsertRepresentativeComponent, {
      data: {
        columns: this._getColumn('contactIds').list,
        formColumns: [
          this._getColumn('beginDate'),
          this._getColumn('roleTypeCode'),
        ],
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.contactId != null) {
        this._insert({
          contactId: result?.contactId,
          beginDate: result?.beginDate,
          roleTypeCode: result?.roleTypeCode,
        });
      }
    });
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(incomingData: any): void {
    // Not used in form, but sent in payload
    const representativeId = incomingData.representativeId;

    // Set Representative end date based on Applicant end date if available
    const data: any = { ...incomingData };
    if (incomingData?.endDate == null && this.applicantData?.endDate != null) {
      data.endDate = this.applicantData.endDate;
    }

    // Dynamically set the endDate validation based on beginDate.
    this._getColumn('endDate').validators = [
      WRISValidators.dateBeforeToday,
      WRISValidators.afterDate(data.beginDate),
      WRISValidators.beforeDate(data.endDate),
    ];

    // require representative end date when the applicant has one
    if (this.applicantData?.endDate !== undefined) {
      this._getColumn('endDate').validators.push(
        Validators.required,
        WRISValidators.beforeDate(this.applicantData.endDate)
      );
    }

    if (data.endDate) {
      this._getColumn('endDate').editable = false;
    }

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
        this._update({
          representativeId,
          contactId: result.contactId,
          beginDate: result.beginDate,
          endDate: result.endDate,
          roleTypeCode: result.roleTypeCode,
        });
      }

      this._getColumn('endDate').editable = true;
    });
  }
}
