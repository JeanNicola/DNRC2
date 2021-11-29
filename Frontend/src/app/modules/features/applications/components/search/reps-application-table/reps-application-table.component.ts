import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Sort } from '@angular/material/sort';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { SearchRepresentativeService } from '../services/search-representative.service';

@Component({
  selector: 'app-reps-application-table',
  templateUrl:
    '../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../shared/components/templates/code-table/code-table.template.scss',
    'reps-application-table.component.scss',
  ],
  providers: [SearchRepresentativeService],
})
export class RepsApplicationTableComponent extends BaseCodeTableComponent {
  @Input() set inputData(value: DataPageInterface<any>) {
    this.data = this._getHelperFunction({ get: value });
    this.rows = this.data.results;
  }
  @Input() set contactId(value: string) {
    this.idArray = [value];
  }
  @Output() dblClickEvent: EventEmitter<number> = new EventEmitter<number>();

  constructor(
    public service: SearchRepresentativeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationTypeCode',
      title: 'Application Type Code',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'applicationTypeDescription',
      title: 'Application Type Description',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'applicationType',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
      sortColumn: 'applicationTypeCode',
    },
    {
      columnId: 'dateTimeReceived',
      title: 'Date/Time Received',
      type: FormFieldTypeEnum.DateTime,
    },
    {
      columnId: 'contactId',
      title: 'Applicant Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'name',
      title: 'Applicant Name',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public primarySortColumn = 'applicationId';
  public sortDirection = 'desc';

  public title = '';
  public searchable = false;
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  protected hideActions = true;
  protected clickableRow = false;
  protected dblClickableRow = true;
  public isInMain = false;

  public onRowDoubleClick(data: any): void {
    this.dblClickEvent.emit(data.applicationId);
  }

  protected _getHelperFunction(data: any): any {
    data.get.results.map((app) => {
      if (
        app.applicationTypeCode !== undefined &&
        app.applicationTypeDescription !== undefined
      ) {
        app.applicationType = `${app.applicationTypeCode} - ${app.applicationTypeDescription}`;
      }
      return app;
    });
    return data.get;
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      const sortColumn =
        this._getColumn(sort.active).sortColumn || sort.active.toUpperCase();
      const sortDirection =
        this._getColumn(sort.active).sortDirection ||
        sort.direction.toUpperCase();
      this.queryParameters.sortColumn = sortColumn.toUpperCase();
      this.queryParameters.sortDirection = sortDirection.toUpperCase();
      this._get();
    }
  }

  public ngOnDestroy(): void {}
}
