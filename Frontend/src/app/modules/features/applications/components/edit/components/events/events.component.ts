import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnDestroy,
} from '@angular/core';
import { AbstractControl, Validators, ValidatorFn } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import * as moment from 'moment';
import { Observable, ReplaySubject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
// eslint-disable-next-line max-len
import { MoreInfoDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/more-info-dialog/more-info-dialog.component';
import { ConfirmationDialogComponent } from 'src/app/modules/shared/components/dialogs/confirmation-dialog/confirmation-dialog.component';
import { ErrorDialogComponent } from 'src/app/modules/shared/components/dialogs/error-dialog/error-dialog.component';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { EditApplicationInterface } from '../../edit.component';
import { EventsInsertDialogComponent } from './components/events-insert-dialog.component';
import { EventsUpdateDialogComponent } from './components/events-update-dialog.component';
import { ApplicationTypesEventTypesService } from './services/application-types-event-types.service';
import { ApplicationsEventsService } from './services/applications-events.service';

export interface EventTypeInterface {
  name: string;
  value: string;
  responseDueDays?: number;
}

class EventValidators {
  static noDuplicateIssues(issued: string): ValidatorFn {
    return (control: AbstractControl) => {
      if (control.value !== 'ISSU') {
        return null;
      }
      if (!issued) {
        return null;
      }

      return {
        errorMessage:
          'Only one event type of ISSUED may be associated to an application',
      };
    };
  }

  static noReissueBeforeIssue(issued: string): ValidatorFn {
    return (control: AbstractControl) => {
      if (control.value !== 'RISS') {
        return null;
      }
      if (issued) {
        return null;
      }

      return {
        errorMessage:
          'An application may not have a REISSUE event without an ISSUE event',
      };
    };
  }

  static noIssueBeforeFeePaid(feeStatus?: string): ValidatorFn {
    return (control: AbstractControl) => {
      if (control.value !== 'ISSU') {
        return null;
      }
      if (!feeStatus || feeStatus === 'FULL') {
        return null;
      }

      return {
        errorMessage:
          'Cannot add an ISSUED event before the application filing fee has been paid in full',
      };
    };
  }

  static noIssueBeforeFormReceived(date: string): ValidatorFn {
    return (control: AbstractControl) => null;
  }
}

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [ApplicationsEventsService, ApplicationTypesEventTypesService],
})
export class EventsComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  public title = 'Event';
  protected searchable = false;
  protected dialogWidth = '500px';
  protected eventTypes: EventTypeInterface[] = [];
  public zHeight = 1;

  private _appData: EditApplicationInterface;

  @Output() eventsChanged: EventEmitter<void> = new EventEmitter<void>();
  @Output() reloadPayments: EventEmitter<void> = new EventEmitter<void>();
  @Input() reloadEvents: Observable<any> = null;

  @Input() set appData(appData: EditApplicationInterface) {
    this._appData = appData;
    this.setEventValidators();
    this.populateDropdowns2();
  }

  get appData() {
    return this._appData;
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'eventId',
      title: 'Event ID',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
      displayInSearch: false,
      displayInEdit: false,
    },
    {
      columnId: 'event',
      title: 'Event',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      editable: false,
      validators: [],
    },
    {
      columnId: 'eventDesc',
      title: 'Event',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInSearch: false,
      displayInEdit: false,
    },
    {
      columnId: 'dateTime',
      title: 'Date',
      type: FormFieldTypeEnum.Date,
      displayInTable: false,
      validators: [],
    },
    {
      // This column is display-only with the date/time
      columnId: 'displayDateTime',
      title: 'Date/Time',
      type: FormFieldTypeEnum.DateTime,
      displayInInsert: false,
      displayInEdit: false,
      primarySort: true,
    },
    {
      columnId: 'responseDue',
      title: 'Response Due',
      type: FormFieldTypeEnum.Date,
      // Validators are set in the Edit dialog
    },
    {
      columnId: 'comments',
      title: 'Comments',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(800)],
    },
    {
      columnId: 'createBy',
      title: 'Created By',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
      displayInTable: false,
      noSort: true,
    },
    {
      columnId: 'createdDate',
      title: 'Created By Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
      displayInInsert: false,
      displayInTable: false,
      noSort: true,
    },
    {
      columnId: 'modifiedBy',
      title: 'Modified By',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
      displayInTable: false,
      noSort: true,
    },
    {
      columnId: 'modifiedDate',
      title: 'Modified By Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
      displayInInsert: false,
      displayInTable: false,
      noSort: true,
    },
  ];

  public initFunction(): void {
    this.reloadEvents
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(this._get.bind(this));

    this.reloadEvents
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(this.setEventValidators.bind(this));

    // Initial load of data
    this._get();
  }

  // Name changed to not trigger during ngOnInit, only when
  // Application Type Code updates
  protected populateDropdowns2(): void {
    // create a separate observable that only emits the one value
    // this way, the http request and the selectArr
    // population only happens once
    this.observables.eventTypes = new ReplaySubject(1);
    // Get the list of application types
    this.appTypesEventTypesService
      .get(this.queryParameters, this.appData.applicationTypeCode)
      .subscribe((data: { results: any[] }) => {
        this.eventTypes = data.results.map((item) => ({
          name: item.description as string,
          value: item.code as string,
          responseDueDays: item.responseDueDays as number | undefined,
        }));

        this._getColumn('event').selectArr = this.eventTypes.filter(
          (event) => !['FRMR', 'PAMH'].includes(event.value)
        );

        this.observables.eventTypes.next(data);
        this.observables.eventTypes.complete();
      });
  }

  constructor(
    public service: ApplicationsEventsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private appTypesEventTypesService: ApplicationTypesEventTypesService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public onMoreInfo(row: number): void {
    this._displayMoreInfoDialog(row);
  }

  private setEventValidators(): void {
    if (!this.appData) {
      return;
    }

    this._getColumn('event').validators = [];

    // Event column is only required for certain Application Type Codes
    if (
      !['607', '617', '618', '626', '651'].includes(
        this.appData.applicationTypeCode
      )
    ) {
      this._getColumn('event').validators.push(Validators.required);
    }

    this._getColumn('event').validators.push(
      EventValidators.noDuplicateIssues(this.appData.issued),
      EventValidators.noReissueBeforeIssue(this.appData.issued),
      EventValidators.noIssueBeforeFeePaid(this.appData.feeStatus)
    );
  }

  /*
   * Adds the date/time display data to the data result set
   */
  protected _getHelperFunction(data: any): any {
    const displayData = { ...data.get };
    const displayResults = [...data.get.results];
    const newResults = displayResults.map((item) => ({
      ...item,
      displayDateTime: item.dateTime,
    }));
    return { ...displayData, results: newResults };
  }

  /*
   * Display the Search dialog and, if data is returned, call the get function
   */
  private _displayMoreInfoDialog(row: number): void {
    // Open the dialog
    const dialogRef = this.dialog.open(MoreInfoDialogComponent, {
      width: '700px',
      data: {
        title: 'Events',
        columns: [
          this._getColumn('createBy'),
          this._getColumn('createdDate'),
          this._getColumn('modifiedBy'),
          this._getColumn('modifiedDate'),
        ],
        values: this.data.results[row],
      },
    });
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.service.insert(newRow, ...this.idArray).subscribe(
      (dto) => {
        let messages = ['Record successfully added.'];
        if (!!dto.messages) {
          messages = [...dto.messages, ...messages];
        }
        this.snackBar.open(messages.join('\n'));
        this._get();
        this.eventsChanged.next();
        if (['RERD', 'EXRD', 'MODR'].includes(dto.event)) {
          this.reloadPayments.next();
        }
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
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    this.service
      .update(updatedRow, ...this.idArray, updatedRow.eventId)
      .subscribe(
        (dto) => {
          let messages = ['Record successfully updated.'];
          if (!!dto.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.eventsChanged.next();
          this._get();
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
    const eventType = this.rows[row].event;
    this.service.delete(...this.idArray, this.rows[row].eventId).subscribe(
      () => {
        this.snackBar.open('Record successfully deleted.');
        this.eventsChanged.next();
        this._get();
        if (['RERD', 'EXRD', 'MODR'].includes(eventType)) {
          this.reloadPayments.next();
        }
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot delete record. ';
        message += errorBody.userMessage || ErrorMessageEnum.DELETE;
        this.snackBar.open(message);
      }
    );
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(EventsInsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Add New ${this.title} Record`,
        columns: this.columns,
        // If no data is being passed in (no values have ben set), default the dateTime to the current date and zero out the time
        values: data
          ? data
          : {
              dateTime: moment().set({
                hour: 0,
                minute: 0,
                second: 0,
                millisecond: 0,
              }),
            },
        appData: this.appData,
        eventTypes: this.eventTypes,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result == null) {
        return;
      }
      if (result.event !== 'ISSU' || this.appData.hasGeocode) {
        return void this._insert(result);
      }

      const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: `Insert ${this.title} Record`,
          message:
            'Warning: You are issuing a water right that does not have a geocode.',
          confirmButtonName: 'Insert',
        },
      });

      dialogRef.afterClosed().subscribe((state) => {
        if (state !== 'confirmed') {
          return void this._displayInsertDialog(result);
        }
        this._insert(result);
      });
    });
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(EventsUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title} Record`,
        columns: this.columns,
        values: data,
        appData: this.appData,
        eventTypes: this.eventTypes,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update({ eventId: data.eventId, ...result });
      }
    });
  }

  /*
   * Display the Delete dialog
   */
  protected _displayDeleteDialog(row: number): void {
    const event = this.rows[row].event;

    if (event === 'ISSU' && this.appData.reissued) {
      this.dialog.open(ErrorDialogComponent, {
        data: {
          title: 'Cannot Delete Record',
          message:
            'Cannot delete an ISSUED event from an application that has a REISSUED event',
        },
      });
      return;
    }

    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._delete(row);
      }
    });
  }
}
