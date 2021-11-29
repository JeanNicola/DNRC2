import { AfterViewInit, Component, Inject, OnDestroy } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as moment from 'moment';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

import { EditApplicationInterface } from '../../../edit.component';
import { EventTypeInterface } from '../events.component';

export interface EventUpdateDialogInterface
  extends DataManagementDialogInterface {
  title: string;
  columns: ColumnDefinitionInterface[];
  values: any;
  appData: EditApplicationInterface;
  eventTypes: EventTypeInterface[];
}

@Component({
  selector: 'events-update-dialog',
  templateUrl:
    '../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class EventsUpdateDialogComponent
  extends UpdateDialogComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    public dialogRef: MatDialogRef<EventsUpdateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EventUpdateDialogInterface
  ) {
    super(dialogRef, data);
  }

  private unsubscribe = new Subject();

  private _dateTimeColumn: ColumnDefinitionInterface | undefined;

  private _eventType: EventTypeInterface | undefined;

  get dateTimeColumn(): ColumnDefinitionInterface {
    if (this._dateTimeColumn) {
      return this._dateTimeColumn;
    }
    return (this._dateTimeColumn = this.displayFields.find(
      (column) => column.columnId === 'dateTime'
    ));
  }

  get eventType(): EventTypeInterface {
    if (this._eventType) {
      return this._eventType;
    }
    return (this._eventType = this.data.eventTypes.find(
      (event) => event.value === this.event.value
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

  initFunction(): void {
    this.detectSystemGeneratedEvent(this.data);
  }

  // Every time dateTime changes, check the value and use it to validate
  // that responseDue comes after dateTime, then update the field to reflect
  // the result of the validation
  ngAfterViewInit(): void {
    const dateTimeValidator = this.dateTime.validator;
    const responseDueValidator = this.responseDue.validator;

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
        if (date) {
          return;
        }
        this.responseDue.setValidators([responseDueValidator]);
        this.responseDue.updateValueAndValidity();
      });

    // NOTE:
    // The Oracle Forms application DOES not do any math using the RSPNS_DUE_DAYS_NO field in the WRD_EVENT_TYPES table.
    // The original developer assumed that table was being used by the business and added this logic.
    // For now, this logic has been commented out in case the business requirements change and they want to use that field.

    // this.dateTime.valueChanges
    //   .pipe(takeUntil(this.unsubscribe))
    //   .subscribe((date) => {
    //     if (!this.eventType?.responseDueDays) return;
    //     this.responseDue.setValue(
    //       moment(date).add(this.eventType.responseDueDays, 'days')
    //     );
    //   });

    // setTimeout(() => {
    //   if (!this.eventType?.responseDueDays) return;
    //   this.responseDue.disable();
    // });

    setTimeout(() => {
      this.dateTimeColumn.type = ['FRMR', 'PAMH', 'ISSU', 'RISS'].includes(
        this.event.value
      )
        ? FormFieldTypeEnum.DateTime
        : FormFieldTypeEnum.Date;

      this.dateTime.updateValueAndValidity();
    });

    setTimeout(() => {
      if (this.event.value !== 'ISSU') {
        return;
      }
      this.dateTime.setValidators([
        dateTimeValidator,
        WRISValidators.afterDate(this.data.appData.dateTimeReceived),
        this.data.appData.reissued
          ? WRISValidators.beforeDate(this.data.appData.reissued)
          : () => null,
      ]);
      this.dateTime.updateValueAndValidity();
    });

    setTimeout(() => {
      if (this.event.value !== 'RISS') {
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
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  // If incoming event is system generated, disable additional fields
  // If incoming event is not system generated, only event is disabled
  detectSystemGeneratedEvent(data: any): void {
    const isPType = data.appData.applicationTypeCode.endsWith('P');
    const event = data.values.event;

    setTimeout(() => {
      if (!['FRMR', 'PAMH'].includes(event)) {
        return;
      }
      this.responseDue.disable();
    });

    setTimeout(() => {
      if (event !== 'PAMH' || isPType) {
        return;
      }
      this.dateTime.disable();
    });
  }
}
