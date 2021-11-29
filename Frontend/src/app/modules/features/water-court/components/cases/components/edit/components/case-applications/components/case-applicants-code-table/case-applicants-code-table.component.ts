import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { RepresentativesDialogComponent } from 'src/app/modules/features/applications/components/edit/components/applicant/components/representatives-dialog/representatives-dialog.component';
import { concatenateNames } from 'src/app/modules/features/applications/components/edit/components/applicant/utilities/concatenate-names';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { CaseApplicantsService } from './services/case-applicants.service';

@Component({
  selector: 'app-case-applicants-code-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './case-applicants-code-table.component.scss',
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CaseApplicantsService],
})
export class CaseApplicantsCodeTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: CaseApplicantsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  private _applicationId = null;
  @Input() set applicationId(value: string) {
    this.idArray = [this.route.snapshot.params.caseId];
    this._applicationId = value;
    this._get();
  }

  get applicationId(): string {
    return this._applicationId;
  }

  public clickableRow = true;
  public hideActions = true;
  public hideInsert = true;
  public isInMain = false;
  public searchable = false;
  public title = 'Applicants';
  public primarySortColumn = 'contactId';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'contactId',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'fullName',
      title: 'Contact Name',
      type: FormFieldTypeEnum.TextArea,
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'representatives',
      title: 'Representatives',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'representativeCount',
      displayInEdit: false,
      displayInInsert: false,
      displayInSearch: false,
      noSort: true,
    },
  ];

  protected _getHelperFunction(data: any): any {
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

  // Handle the onCellClick event
  public cellClick(data: any): void {
    if (data?.columnId === 'representatives') {
      const dialog = this.dialog.open(RepresentativesDialogComponent, {
        width: '800px',
        data: {
          title: 'View Representatives',
          idArray: [
            this.applicationId,
            this.rows[data.row].ownerId,
            this.rows[data.row].contactId,
          ],
          applicantData: this.rows[data.row],
          appData: {},
          readOnly: true,
        },
      });
    }
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'contacts', data.contactId]);
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
