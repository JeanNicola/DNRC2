import { DatePipe, CurrencyPipe } from '@angular/common';
import { Component } from '@angular/core';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-pod-data-table',
  templateUrl: './pod-data-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [DatePipe, CurrencyPipe],
})
export class PodDataTableComponent extends DataTableComponent {
  constructor(datePipe: DatePipe, currencyPipe: CurrencyPipe) {
    super(datePipe, currencyPipe);
  }

  public insertRow($event: Event, row: number): void {
    $event.stopPropagation();
    this.insertEvent.emit(row);
  }
}
