import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { CaseEventTypesService } from '../../../../../../services/case-event-types.service';
import { DistrictCourtEventsService } from './services/district-court-events.service';

@Component({
  selector: 'app-district-court-events-code-table',
  templateUrl: './district-court-events-code-table.component.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [DistrictCourtEventsService, CaseEventTypesService],
})
export class DistrictCourtEventsCodeTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: DistrictCourtEventsService,
    public caseEventTypesService: CaseEventTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined) && this.service) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public dialogWidth = '500px';
  public clickableRow = false;
  public isInMain = false;
  public title = 'Cause Event';
  public primarySortColumn = 'eventTypeDescription';
  public searchable = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'eventTypeDescription',
      title: 'Cause Event',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'eventType',
      title: 'Cause Event',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'eventDate',
      title: 'Cause Date',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required],
    },
    {
      columnId: 'comments',
      title: 'Comments',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(800)],
    },
  ];

  protected populateDropdowns(): void {
    this.observables.eventTypes = new ReplaySubject(1);

    this.caseEventTypesService
      .get({ filters: { supported: '0' } }, 'DCC')
      .subscribe((eventTypes) => {
        this._getColumn('eventType').selectArr = eventTypes.results.map(
          (eventType: { code: string; description: string }) => ({
            name: eventType.description,
            value: eventType.code,
          })
        );
        this.observables.eventTypes.next(eventTypes);
        this.observables.eventTypes.complete();
      });
  }

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    return {
      ...data.get,
      results: data.get.results.map((evt) => {
        return {
          ...evt,
          shortComment: evt.comments
            ? evt.comments.substring(0, 300) +
              (evt.comments.length > 300 ? '...' : '')
            : '',
        };
      }),
    };
  }

  protected getInsertDialogTitle() {
    return 'Add New Cause Event Record';
  }

  protected getEditDialogTitle() {
    return `Update Cause Event Record`;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.eventDateId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].eventDateId];
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
