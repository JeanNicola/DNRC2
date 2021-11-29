import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-appl-wr-data-table',
  templateUrl: './appl-wr-data-table.component.html',
  styleUrls: [
    '../../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [DatePipe, CurrencyPipe],
})
export class ApplWrDataTableComponent extends DataTableComponent {
  constructor(datePipe: DatePipe, currencyPipe: CurrencyPipe) {
    super(datePipe, currencyPipe);
  }
}
