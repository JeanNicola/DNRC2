import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, ViewChild } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CaseStatusService } from 'src/app/modules/features/code-tables/components/case-status/services/case-status-types.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { CreateComponent } from 'src/app/modules/features/water-court/components/cases/components/create/create.component';
import { CaseProgramTypes } from 'src/app/modules/features/water-court/components/cases/components/create/enums/caseProgramTypes';
import { CaseTypeValuesService } from 'src/app/modules/features/water-court/components/cases/services/case-type-values.service';
import { CasesAndHearingsService } from 'src/app/modules/features/water-court/components/cases/services/cases-and-hearings.service';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { Office } from 'src/app/modules/shared/components/templates/file-location-processor/file-location-processor.component';
import { OfficeService } from 'src/app/modules/shared/components/templates/file-location-processor/services/office.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';

@Component({
  selector: 'app-objections-summary',
  templateUrl: './objections-summary.component.html',
  styleUrls: [
    './objections-summary.component.scss',
    './../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [
    CasesAndHearingsService,
    CaseTypeValuesService,
    CaseStatusService,
    OfficeService,
  ],
})
export class ObjectionsSummaryComponent extends DataRowComponent {
  @Input() data: any = null;
  @Input() applicationId: any = null;

  constructor(
    public service: CasesAndHearingsService,
    public caseStatusService: CaseStatusService,
    public caseTypesService: CaseTypeValuesService,
    public officeService: OfficeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @ViewChild('firstInsert', { static: false }) firstInsert: MatButton;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'caseId',
      title: 'Case #',
      type: FormFieldTypeEnum.Input,
      dblClickable: true,
    },
    {
      columnId: 'typeDescription',
      title: 'Case Type',
      type: FormFieldTypeEnum.Input,
      width: 300,
    },
    {
      columnId: 'statusDescription',
      title: 'Case Status',
      type: FormFieldTypeEnum.Input,
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

  public getColumn(columnId: string): ColumnDefinitionInterface {
    return this.columns.filter((item) => item.columnId === columnId)[0];
  }

  public onIdDoubleClick(): void {
    void this.router.navigate([
      'wris',
      'water-court',
      'case-hearings',
      this.data.caseId,
    ]);
  }

  public populateDropdowns(): void {
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
        const typesArr = types.results
          .filter((type) => type.program !== CaseProgramTypes.WC_PROGRAM)
          .map((type: { value: string; description: string }) => ({
            name: type.description,
            value: type.value,
          }));

        this._getCreateColumn('caseType').selectArr = typesArr;
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

  private redirectToCasesEditScreen(caseNumber: string) {
    void this.router.navigate([
      'wris',
      'water-court',
      'case-hearings',
      caseNumber,
    ]);
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.service.insert(newRow).subscribe(
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
      width: '450px',
      data: {
        title: 'Create New Case or Hearing Record',
        mode: DataManagementDialogModes.Insert,
        createColumns: this.createColumns,
        attachedApplicationId: this.applicationId,
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

  // Handle the onInsert event
  public onInsert(): void {
    this._displayInsertDialog(null);
  }

  protected _getCreateColumn(columnId: string): ColumnDefinitionInterface {
    return [...this.createColumns].find(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );
  }
}
