import { CurrencyPipe, DatePipe } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-place-of-use-data-table',
  templateUrl: './place-of-use-data-table.component.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [CurrencyPipe, DatePipe],
})
export class PlaceOfUseDataTableComponent
  extends DataTableComponent
  implements OnChanges
{
  @Output() copyEvent: EventEmitter<number> = new EventEmitter<number>();
  @Input() resetRowIndex: boolean = null;
  private selectedRowNumber: number = null;

  public copyRow($event: Event, row: number): void {
    $event.stopPropagation();
    this.copyEvent.emit(row);
  }

  public ngOnChanges(changes: SimpleChanges): void {
    if (
      this.highlightFirstRowOnInit &&
      changes?.data?.currentValue?.length &&
      this.resetRowIndex
    ) {
      this.selectedRow = changes?.data?.currentValue[0];
      this.selectedRowNumber = 0;
    } else {
      this.selectedRow = this.data[this.selectedRowNumber];
    }
  }

  public clickRow($event: Event, row: number): void {
    this.selectedRowNumber = row;
    this.selectedRow = this.data[row];
    this.rowClickEvent.emit(row);
    $event.preventDefault();
  }
}
