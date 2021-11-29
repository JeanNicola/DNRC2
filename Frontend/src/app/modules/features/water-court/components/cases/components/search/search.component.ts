import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CaseStatusService } from 'src/app/modules/features/code-tables/components/case-status/services/case-status-types.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { Office } from 'src/app/modules/shared/components/templates/file-location-processor/file-location-processor.component';
import { OfficeService } from 'src/app/modules/shared/components/templates/file-location-processor/services/office.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { CaseTypeValuesService } from '../../services/case-type-values.service';
import { CasesAndHearingsService } from '../../services/cases-and-hearings.service';
import { CreateComponent } from '../create/create.component';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    CasesAndHearingsService,
    CaseTypeValuesService,
    CaseStatusService,
    OfficeService,
  ],
})
export class SearchComponent extends BaseCodeTableComponent {
  constructor(
    public service: CasesAndHearingsService,
    public caseStatusService: CaseStatusService,
    public caseTypesService: CaseTypeValuesService,
    public officeService: OfficeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Cases / Hearings';
  public dialogWidth = '450px';
  public primarySortColumn = 'caseNumber';
  public sortDirection = 'asc';
  public hideActions = true;
  public hideEdit = true;
  public hideDelete = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;
  public programsDictionary = {};

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'caseNumber',
      title: 'Case #',
      type: FormFieldTypeEnum.Input,
      searchValidators: [Validators.maxLength(10)],
    },
    {
      columnId: 'caseTypeDescription',
      title: 'Case Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'caseTypeCode',
      title: 'Case Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'waterCourtCaseNumber',
      title: 'Water Court Case #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      searchValidators: [Validators.maxLength(20)],
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
      searchValidators: [Validators.maxLength(10)],
    },
    {
      columnId: 'completeApplicationType',
      title: 'Application Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'caseStatusDescription',
      title: 'Case Status',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'caseStatusCode',
      title: 'Case Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
  ];

  public createColumns = [
    {
      columnId: 'caseType',
      title: 'Case Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInSearch: false,
    },
    {
      columnId: 'waterCourtCaseNumber',
      title: 'Water Court Case Number',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(20)],
      displayInSearch: false,
    },
    {
      columnId: 'regionalOfficeId',
      title: 'Regional Office',
      type: FormFieldTypeEnum.Select,
      displayInSearch: false,
    },
    {
      columnId: 'caseStatus',
      title: 'Case Status',
      type: FormFieldTypeEnum.Select,
      displayInSearch: false,
    },
  ];

  public initFunction(): void {
    this.dataMessage = 'Search for or Create a New Case / Hearing';
  }

  private redirectToCasesEditScreen(caseNumber: string) {
    void this.router.navigate([caseNumber], { relativeTo: this.route });
  }

  protected _getHelperFunction(data?: any): any {
    if (data.get?.results?.length === 1 && data.get.currentPage === 1) {
      this.redirectToCasesEditScreen(data.get.results[0].caseNumber);
    }
    return data.get;
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToCasesEditScreen(data?.caseNumber);
  }

  public populateDropdowns(): void {
    // Case Types
    this.observables.caseTypes = new ReplaySubject(1);

    this.caseTypesService
      .get({
        ...this.queryParameters,
        filters: {
          supported: '0',
        },
      })
      .subscribe((types) => {
        const typesArr = [];
        for (let i = 0; i < types.results.length; i++) {
          typesArr.push({
            name: types.results[i].description,
            value: types.results[i].value,
          });
          this.programsDictionary[types.results[i].value] =
            types.results[i].program;
        }
        typesArr.unshift({
          value: null,
          description: null,
        });
        this._getColumn('caseTypeCode').selectArr = typesArr;
        this.observables.caseTypes.next(typesArr);
        this.observables.caseTypes.complete();
      });
    // Case Types for create
    this.observables.caseTypes = new ReplaySubject(1);

    this.caseTypesService
      .get({
        ...this.queryParameters,
        filters: {
          supported: '1',
        },
      })
      .subscribe((types) => {
        this._getCreateColumn('caseType').selectArr = types.results.map(
          (type: { value: string; description: string; program: string }) => ({
            name: type.description,
            value: type.value,
          })
        );
        this.observables.caseTypes.next(types);
        this.observables.caseTypes.complete();
      });
    // Case Statuses
    this.observables.caseStatuses = new ReplaySubject(1);

    this.caseStatusService.get(this.queryParameters).subscribe((statuses) => {
      statuses.results.unshift({
        value: null,
        description: null,
      });
      const statusesArr = statuses.results.map(
        (status: { code: string; description: string }) => ({
          name: status.description,
          value: status.code,
        })
      );

      this._getCreateColumn('caseStatus').selectArr = statusesArr;
      this._getColumn('caseStatusCode').selectArr = statusesArr;
      this.observables.caseStatuses.next(statuses);
      this.observables.caseStatuses.complete();
    });

    // Regional Offcies
    this.observables.offices = new ReplaySubject(1);

    this.officeService
      .get(this.queryParameters)
      .subscribe((offices: { results: Office[] }) => {
        offices.results.unshift({
          officeId: null,
          description: null,
        });
        this._getCreateColumn('regionalOfficeId').selectArr =
          offices.results.map((office: Office) => ({
            name: office.description,
            value: office.officeId,
          }));

        this.observables.offices.next(offices);
        this.observables.offices.complete();
      });
  }

  protected getInsertDialogTitle() {
    return `Create New Case or Hearing Record`;
  }

  /*
   * Insert the data using the data service
   */
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
          this.redirectToCasesEditScreen(dto.caseNumber);
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
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(CreateComponent, {
      width: 'auto',
      data: {
        title: this.getInsertDialogTitle(),
        mode: DataManagementDialogModes.Insert,
        createColumns: this.createColumns,
        programsDictionary: this.programsDictionary,
        values: {
          ...data,
        },
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

  protected _getCreateColumn(columnId: string): ColumnDefinitionInterface {
    return [...this.createColumns].find(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );
  }
}
