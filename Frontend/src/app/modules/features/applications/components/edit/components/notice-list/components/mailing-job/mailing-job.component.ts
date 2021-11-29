import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { NoticeListService } from '../../services/notice-list.service';
import { MailingJob } from '../../interfaces/mailing-job-interface';
import { ActivatedRoute, Router } from '@angular/router';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';

@Component({
  selector: 'app-mailing-job',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [NoticeListService],
})
export class MailingJobComponent extends BaseCodeTableComponent {
  @Output() mailingJobSelectedId = new EventEmitter<string>();

  protected clickableRow = true;
  protected dblClickableRow = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public data: DataPageInterface<MailingJob>;
  public rows: MailingJob[];

  constructor(
    public service: NoticeListService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'ASC',
    sortColumn: 'mailingJobId',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public title = 'Mailing Job';
  protected searchable = false;
  public hideInsert = true;
  public hideActions = true;
  public containerStyles = {
    width: '100%',
    height: 'fit-content',
    margin: '20px 0',
    background: 'rgb(250, 250, 250)',
  };

  public titleStyles = {
    fontSize: '16px',
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'mailingJobId',
      title: 'Mailing Job #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dateGenerated',
      title: 'Generated Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  initFunction(): void {
    this.dataMessage = 'No data found';
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  protected _getHelperFunction(data: any): { results: MailingJob[] } {
    if (data?.get?.results?.length) {
      this.setMailingJobSelectedId(data.get.results[0]);
    }
    if (data?.get?.results) {
      return data.get;
    }
  }

  setMailingJobSelectedId(row: MailingJob) {
    if (row?.mailingJobId) {
      this.mailingJobSelectedId.emit(row?.mailingJobId.toString());
    }
  }

  // Handle the onRowClick event
  public rowClick(row: any): void {
    this.setMailingJobSelectedId(row);
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'mailing-jobs', data.mailingJobId]);
  }
}
