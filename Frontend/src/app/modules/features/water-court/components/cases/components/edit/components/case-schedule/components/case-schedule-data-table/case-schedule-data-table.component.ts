import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-case-schedule-data-table',
  templateUrl: './case-schedule-data-table.component.html',
  styleUrls: [
    './case-schedule-data-table.component.scss',
    '../../../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [CurrencyPipe, DatePipe],
})
export class CaseScheduleDataTableComponent extends DataTableComponent {
  constructor(datePipe: DatePipe, currencyPipe: CurrencyPipe) {
    super(datePipe, currencyPipe);
  }
}
