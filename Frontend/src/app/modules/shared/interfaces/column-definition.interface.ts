import { ValidatorFn } from '@angular/forms';
import { Subject } from 'rxjs';

export interface ColumnDefinitionInterface {
  // Field data
  // Camelcase property name from DTO
  readonly columnId: string;
  // Pretty print name of property for UI
  title: string;
  // Width of table cell - Overrides default of 200px
  readonly width?: number;
  // Input type for <app-form-field> - Overrides default
  type: string;
  // Display a table input instead
  readonly list?: ColumnDefinitionInterface[];
  // Specifies if the column should not be sortable
  readonly noSort?: boolean;
  // Specifies if this is the primary sort column
  readonly primarySort?: boolean;
  // Specifies the default sort for this column
  readonly sortDirection?: 'asc' | 'desc';
  // Database Column Name - Overrides default of 'UPPERCASE_WITH_UNDERSCORES'
  readonly sortColumn?: string;
  // Specifies if the column should be displayed in the table
  displayInTable?: boolean;
  // Specifies if the field should be displayed in the search dialog
  displayInSearch?: boolean;
  // Specifies if the field should be displayed in the insert dialog
  displayInInsert?: boolean;
  // Specifies if the field should be displayed in the edit dialog
  displayInEdit?: boolean;
  // Width of <app-form-field> - Overrides default of 200px
  readonly formWidth?: number;
  // Specifies if the column should be disabled in the edit dialog
  editable?: boolean;
  // whether or not the individual cell is double clickable
  dblClickable?: boolean;
  // An array that contains data for select/autocomplete in <app-form-field>
  selectArr?: SelectionInterface[];
  // Validators for use in creating and editing rows.
  // Use this array to add other validators such as min/max values, numbers only patterns, etc.
  validators?: ValidatorFn[];
  // Validators for use for searching.
  // Use this array to add other validators such as min/max values, numbers only patterns, etc.
  searchValidators?: ValidatorFn[];
  // Hint for input
  readonly hint?: string;
  // Placeholder for input
  readonly placeholder?: string;
  // Font Weight (400 is normal/default)
  readonly fontWeight?: number;
  // Observable, when present should change the value of the input
  valueObservable$?: Subject<any>;
  // Decimal places for the value
  decimalPlaces?: number;
  // If true then show the counter
  showCounter?: boolean;
  // Ref to the counter value
  counterRef?: string;
  // message to display for a required field
  customErrorMessages?: any;
  // file type
  fileMimeType?: string;
}

// Selectio interface the consists of a Name/Value pair
export interface SelectionInterface {
  readonly name?: string;
  readonly value: string | number;
}
