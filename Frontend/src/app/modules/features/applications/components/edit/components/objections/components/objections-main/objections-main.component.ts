import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { PageInterface } from 'src/app/modules/shared/models/page.interface';
import { ApplicationsObjectionsService } from '../../services/applications-objections.service';

@Component({
  selector: 'app-objections-main',
  templateUrl: './objections-main.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './objections-main.component.scss',
    './../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [ApplicationsObjectionsService],
})
export class ObjectionsMainComponent extends BaseCodeTableComponent {
  public hideActions = true;
  public zHeight = 1;
  public title = '';
  public primarySortColumn = 'status';
  public searchable = false;
  protected clickableRow = true;

  // This contains the extra data for the top row
  public summaryRow: any;

  @Output() objectionEvent: EventEmitter<string> = new EventEmitter<string>();
  @Output() summaryEvent: EventEmitter<any> = new EventEmitter<any>();

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'id',
      title: 'Objection #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'objectionType',
      title: 'Objection Type',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'dateReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'late',
      title: 'Late?',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'status',
      title: 'Objection Status',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public topRow: ColumnDefinitionInterface[] = [
    {
      columnId: 'caseId',
      title: 'Case #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'typeDescription',
      title: 'Case Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'statusDescription',
      title: 'Case Status',
      type: FormFieldTypeEnum.Input,
    },
  ];

  constructor(
    public service: ApplicationsObjectionsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  initFunction(): void {
    this._get();
  }

  // Override the initial focus
  protected setTableFocus(): void {}

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    this.summaryRow = { ...data.get.results.summary };
    this.summaryEvent.emit({ ...data.get.results.summary });
    if (data.get.results.details.length) {
      this.objectionEvent.emit(data.get.results.details[0].id);
    }
    // Strip off the other data and only return the details
    return { ...data.get, results: data.get.results.details };
  }

  // Handle the onRowClick event
  public onRowClick(data: any): void {
    this.objectionEvent.emit(data.id);
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'water-court', 'objections', data.id]);
  }
}
