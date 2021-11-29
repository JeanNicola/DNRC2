import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-geocodes-data-table',
  templateUrl: './geocodes-data-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [CurrencyPipe, DatePipe],
})
export class GeocodesDataTableComponent extends DataTableComponent {
  constructor(datePipe: DatePipe, currencyPipe: CurrencyPipe) {
    super(datePipe, currencyPipe);
  }

  public openLink(link) {
    window.open(link, '_blank');
  }
}
