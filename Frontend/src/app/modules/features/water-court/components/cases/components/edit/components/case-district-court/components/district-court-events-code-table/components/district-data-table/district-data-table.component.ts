import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-district-data-table',
  templateUrl: './district-data-table.component.html',
  styleUrls: [
    '../../../../../../../../../../../../shared/components/data-table/data-table.scss',
    './district-data-table.component.scss',
  ],
  providers: [CurrencyPipe, DatePipe],
})
export class DistrictDataTableComponent extends DataTableComponent {
  constructor(datePipe: DatePipe, currencyPipe: CurrencyPipe) {
    super(datePipe, currencyPipe);
  }
}
