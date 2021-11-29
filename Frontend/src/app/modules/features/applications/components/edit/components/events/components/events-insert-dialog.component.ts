import { AfterViewInit, Component, Inject, OnDestroy } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import * as moment from 'moment';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

import { EditApplicationInterface } from '../../../edit.component';
import { EventTypeInterface } from '../events.component';

export interface EventInsertDialogInterface
  extends DataManagementDialogInterface {
  title: string;
  columns: ColumnDefinitionInterface[];
  values: any;
  appData: EditApplicationInterface;
  eventTypes: EventTypeInterface[];
}

@Component({
  selector: 'events-insert-dialog',
  templateUrl:
    '../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class EventsInsertDialogComponent
  extends InsertDialogComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: EventInsertDialogInterface
  ) {
    super(dialogRef, data);
  }

  private unsubscribe = new Subject();

  private _dateTimeColumn: ColumnDefinitionInterface | undefined;

  get dateTimeColumn(): ColumnDefinitionInterface {
    if (this._dateTimeColumn) {
      return this._dateTimeColumn;
    }
    return (this._dateTimeColumn = this.displayFields.find(
      (column) => column.columnId === 'dateTime'
    ));
  }

  get event(): AbstractControl {
    return this.formGroup.get('event');
  }

  get dateTime(): AbstractControl {
    return this.formGroup.get('dateTime');
  }

  get responseDue(): AbstractControl {
    return this.formGroup.get('responseDue');
  }

  public ngAfterViewInit(): void {
    const dateTimeValidator = this.dateTime.validator;
    const responseDueValidator = this.responseDue.validator;

    setTimeout(() => {
      this.dateTimeColumn.type = ['ISSU', 'RISS'].includes(this.event.value)
        ? FormFieldTypeEnum.DateTime
        : FormFieldTypeEnum.Date;
      this.dateTime.updateValueAndValidity();
    });

    this.responseDue.setValidators([
      responseDueValidator,
      this.dateTime.value
        ? WRISValidators.afterDate(this.dateTime.value)
        : () => null,
    ]);

    this.dateTime.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((date) => {
        if (!date) {
          return;
        }
        this.responseDue.setValidators([
          responseDueValidator,
          WRISValidators.afterDate(date),
        ]);
        this.responseDue.updateValueAndValidity();
      });

    this.dateTime.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((date) => {
        this.updateResponseDueDate(date, this.event.value);
      });

    this.event.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        this.dateTimeColumn.type = ['ISSU', 'RISS'].includes(event)
          ? FormFieldTypeEnum.DateTime
          : FormFieldTypeEnum.Date;
        this.dateTime.updateValueAndValidity();
      });

    this.event.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        if (['ISSU', 'RISS'].includes(event)) {
          return;
        }
        this.dateTime.setValidators([dateTimeValidator]);
        this.dateTime.updateValueAndValidity();
      });

    this.event.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        if (event !== 'ISSU') {
          return;
        }
        this.dateTime.setValidators([
          dateTimeValidator,
          WRISValidators.afterDate(this.data.appData.dateTimeReceived),
        ]);
        this.dateTime.updateValueAndValidity();
      });

    this.event.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        if (event !== 'RISS') {
          return;
        }
        this.dateTime.setValidators([
          dateTimeValidator,
          WRISValidators.afterDate(this.data.appData.dateTimeReceived),
          this.data.appData.issued
            ? WRISValidators.afterDate(this.data.appData.issued)
            : () => null,
        ]);
        this.dateTime.updateValueAndValidity();
      });

    this.event.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        if (!this.dateTime.valid || !this.dateTime.value) {
          return;
        }
        this.updateResponseDueDate(this.dateTime.value, event);
      });
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  private updateResponseDueDate(
    value: moment.Moment | string | null | undefined,
    event: string
  ): void {
    // NOTE:
    // The Oracle Forms application DOES not do any math using the RSPNS_DUE_DAYS_NO field in the WRD_EVENT_TYPES table.
    // The original developer assumed that table was being used by the business and added this logic.
    // For now, this logic has been commented out in case the business requirements change and they want to use that field.
    //   const date = moment(value, customDateFormats.display.dateInput);
    //   const days = this.data.eventTypes.find(
    //     (item) => item.value === event
    //   )?.responseDueDays;
    //   if (!days) {
    //     if (this.responseDue.disabled) this.responseDue.reset();
    //     return void this.responseDue.enable();
    //   }
    //   if (date?.isValid()) this.responseDue.setValue(date.add(days, 'days'));
    //   this.responseDue.disable();
  }
}
