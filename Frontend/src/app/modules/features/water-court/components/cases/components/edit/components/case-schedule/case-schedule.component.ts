import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import * as moment from 'moment';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { CaseEventTypesService } from '../../../../services/case-event-types.service';
import { CaseScheduleService } from './services/case-schedule.service';

@Component({
  selector: 'app-case-schedule',
  templateUrl: './case-schedule.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CaseScheduleService, CaseEventTypesService],
})
export class CaseScheduleComponent extends BaseCodeTableComponent {
  constructor(
    public service: CaseScheduleService,
    public caseEventTypesService: CaseEventTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public dialogWidth = '500px';
  public isInMain = false;
  public searchable = false;
  public title = '';
  public primarySortColumn = 'eventTypeDescription';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'eventTypeDescription',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'eventTypeDescription',
      title: 'Event',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'eventType',
      title: 'Event',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'eventStatus',
      title: 'Event Status',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(30)],
    },
    {
      columnId: 'eventDate',
      title: 'Event Date',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required],
    },
    {
      columnId: 'eventBeginTime',
      title: 'Event Begin Time',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.validTime],
    },
    {
      columnId: 'shortNotes',
      title: 'Notes',
      type: FormFieldTypeEnum.TextArea,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'notes',
      title: 'Notes',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(2000)],
      displayInTable: false,
    },
  ];

  protected initFunction() {
    this.idArray = [this.route.snapshot.params.caseId];
    this._get();
  }

  protected _getHelperFunction(data: any): any {
    return {
      ...data.get,
      results: data.get.results.map((schedule) => {
        return {
          ...schedule,
          eventBeginTime: schedule.eventBeginTime
            ? moment(schedule.eventBeginTime).format('HH:mm')
            : null,
          shortNotes: schedule.notes
            ? schedule.notes.substring(0, 300) +
              (schedule.notes.length > 300 ? '...' : '')
            : '',
        };
      }),
    };
  }

  protected populateDropdowns(): void {
    this.observables.eventTypes = new ReplaySubject(1);

    this.caseEventTypesService
      .get(this.queryParameters, 'SCHD')
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

  protected getInsertDialogTitle() {
    return `Add New Schedule Record`;
  }

  protected getEditDialogTitle() {
    return `Update Schedule Record`;
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].scheduleId];
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.scheduleId];
  }

  private beginTimeOnBlurHandler(
    dialogRef:
      | MatDialogRef<UpdateDialogComponent, any>
      | MatDialogRef<InsertDialogComponent, any>
  ): void {
    dialogRef.componentInstance.changeEvent.subscribe(($event) => {
      if ($event.fieldName === 'eventBeginTime') {
        const enteredTime = moment($event.value, 'H:m', true);
        if (enteredTime.isValid()) {
          const splitTime = $event.value.split(/:/);

          dialogRef.componentInstance.formGroup
            .get('eventBeginTime')
            .setValue(
              moment().hour(splitTime[0]).minutes(splitTime[1]).format('HH:mm')
            );
        }
      }
    });
  }

  private attachEventBeginDateToResult(result): void {
    if (result.eventBeginTime) {
      const splitTime = result.eventBeginTime.split(/:/);
      result.eventBeginTime = moment()
        .hour(splitTime[0])
        .minutes(splitTime[1])
        .day(1);
    }
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getInsertDialogTitle(),
        columns: this.columns,
        values: data,
        validators: this.validators,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this.attachEventBeginDateToResult(result);
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
      if (dialogRef.componentInstance.changeEvent) {
        dialogRef.componentInstance.changeEvent.unsubscribe();
      }
    });

    this.beginTimeOnBlurHandler(dialogRef);
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getEditDialogTitle(),
        columns: this.columns,
        values: data,
        validators: this.validators,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this.attachEventBeginDateToResult(result);
        this._update(this._buildEditDto(data, result), data);
      }
      if (dialogRef.componentInstance.changeEvent) {
        dialogRef.componentInstance.changeEvent.unsubscribe();
      }
    });

    this.beginTimeOnBlurHandler(dialogRef);
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
