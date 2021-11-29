import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-applications-data-table',
  templateUrl: './applications-data-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [CurrencyPipe, DatePipe],
})
export class ApplicationsDataTableComponent extends DataTableComponent {
  @Input() showScannedUrl = false;

  constructor(datePipe: DatePipe, currencyPipe: CurrencyPipe) {
    super(datePipe, currencyPipe);
  }
}
