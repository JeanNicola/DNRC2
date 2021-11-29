import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { CaseObjectionsService } from './services/case-objections.service';

@Component({
  selector: 'app-case-objections-code-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './case-objections-code-table.component.scss',
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CaseObjectionsService],
})
export class CaseObjectionsCodeTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: CaseObjectionsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() objectionEvent: EventEmitter<string> = new EventEmitter<string>();
  private _applicationId = null;
  @Input() set applicationId(value: string) {
    this.idArray = [this.route.snapshot.params.caseId];
    this._get();
  }

  get applicationId(): string {
    return this._applicationId;
  }

  public highlightFirstRowOnInit = true;
  public highlightOneRow = true;
  public clickableRow = true;
  public hideActions = true;
  public hideInsert = true;
  public isInMain = false;
  public searchable = false;
  public dblClickableRow = true;
  public title = 'Objections';
  public primarySortColumn = 'status';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'status',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'id',
      title: 'Objection #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dateReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'status',
      title: 'Objection Status',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected _getHelperFunction(data: any): any {
    if (data.get.results.details.length) {
      this.objectionEvent.emit(data.get.results.details[0].id);
    } else {
      this.objectionEvent.emit(null);
    }
    // Strip off the other data and only return the details
    return { ...data.get, results: data.get.results.details };
  }

  // Handle the onRowClick event
  public rowClick(data: any): void {
    this.objectionEvent.emit(data.id);
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'water-court', 'objections', data.id]);
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
