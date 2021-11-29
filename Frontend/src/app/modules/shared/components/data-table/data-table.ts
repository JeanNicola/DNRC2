import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  QueryList,
  SimpleChanges,
  TemplateRef,
  ViewChildren,
} from '@angular/core';
import { Sort } from '@angular/material/sort';
import { ColumnDefinitionInterface } from '../../interfaces/column-definition.interface';
import { PermissionsInterface } from '../../interfaces/permissions.interface';
// Imports are used in template
import {
  dateFormatString,
  dateTimeFormatString,
  monthDayDateFormatString,
} from 'src/app/modules/shared/constants/date-format-strings';
import { CurrencyPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.html',
  styleUrls: ['./data-table.scss'],
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
export class DataTableComponent implements OnInit, OnChanges {
  @Input() public data: any[];
  @Input() public selectedRow: number;
  // @Input() public columns: ColumnDefinitionInterface[];
  protected _columns: ColumnDefinitionInterface[];
  @Input() set columns(value: ColumnDefinitionInterface[]) {
    this._columns = value;
    // Set the table columns to display and add the "Action Button" column
    this.displayedColumns = value
      .filter((item) =>
        item?.displayInTable == null ? true : item?.displayInTable
      )
      .map((item) => item.columnId);

    // Add column at end of table to contain action buttons
    this.displayedColumns.push('actions');
  }
  get columns(): ColumnDefinitionInterface[] {
    return this._columns;
  }
  @Input() public permissions: PermissionsInterface = {
    canGET: false,
    canPOST: false,
    canDELETE: false,
    canPUT: false,
  };
  @Input() public hideActions = false;
  @Input() public hideEdit = false;
  @Input() public hideDelete = false;
  @Input() public hideHeader = false;
  @Input() public expandChildRef: TemplateRef<any>;
  @Input() public clickableRow = false;
  @Input() public dblClickableRow = false;
  @Input() public zHeight = 8;
  @Input() public highlightOneRow = true;
  @Input() public highlightFirstRowOnInit = false;
  @Input() public isInMain = true;
  @Input() public primarySortColumn: string;
  @Input() public primaryDirection: 'asc' | 'desc';
  @Input() public noSort = false;
  @Input() public selectedRowStyles;
  @Input() public enableMoreInfo: boolean;
  @Output() sortEvent: EventEmitter<Sort> = new EventEmitter<Sort>();
  @Output() insertEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() editEvent: EventEmitter<number> = new EventEmitter<number>();
  @Output() deleteEvent: EventEmitter<number> = new EventEmitter<number>();
  @Output() rowClickEvent: EventEmitter<number> = new EventEmitter<number>();
  @Output() cellClickEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() cellDblClickEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() rowDblClickEvent: EventEmitter<number> = new EventEmitter<number>();
  @Output() moreInfoEvent: EventEmitter<number> = new EventEmitter<number>();

  @ViewChildren('row', { read: ElementRef }) rows: QueryList<ElementRef>;
  @ViewChildren('deleteButtons', { read: ElementRef })
  deleteButtons: QueryList<ElementRef>;
  @ViewChildren('editButtons', { read: ElementRef })
  editButtons: QueryList<ElementRef>;

  // The list of columns to display in the data table
  protected displayedColumns: string[];

  // Strings used for formatting with template date pipe
  public dateFormatString = dateFormatString;
  public dateTimeFormatString = dateTimeFormatString;

  constructor(private datePipe: DatePipe, private currencyPipe: CurrencyPipe) {}

  ngOnInit(): void {
    this.columns = this.columns;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.selectedRow) {
      this.selectedRow = changes.selectedRow.currentValue;
    } else if (
      this.highlightFirstRowOnInit &&
      changes?.data?.currentValue?.length
    ) {
      this.selectedRow = 0;
    }
  }

  focusFirstElement(): void {
    setTimeout(() => {
      if (
        this.rows.first &&
        (this.dblClickableRow || this.clickableRow || this.highlightOneRow)
      ) {
        this.rows.first.nativeElement.focus();
      } else if (this.editButtons.first && this.permissions.canPUT) {
        this.editButtons.first.nativeElement.focus();
      } else if (this.deleteButtons.first && this.permissions.canDELETE) {
        this.deleteButtons.first.nativeElement.focus();
      }
    }, 0);
  }

  focusRowByIndex(index: number): void {
    if (
      this.rows.get(index) &&
      (this.dblClickableRow || this.clickableRow || this.highlightOneRow)
    ) {
      this.rows.get(index).nativeElement.focus();
    }
  }

  /*
   * Event handler for edit button - send this back to main component to start edit process
   * "row" is the row numer of the data in the main component
   */
  public selectRow(row: number): void {
    this.editEvent.emit(row);
  }

  public onSortData(sort: Sort): void {
    if (sort) {
      this.sortEvent.emit(sort);
    }
  }

  /*
   * Event handler for delete button - send this back to main component to start delete process
   * "row" is the row numer of the data in the main component
   */
  public deleteRow($event: Event, row: number): void {
    $event.stopPropagation();
    this.deleteEvent.emit(row);
  }

  /*
   * Event handler for edit button - send this back to main component to start edit process
   * "row" is the row numer of the data in the main component
   */
  public editRow($event: Event, row: number): void {
    $event.stopPropagation();
    this.editEvent.emit(row);
  }

  public dblclickRow($event: Event, row: number): void {
    this.rowDblClickEvent.emit(row);
    $event.preventDefault();
  }

  public clickRow($event: Event, row: number): void {
    this.selectedRow = row;
    this.rowClickEvent.emit(row);
    $event.preventDefault();
  }

  public clickCell(row: number, columnId: string): void {
    this.cellClickEvent.emit({
      cell: this.data[row] ? this.data[row][columnId] : null,
      columnId,
      row,
    });
  }

  public dblClickCell(row: number, columnId: string): void {
    // this ensures this.data is an array
    if (Array.isArray(this.data)) {
      this.cellDblClickEvent.emit({
        cell: this.data[row][columnId],
        columnId,
        row,
      });
    }
  }

  protected dblClickableCell(column: ColumnDefinitionInterface): boolean {
    return !!column?.dblClickable;
  }

  public moreInfo(row: number): void {
    this.moreInfoEvent.emit(row);
  }

  // This is to show the circular button when you hover on the cell
  toggleCounterForRow(event: Event): void {
    const counterElement = (event.target as HTMLElement).querySelector(
      '.counter-btn'
    );
    if (counterElement) {
      counterElement.classList.toggle('active');
    }
  }

  public formatCellText(value: any, type: string): string {
    switch (type) {
      case 'datetime':
        return this.datePipe.transform(value, dateTimeFormatString);
      case 'date':
        return this.datePipe.transform(value, dateFormatString);
      case 'month_day_date':
        return this.datePipe.transform(value, monthDayDateFormatString);
      case 'currency':
        return this.currencyPipe.transform(value);
      case 'checkbox':
        return value ? 'check_box' : 'check_box_outline_blank';
      default:
        return value as string;
    }
  }
}
