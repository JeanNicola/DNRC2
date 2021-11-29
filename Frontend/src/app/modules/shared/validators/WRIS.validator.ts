import { SourceMapGenerator } from '@angular/compiler/src/output/source_map';
import {
  AbstractControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import * as moment from 'moment';
import {
  dateDisplayFormat,
  dateFormats,
  dateTimeDisplayFormat,
  dateTimeDtoFormat,
  dateTimeFormats,
  dateTimeMissingSpaceFormats,
} from '../constants/date-time-formats';
import { CountiesRowInterface } from '../interfaces/counties-row.interface';
import { SelectInterface } from '../interfaces/select.interface';
import { getBytesFromReadable } from '../utilities/get-bytes-from-readable';

// Checks the passed-in value; If null or length of 0m returns TRUE (the value is empty)
// eslint-disable-next-line prefer-arrow/prefer-arrow-functions
function isEmptyInputValue(value: any): boolean {
  // we don't check for string here so it also works with arrays
  return value == null || value.length === 0;
}

// Checks the passed-in value is a moment. If not, creates a new moment using the value
// eslint-disable-next-line prefer-arrow/prefer-arrow-functions
export function checkAndConvertDate(
  date: string | moment.Moment
): moment.Moment {
  return moment.isMoment(date)
    ? date.clone()
    : moment(
        date,
        [...dateFormats, dateTimeDtoFormat, dateTimeDisplayFormat],
        true
      );
}

export class WRISValidators {
  /* Validates a value is a proper US currency (e.g., 9,999,999.99 or -9,999,999.99)
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.currency()]  // for default 2-digit decimal; places or
   *   validators: [WRISValidators.currency(3)] // for a specific number of decimal places
   * },
   */
  static currency(decimalPlaces?: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (isEmptyInputValue(control.value)) {
        // don't validate empty values to allow optional controls
        return null;
      }

      // Default to 2 decimal places
      const digits = decimalPlaces || 2;

      // Convert the string to an actual float number. If ti fails, the value is NAN
      const value = parseFloat(control.value);
      const checkNumOfDigits = RegExp(
        `^-?(([0-9]{1,3},)*([0-9]{3},)*[0-9]{3}|[0-9]*)(\\.[0-9]{0,${digits}})?$`
      );

      if (!isNaN(value)) {
        return checkNumOfDigits.test(control.value)
          ? null
          : { currency: { decimalPlaces: digits, enteredValue: value } };
      } else {
        return {
          isNumber: {
            notANumber: true,
            enteredValue: value,
          },
        };
      }
    };
  }

  /* Validates a an input value is an integer
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.integer]
   * },
   */
  static integer(control: AbstractControl): ValidationErrors | null {
    if (isEmptyInputValue(control.value)) {
      // don't validate empty values to allow optional controls
      return null;
    }

    // Regex looks for line breaks and carriage returns.
    const regex = RegExp('^[0-9]+$');
    return regex.test(control.value) ? null : { integer: true };
  }

  /* Validates a an input value is an= number with max digits and decimal places
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.isNumber(maxDigits: 9, decimalPlaces: 2)]
   * },
   */
  static isNumber(maxDigits?: number, decimalPlaces?: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (isEmptyInputValue(control.value)) {
        // don't validate empty values to allow optional controls
        return null;
      }

      // Default to 2 decimal places
      const decimals = decimalPlaces !== undefined ? decimalPlaces : 2;

      // Default to 10 decimal places
      const digits = maxDigits !== undefined ? maxDigits : 10;

      // Convert the string to an actual float number. If ti fails, the value is NAN
      const value = Number(control.value);
      const checkNumOfDecimals = RegExp(
        `^-?([0-9]*)(\\.{${!!decimals ? 1 : 0}}[0-9]{0,${decimals}})?$`
      );
      const checkNumOfDigits = RegExp(
        `^-?([0-9]{0,${digits}})(\\.|\\.[0-9]+)?$`
      );

      if (isNaN(value)) {
        return {
          isNumber: {
            notANumber: true,
            enteredValue: value,
          },
        };
      }

      if (!checkNumOfDecimals.test(control.value)) {
        return {
          isNumber: {
            decimalPlaces: decimals,
            enteredValue: control.value,
          },
        };
      }

      if (!checkNumOfDigits.test(control.value)) {
        return {
          isNumber: {
            digitsPlaces: digits,
            enteredValue: control.value,
          },
        };
      }

      return null;
    };
  }

  /* Validates that a field has the required other field
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   title: 'Column Id'
   *   validators: [WRISValidators.requireOtherFieldIfNonNull('columnId', 'Column Id')]
   * },
   */
  static requireOtherFieldIfNonNull(columnId: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // update the other field
      // if this one has data and the other does not
      // or if this one doesn't have data and the other field says it does
      const nonNull = control.value != null && control.value !== '';
      const otherNonNull =
        control.parent?.controls[columnId]?.value != null &&
        control.parent?.controls[columnId]?.value !== '';
      if (
        control.parent?.controls[columnId] != null &&
        control.dirty &&
        ((nonNull &&
          !otherNonNull &&
          control.parent?.controls[columnId]?.valid) ||
          (!nonNull && !control.parent?.controls[columnId]?.valid))
      ) {
        control.parent.controls[columnId].updateValueAndValidity();
      }
      // check if the field
      if (!nonNull && otherNonNull) {
        control.markAsTouched();
        return {
          requireOtherFieldIfNonNull: true,
        };
      }
      return null;
    };
  }

  /* Update other field when this one changes, for use with requireOtherFieldIfNonNull
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   title: 'Column Id'
   *   validators: [WRISValidators.updateValidityOfOtherField('columnId')]
   * },
   */
  static updateValidityOfOtherField(columnId: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // update the other field
      // if this one has data and the other does not
      // or if this one doesn't have data and the other field says it does
      const nonNull = control.value != null && control.value !== '';
      const otherNonNull =
        control.parent?.controls[columnId]?.value != null &&
        control.parent?.controls[columnId]?.value !== '';
      if (
        control.parent?.controls[columnId] != null &&
        control.dirty &&
        ((nonNull &&
          !otherNonNull &&
          control.parent?.controls[columnId]?.valid) ||
          (!nonNull && !control.parent?.controls[columnId]?.valid))
      ) {
        control.parent.controls[columnId].updateValueAndValidity();
      }
      return null;
    };
  }
  /*
   * Same as the above two functions, just with multiple values
   */
  static requireOtherFieldsIfAnyNonNull(...columnIds: string[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // update the other fields
      // if this one has data and the other does not
      // or if this one doesn't have data and the other field says it does
      for (const c of columnIds) {
        const nonNull = control.value != null && control.value !== '';
        const otherNonNull =
          control.parent?.controls[c]?.value != null &&
          control.parent?.controls[c]?.value !== '';
        if (
          control.parent?.controls[c] != null &&
          control.dirty &&
          ((nonNull && !otherNonNull && control.parent?.controls[c]?.valid) ||
            (!nonNull && !control.parent?.controls[c]?.valid))
        ) {
          control.parent.controls[c].updateValueAndValidity();
        }
        // check if the field
        if (!nonNull && otherNonNull) {
          control.markAsTouched();
          return {
            requireOtherFieldsIfAnyNonNull: true,
          };
        }
      }
      return null;
    };
  }
  static updateValidityOfOtherFields(...columnIds: string[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // update the other field
      // if this one has data and the other does not
      // or if this one doesn't have data and the other field says it does
      for (const c of columnIds) {
        const nonNull = control.value != null && control.value !== '';
        const otherNonNull =
          control.parent?.controls[c]?.value != null &&
          control.parent?.controls[c]?.value !== '';
        if (
          control.parent?.controls[c] != null &&
          control.dirty &&
          ((nonNull && !otherNonNull && control.parent?.controls[c]?.valid) ||
            (!nonNull && !control.parent?.controls[c]?.valid))
        ) {
          control.parent.controls[c].updateValueAndValidity();
        }
      }
      return null;
    };
  }

  /* Validates that a field is after another
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   title: 'Column Id'
   *   type: FormFieldTypeEnum.Date // both columns must be dates
   *   validators: [WRISValidators.afterOtherField('columnId', 'Column Id')]
   * },
   */
  static notAllowedIfAnyOtherFieldsNonNull(
    ...columns: { columnId: string; title: string }[]
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // update the other fields
      // if this one has data and the other does not
      // or if this one doesn't have data and the other field says it does
      for (const column of columns) {
        const c = column.columnId;
        const nonNull = control.value != null && control.value !== '';
        const otherNonNull =
          control.parent?.controls[c]?.value != null &&
          control.parent?.controls[c]?.value !== '';
        if (
          control.parent?.controls[c] != null &&
          control.dirty &&
          ((nonNull && !otherNonNull && control.parent?.controls[c]?.valid) ||
            (!nonNull && !control.parent?.controls[c]?.valid))
        ) {
          control.parent.controls[c].updateValueAndValidity();
        }
        // check if the field
        if (nonNull && otherNonNull && control.parent?.controls[c]?.valid) {
          return {
            notAllowedIfAnyOtherFieldsNonNull: true,
            title: column.title,
          };
        }
      }
      return null;
    };
  }

  /* Validates that a field is before another
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   title: 'Column Id'
   *   type: FormFieldTypeEnum.Date // both columns must be dates
   *   validators: [WRISValidators.beforeOtherField('columnId', 'Column Id')]
   * },
   */
  static beforeOtherField(columnId: string, title: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (
        isEmptyInputValue(control.value) ||
        isEmptyInputValue(control.parent?.controls[columnId]?.value)
      ) {
        return null;
      }

      const thisDate = checkAndConvertDate(control.value);
      const otherDate = checkAndConvertDate(
        control.parent.controls[columnId].value
      );

      if (thisDate.isAfter(otherDate)) {
        return {
          beforeOtherField: true,
          title,
        };
      }

      // If the "other" field has been changed (dirty) and is invalid, revalidate with new value
      if (
        !control.parent.controls[columnId].valid &&
        control.parent.controls[columnId].dirty
      ) {
        control.parent.controls[columnId].updateValueAndValidity();
      }
      return null;
    };
  }

  /* Validates that a field is after another
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   title: 'Column Id'
   *   type: FormFieldTypeEnum.Date // both columns must be dates
   *   validators: [WRISValidators.afterOtherField('columnId', 'Column Id')]
   * },
   */
  static afterOtherField(columnId: string, title: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (
        isEmptyInputValue(control.value) ||
        isEmptyInputValue(control.parent?.controls[columnId]?.value)
      ) {
        return null;
      }

      const thisDate = checkAndConvertDate(control.value);
      const otherDate = checkAndConvertDate(
        control.parent.controls[columnId].value
      );

      if (thisDate.isBefore(otherDate)) {
        return {
          afterOtherField: true,
          title,
        };
      }

      // If the "other" field has been changed (dirty) and is invalid, revalidate with new value
      if (
        !control.parent.controls[columnId].valid &&
        control.parent.controls[columnId].dirty
      ) {
        control.parent.controls[columnId].updateValueAndValidity();
      }
      return null;
    };
  }

  /* Validates if the line contains a newline. If it does, throws an error
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.preventNewLineCharacter]
   * },
   */
  static preventNewLineCharacter(
    control: AbstractControl
  ): ValidationErrors | null {
    if (isEmptyInputValue(control.value)) {
      // don't validate empty values to allow optional controls
      return null;
    }

    // Regex looks for line breaks and carriage returns.
    const regex = RegExp('^[^\r\n]*$', 'g');
    return regex.test(control.value) ? null : { preventNewLineCharacter: true };
  }

  /* Validates a date(time) field value is after a specific date
   * Return format varies if the dateTime flag is True
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.afterDate("01/02/2003")]
   * },
   */
  static afterDate(
    minDate: string | moment.Moment,
    dateTime?: boolean
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const date = checkAndConvertDate(control.value);
      const minimumDate = checkAndConvertDate(minDate);
      if (!dateTime) {
        minimumDate.hour(0);
        minimumDate.minute(0);
        minimumDate.second(0);
      }

      return date.isBefore(minimumDate)
        ? {
            afterDate: {
              minDate: minimumDate.format(
                dateTime ? dateTimeDisplayFormat : dateDisplayFormat
              ),
              enteredValue: date.format(
                dateTime ? dateTimeDisplayFormat : dateDisplayFormat
              ),
            },
          }
        : null;
    };
  }

  /* Validates a date(time) field value is after today
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.dateAfterToday]
   * },
   */
  static dateAfterToday(control: AbstractControl): ValidationErrors | null {
    if (isEmptyInputValue(control.value)) {
      // don't validate empty values to allow optional controls
      return null;
    }

    const date = checkAndConvertDate(control.value);
    return date.isBefore()
      ? { dateAfterToday: { enteredValue: date.format(dateDisplayFormat) } }
      : null;
  }

  /* Validates a date(time) field value is before a specific date
   * Return format varies if the dateTime flag is True
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.beforeDate("01/02/2003")]
   * },
   */
  static beforeDate(
    maxDate: string | moment.Moment,
    dateTime?: boolean
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const date = checkAndConvertDate(control.value);
      const maximumDate = checkAndConvertDate(maxDate);
      if (!dateTime) {
        maximumDate.hour(0);
        maximumDate.minute(0);
        maximumDate.second(0);
      }

      return date.isAfter(maximumDate)
        ? {
            beforeDate: {
              maxDate: maximumDate.format(
                dateTime ? dateTimeDisplayFormat : dateDisplayFormat
              ),
              enteredValue: date.format(
                dateTime ? dateTimeDisplayFormat : dateDisplayFormat
              ),
            },
          }
        : null;
    };
  }

  /* Validates a date(time) field value is before today
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.dateBeforeToday]
   * },
   */
  static dateBeforeToday(control: AbstractControl): ValidationErrors | null {
    if (isEmptyInputValue(control.value)) {
      // don't validate empty values to allow optional controls
      return null;
    }

    const date = checkAndConvertDate(control.value);
    return date.isAfter()
      ? { dateBeforeToday: { enteredValue: date.format(dateDisplayFormat) } }
      : null;
  }

  /* Validates an input value is a valid date/time (e.g. MM/DD/YYYY HH:mm)
   * This is used internally
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.dateTime]
   * },
   */
  static dateTime(control: AbstractControl): ValidationErrors | null {
    if (isEmptyInputValue(control.value)) {
      // don't validate empty values to allow optional controls
      return null;
    }

    let dateTimeString = control.value as string;

    // remove the trailing spaces before validation checks if only a date was entered
    if (dateTimeString.length === 11) {
      dateTimeString = dateTimeString.trim();
    }

    const enteredDate = moment(dateTimeString, dateFormats, true);
    const enteredDateTime = moment(dateTimeString, dateTimeFormats, true);
    const enteredDateTimeMissingSpace = moment(
      dateTimeString,
      dateTimeMissingSpaceFormats,
      true
    );

    // If a valid date/time was entered, validator is correct - return
    if (enteredDateTime.isValid()) {
      return null;
    }

    /*
     * 1. Check is a space exists between date and time
     * 2. Check is a valid date is entered
     * 3. Check if a time is entered
     * 4. If these all fail, show the general message
     */
    if (enteredDateTimeMissingSpace.isValid()) {
      return {
        dateTime: {
          message: 'Space required between date and time',
        },
      };
    } else if (!enteredDate.isValid()) {
      return {
        dateTime: {
          message: 'Enter valid date and time',
          enteredValue: control.value,
        },
      };
    } else if (dateTimeString.length < 12) {
      return {
        dateTime: { message: 'Time is required', enteredValue: control.value },
      };
    } else {
      return {
        dateTime: {
          message: 'Enter valid date and time',
          enteredValue: control.value,
        },
      };
    }
  }

  /* Validates an input value is either a valid date/time or a valid date
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.dateOrDateTime]
   * },
   */
  static dateOrDateTime(control: AbstractControl): ValidationErrors | null {
    if (isEmptyInputValue(control.value)) {
      return null;
    }

    const enteredDate = moment(
      typeof control.value === 'string' ? control.value.trim() : control.value,
      dateFormats,
      true
    );

    return enteredDate.isValid() ? null : WRISValidators.dateTime(control);
  }

  /* Validates an input value is a valid time
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.dateOrDateTime]
   * },
   */

  static validTime(control: AbstractControl): ValidationErrors | null {
    if (isEmptyInputValue(control.value)) {
      return null;
    }

    const enteredTime = moment(
      typeof control.value === 'string' ? control.value.trim() : control.value,
      'H:m',
      true
    );

    if (enteredTime.isValid()) {
      return null;
    } else {
      return { invalidTime: { enteredValue: control.value } };
    }
  }

  /* Validates an input value is in a name/value array
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.matchToSelectArray(array: SelectInterface[])]
   * },
   */
  static matchToSelectArray(selectArr: SelectInterface[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value) {
        const selectValue = String(control.value).toUpperCase();
        const selectedOrNot = selectArr.filter(
          (x) => String(x.value) === selectValue
        );
        return selectedOrNot.length > 0
          ? null
          : { matchToSelectArray: { enteredValue: selectValue } };
      }
    };
  }

  /* Validates an input value matches the is in a name/value array
   * Add to your column config array like so
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.matchStateCountyIdToId(countiesArr: CountiesRowInterface[])]
   * },
   */
  static matchStateCountyIdToId(
    countiesArr: CountiesRowInterface[]
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (isEmptyInputValue(control.value)) {
        // don't validate empty values to allow optional controls
        return null;
      }

      if ((control.value as string).length > 1) {
        return !countiesArr.filter(
          (x) =>
            x.stateCountyNumber === (control.value as string).substring(0, 2)
        ).length
          ? { matchStateCountyIdToId: { enteredValue: control.value } }
          : null;
      } else {
        return null;
      }
    };
  }

  static isGeocode(control: AbstractControl): ValidationErrors | null {
    if (isEmptyInputValue(control.value)) {
      return null;
    }

    const rawGeocode: string = control.value.replace(/\-/g, '');
    const regex = RegExp(/\d{13}[ \S]{4}/, 'g');

    if (!regex.test(rawGeocode)) {
      return { isNotGeocode: true };
    } else if (rawGeocode.length !== 17) {
      return { invalidGeocodeLength: true };
    } else {
      return null;
    }
  }

  // File Upload Validators
  static uploadFileType(name: string, mimeType: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value == null || !(control.value instanceof File)) {
        return null;
      }
      if (control.value.type !== mimeType) {
        return { errorMessage: `Only ${name} files are allowed` };
      }
      return null;
    };
  }

  static uploadFileMaxSize(size: string): ValidatorFn {
    const maxSize = getBytesFromReadable(size);
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value == null || !(control.value instanceof File)) {
        return null;
      }
      if (control.value.size > maxSize) {
        return { errorMessage: `The maximum size is ${size}` };
      }
      return null;
    };
  }

  static requireOneOtherField(
    columnId: string,
    message: string,
    ...otherFields: string[]
  ): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null =>
      !formGroup.get(columnId)?.errors &&
      otherFields.every(
        (field: string) =>
          formGroup.get(field)?.value == null ||
          formGroup.get(field)?.value === undefined
      )
        ? {
            message,
          }
        : null;
  }

  /* Validates an input year or date is greater than a minmum value
   * Add to your column config array. The column being validated must either be a valid date or a number
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.minimumYear(year: number)]
   * },
   */
  static minimumYear(year: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (isEmptyInputValue(control.value)) {
        // don't validate empty values to allow optional controls
        return null;
      }

      // If the control value is a Moment date use the year() method; otherwie assume it's a number
      const isValid = moment.isMoment(control.value)
        ? control.value.year() < year
        : control.value < year;

      return isValid
        ? {
            errorMessage: `The Year must be on or after ${year}`,
          }
        : null;
    };
  }

  /* Validates an input year or date is less than a minmum value
   * Add to your column config array. The column being validated must either be a valid date or a number
   *
   * {
   *   columnId: 'columnId'
   *   validators: [WRISValidators.maximumYear(year: number)]
   * },
   */
  static maximumYear(year: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (isEmptyInputValue(control.value)) {
        // don't validate empty values to allow optional controls
        return null;
      }

      // If the control value is a Moment date use the year() method; otherwie assume it's a number
      const isValid = moment.isMoment(control.value)
        ? control.value.year() > year
        : control.value > year;

      return isValid
        ? {
            errorMessage: `The Year must be on or before ${year}`,
          }
        : null;
    };
  }
}
