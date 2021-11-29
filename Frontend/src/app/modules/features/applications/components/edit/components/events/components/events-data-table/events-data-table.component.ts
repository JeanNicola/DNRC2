import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-events-data-table',
  templateUrl: './events-data-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [CurrencyPipe, DatePipe],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition(
        'expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ),
    ]),
  ],
})
export class EventsDataTableComponent extends DataTableComponent {
  @Input() public highlightOneRow = false;
  @Output() moreInfoEvent: EventEmitter<number> = new EventEmitter<number>();

  constructor(datePipe: DatePipe, currencyPipe: CurrencyPipe) {
    super(datePipe, currencyPipe);
  }

  /*
   * Event handler for more info button - send this back to main component to
   * show additional data that doesn't belong in the table in a new dialog.
   * "row" is the row numer of the data in the main component
   */
  public moreInfo(row: number): void {
    this.moreInfoEvent.emit(row);
  }
}
