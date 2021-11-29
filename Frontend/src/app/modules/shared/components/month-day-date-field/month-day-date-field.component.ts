import { Component } from '@angular/core';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { DataManagementFormFieldComponent } from '../data-form-field/data-form-field.component';

export const MONTH_DAY_FORMAT = {
  parse: {
    dateInput: 'M/D',
  },
  display: {
    dateInput: 'MM/DD',
    monthYearLabel: 'MM/DD',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MM/DD',
  },
};

@Component({
  selector: 'app-month-day-date-field',
  templateUrl: './month-day-date-field.component.html',
  styleUrls: [
    './month-day-date-field.component.scss',
    '../data-form-field/data-form-field.component.scss',
  ],
  providers: [{ provide: MAT_DATE_FORMATS, useValue: MONTH_DAY_FORMAT }],
})
export class MonthDayDateFieldComponent extends DataManagementFormFieldComponent {}
