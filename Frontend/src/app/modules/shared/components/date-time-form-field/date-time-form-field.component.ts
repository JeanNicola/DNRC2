import {
  AfterViewInit,
  OnDestroy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
  ElementRef,
} from '@angular/core';
import {
  ControlContainer,
  FormControl,
  FormGroup,
  AbstractControl,
} from '@angular/forms';
import * as moment from 'moment';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {
  dateDisplayFormat,
  dateFormats,
  dateTimeDisplayFormat,
  dateTimeDtoFormat,
  dateTimeFormats,
} from '../../constants/date-time-formats';
import { FormFieldTypeEnum } from '../../enums/form-field-type.enum';
import { ColumnDefinitionInterface } from '../../interfaces/column-definition.interface';
import { WRISValidators } from '../../validators/WRIS.validator';
import { DataManagementDialogModes } from '../dialogs/data-management/data-management-dialog.enum';

type Time = {
  hour: number;
  minute: number;
  second: number;
};

@Component({
  selector: 'app-date-time-form-field',
  templateUrl: './date-time-form-field.component.html',
  styleUrls: ['./date-time-form-field.component.scss'],
})
export class DateTimeFormFieldComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  @Input() public field: ColumnDefinitionInterface;
  @Input() public mode: DataManagementDialogModes;
  @Input() isRequired: boolean;
  @Input() value?: string | moment.Moment;
  public defaultValue: string | moment.Moment;

  @Output() public shiftTabEvent = new EventEmitter<KeyboardEvent>();

  @ViewChild('focusRef') focusRef: ElementRef<HTMLInputElement>;

  private unsubscribe = new Subject();

  constructor(private parentContainer: ControlContainer) {}

  public form: FormGroup;
  public parentForm: FormGroup;
  public datePicker: FormControl;

  get isTimeOptional(): boolean {
    return this.field.type === FormFieldTypeEnum.DateOrDateTime;
  }

  get clearable(): boolean {
    return (
      (this.mode === DataManagementDialogModes.Update || this.field.editable) &&
      !this.parentField?.disabled
    );
  }

  get parentField(): AbstractControl {
    return this.parentForm.get(this.field.columnId);
  }

  get dateWriter(): AbstractControl {
    return this.form.get(this.field.columnId);
  }

  protected time(date: moment.Moment): Time | null {
    return date?.isValid()
      ? {
          hour: date.hour(),
          minute: date.minute(),
          second: date.second(),
        }
      : this.isTimeOptional
      ? { hour: 0, minute: 0, second: 0 }
      : null;
  }

  ngOnInit(): void {
    this.parentForm = this.parentContainer.control as FormGroup;
    this.datePicker = new FormControl('');

    this.form = new FormGroup({
      datePicker: this.datePicker,
      [this.field.columnId]: new FormControl('', [
        this.isTimeOptional
          ? WRISValidators.dateOrDateTime
          : WRISValidators.dateTime,
      ]),
    });

    this.parentField.setValidators(
      [
        this.parentField.validator,
        this.isTimeOptional
          ? WRISValidators.dateOrDateTime
          : WRISValidators.dateTime,
      ].filter(Boolean)
    );
  }

  public ngAfterViewInit(): void {
    if (this.value) {
      const date = moment(this.value, dateTimeDtoFormat);
      setTimeout(() => {
        this.dateWriter.setValue(date.format(dateTimeDisplayFormat));
        this.datePicker.setValue(date);
        this.parentField.markAsPristine();
        this.dateWriter.markAsPristine();
        this.defaultValue = date;
      });
    }

    // Validators often depend on other fields belonging to the
    // parent form, so we copy the errors onto the `dateWriter`
    // field directly rather than copy the validators.
    //
    // Without this logic, the `data-field-errors` component, which
    // is attached only to `dateWriter`, will silently ignore
    // validation checks such as `afterOtherField`.
    this.parentForm.statusChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(() => {
        this.dateWriter.setErrors(this.parentField.errors);
      });

    this.parentField.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((value) => {
        if (!value) {
          // Set the default calendar date to today
          this.dateWriter.setValue(null);
          this.datePicker.setValue(null);
          this.defaultValue = null;

          this.parentField.markAsDirty();
          this.dateWriter.markAsDirty();
          return;
        }

        const date = moment(value, [dateTimeDisplayFormat, dateTimeDtoFormat]);
        if (!date.isValid()) {
          return;
        }
        this.dateWriter.setValue(date.format(dateTimeDisplayFormat));
        this.defaultValue = date;
      });

    this.parentField.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(() => {
        if (this.parentField.disabled) {
          this.dateWriter.disable({ emitEvent: false });
          this.datePicker.disable({ emitEvent: false });
        } else {
          this.dateWriter.enable({ emitEvent: false });
          this.datePicker.enable({ emitEvent: false });
        }
      });

    this.datePicker.valueChanges
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((value) => {
        const date = value?.clone();
        const time = this.time(date);
        if (!date?.isValid()) {
          return;
        }

        if (this.dateWriter.value && time) {
          return void setTimeout(() => {
            this.parentField.setValue(date.set(time));
            this.parentField.markAsDirty();
          });
        }

        this.dateWriter.setValue(`${date.format(dateDisplayFormat)} `);
        this.dateWriter.markAsDirty();
        this.defaultValue = null;
        setTimeout(() => void this.focusRef.nativeElement.focus());
      });
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public onChange(value: string): void {
    const date = moment(
      value,
      [...dateTimeFormats, ...(this.isTimeOptional ? dateFormats : [])],
      true
    );

    this.datePicker.setValue(date.clone());
    this.parentField.setValue(date.clone());
    this.parentField.markAsDirty();
  }

  public keydown($event: KeyboardEvent): void {
    // Sends event to parent to keep focus on form.
    if ($event.key === 'Tab' && $event.shiftKey === true) {
      this.shiftTabEvent.emit($event);
    }
  }

  public clear($event: Event): void {
    $event.stopPropagation();

    this.parentField.setValue(null);
    this.dateWriter.setValue(null);
    this.datePicker.setValue(null);
    this.defaultValue = null;

    this.parentField.markAsDirty();
    this.dateWriter.markAsDirty();
  }

  public onBlur(): void {
    if (!this.isTimeOptional || !this.dateWriter.value) {
      return;
    }

    const date = moment(
      this.dateWriter.value.trim(),
      [...dateTimeFormats, ...dateFormats],
      true
    );

    if (!date?.isValid()) {
      return;
    }
    const time = this.time(date);

    return void setTimeout(() => {
      this.parentField.setValue(date.set(time));
      this.parentField.markAsDirty();
    });
  }
}
