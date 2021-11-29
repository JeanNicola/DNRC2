/* eslint-disable @typescript-eslint/no-unsafe-member-access */
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import {
  ControlContainer,
  FormControl,
  FormGroup,
  FormGroupDirective,
  NgForm,
  ValidatorFn,
} from '@angular/forms';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from '../../interfaces/column-definition.interface';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { SelectInterface } from '../../interfaces/select.interface';
import { map, startWith, takeUntil } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatSelectChange } from '@angular/material/select';
import { DecimalPipe, formatNumber } from '@angular/common';
import { ErrorStateMatcher } from '@angular/material/core';
import * as moment from 'moment';
import { dateDtoFormat } from '../../constants/date-format-strings';

@Component({
  selector: 'shared-data-form-field',
  templateUrl: './data-form-field.component.html',
  styleUrls: ['./data-form-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataManagementFormFieldComponent
  implements OnInit, OnDestroy, AfterViewInit
{
  @Input() public mode: DataManagementDialogModes;
  @Input() public field: ColumnDefinitionInterface;
  @Input() public value?: any;
  @Input() removePadding = false;

  // Used for keeping focus on a single app-form-row
  @ViewChild('focusRef') focusRef: any;
  @ViewChild('dateTimeInput') dateTimeInput: any;
  // Used for manipulating the autocomplete element
  @ViewChild('autoPanel', { read: MatAutocompleteTrigger })
  autoPanel: MatAutocompleteTrigger;
  @Output()
  public shiftTabEvent: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>();

  @Output()
  public changeEvent: EventEmitter<any> = new EventEmitter<any>();

  @Output()
  public blurEvent: EventEmitter<any> = new EventEmitter<any>();

  public form: FormGroup;
  public displayWidth = 200;
  public validators: ValidatorFn[];
  public isRequired: boolean;
  public filteredArr: Observable<SelectInterface[]>;
  public ctrl: FormControl;
  public formErrorMatcher = new FormErrorStateMatcher();
  public fieldTypes = FormFieldTypeEnum;
  public dialogTypes = DataManagementDialogModes;

  private unsubscribe = new Subject();

  constructor(
    private parentContainer: ControlContainer,
    private decimalPipe: DecimalPipe
  ) {}

  public ngOnInit(): void {
    this.form = this.parentContainer.control as FormGroup;

    const editable = this.field.editable ?? true;
    const validators =
      this.mode === DataManagementDialogModes.Search
        ? this.field.searchValidators
        : this.field.validators;

    this.ctrl = new FormControl(this.value, validators || []);
    this.form.addControl(this.field.columnId, this.ctrl);

    this.isRequired = Boolean(
      validators?.find((validator) => validator.name === 'required')
    );

    this.field.valueObservable$
      ?.pipe(takeUntil(this.unsubscribe))
      .subscribe(this.setValue.bind(this));

    if (!editable && this.mode === DataManagementDialogModes.Update) {
      this.ctrl.disable();
    }

    if (this.field.type === FormFieldTypeEnum.Autocomplete) {
      // if this.validators does not exist, create empty array
      if (!this.validators) {
        this.validators = [];
      }
      // Add new selectArray validator to array and set control validators
      this.validators.push(
        WRISValidators.matchToSelectArray(this.field.selectArr)
      );
      this.ctrl.setValidators(this.validators);
    }

    this.filteredArr =
      this.field.type === FormFieldTypeEnum.Autocomplete
        ? this.ctrl.valueChanges.pipe(
            startWith(''),
            map(this._filter.bind(this))
          )
        : undefined;
  }

  public ngAfterViewInit(): void {
    if (this.field.type === this.fieldTypes.Currency) {
      this.formatCurrency();
    }
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public getFocusRef(): any {
    return this.field.type === FormFieldTypeEnum.DateTime ||
      this.field.type === FormFieldTypeEnum.DateOrDateTime
      ? this.dateTimeInput.focusRef
      : this.focusRef;
  }

  // called from the parent dialog after it opens
  public initFocus(): void {
    this.getFocusRef()?.nativeElement?.focus();
    // if value is already set
    // close the autocomplete panel
    // Added check for autopanel since sometimes it's not present
    if (this.value && this.autoPanel) {
      this.autoPanel.closePanel();
    }
  }

  public isNotDisabled(): boolean {
    return this.field.type === FormFieldTypeEnum.Select
      ? !this.getFocusRef().ngControl.control.disabled
      : !this.getFocusRef()?.nativeElement?.disabled;
  }

  // Detect keyboard presses when in the form field.
  public keydown($event: KeyboardEvent): void {
    // Sends event to parent to keep focus on form.
    if ($event.key === 'Tab' && $event.shiftKey === true) {
      this.shiftTabEvent.emit($event);
    }
  }

  public change($event: any): void {
    let event = {
      ...$event,
      fieldName: this.field.columnId,
    };

    if (this.field.type === this.fieldTypes.Input) {
      event.value = $event?.target?.value;
    }
    this.changeEvent.emit(event);
  }

  public blur($event: Event): void {
    this.blurEvent.emit({ fieldName: this.field.columnId, event: $event });
  }

  public getValue(): string | number | null {
    return typeof this.ctrl.value === 'string'
      ? this.ctrl.value.toUpperCase()
      : this.ctrl.value;
  }

  public setValue(value?: string | number): void {
    value = value ?? null;
    if (this.field.type === FormFieldTypeEnum.Date && value != null) {
      // strict mode is off since the value is always a datetime string
      this.ctrl.setValue(moment(value, dateDtoFormat));
    } else {
      this.ctrl.setValue(value);
    }
  }

  /*
   * Formats the entered currency
   */
  public formatCurrency(): void {
    // Remove any commas from the number then format it to the proper decimals
    if (this.ctrl.valid) {
      // Strip off commas and formt number using specified decimal places
      let strValue = `${this.ctrl.value}`.replace(/,/g, '');

      // If strValue is not null or empty, process it, otherwise set strValue to 0
      if (strValue) {
        let val = parseFloat(strValue);

        if (isNaN(val)) {
          val = 0;
        }

        strValue = formatNumber(
          val,
          'en-us',
          '0.2-' + (this.field?.decimalPlaces || '2')
        );
      } else {
        strValue = '0';
      }

      // Set the  value on the control
      this.ctrl.setValue(strValue.replace(/,/g, ''));

      // Sets the value directly inside the HTML input
      if (this.focusRef?.nativeElement?.value) {
        this.focusRef.nativeElement.value = this.decimalPipe.transform(
          this.ctrl.value,
          `0.${this.field?.decimalPlaces || 2}`
        );
      }
    }
  }

  clear($event: Event): void {
    $event.stopPropagation();
    // Clear out the existing data and mark the field as dirty
    // If it's an Autocomplete type, clear the field using an empty string
    // as this will allow the popup list to show upon gaining focus again.
    // Otherwise the popup will only display once typing has begun
    this.form.controls[this.field.columnId].patchValue(
      this.autoPanel ? '' : null
    );
    this.form.controls[this.field.columnId].markAsDirty();
    this.form.controls[this.field.columnId].markAsTouched();
    this.form.controls[this.field.columnId].updateValueAndValidity();
    this.changeEvent.emit({
      target: {
        ...$event.target,
        value: this.form.controls[this.field.columnId].value,
      },
      fieldName: this.field.columnId,
    });
  }

  // Displays option.name string if the input string an option.value.
  public autocompleteDisplay(
    value: string | number
  ): string | number | undefined {
    return (
      this.field.selectArr.find((item) => item.value === value)?.name ??
      this.getValue()
    );
  }

  // Set the field if the user tabs/leaves the field
  public updateAutocomplete(): void {
    const value = this.getValue();
    if (!value) {
      return;
    }

    this.setValue(
      this.field.selectArr.find((item) => item.name === value)?.value ?? value
    );
  }

  // Assumption: For autocomplete elements, name will always be a string.
  private _filter(value: string | number): SelectInterface[] {
    // filtering by number?
    if (typeof value === 'number') {
      return [];
    }
    return this.field.selectArr.filter(
      (option) =>
        option.name?.toUpperCase().includes((value || '').toUpperCase()) ??
        (option.value as string).includes((value || '').toUpperCase())
    );
  }
}

/** Error when invalid control is dirty, touched, or submitted. */
export class FormErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(
    control: FormControl | null,
    form: FormGroupDirective | NgForm | null
  ): boolean {
    return Boolean(
      control?.invalid &&
        (control?.dirty || control?.touched || form?.submitted)
    );
  }
}
