import { CurrencyPipe, DatePipe } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import {
  ControlContainer,
  FormArray,
  FormControl,
  FormGroup,
} from '@angular/forms';
import { ColumnDefinitionInterface } from '../../../interfaces/column-definition.interface';
import { DataTableComponent } from '../../data-table/data-table';

@Component({
  selector: 'app-selection-data-table',
  templateUrl: './selection-data-table.component.html',
  styleUrls: ['../../data-table/data-table.scss'],
  providers: [DatePipe, CurrencyPipe],
})
export class SelectionDataTableComponent
  extends DataTableComponent
  implements OnInit, OnChanges {
  constructor(
    datePipe: DatePipe,
    currencyPipe: CurrencyPipe,
    private parentContainer: ControlContainer
  ) {
    super(datePipe, currencyPipe);
  }

  // Event that fires whenever the checkboxes change
  @Output() public onRowStateChanged: EventEmitter<any> = new EventEmitter();
  // Name for the formArray
  // Set all the columns except the actions
  @Input() set columns(value: ColumnDefinitionInterface[]) {
    if (!value) return;

    this._columns = value;

    // Set the table columns to display and add the "Action Button" column
    this.displayedColumns = value
      .filter((item) =>
        item?.displayInTable == null ? true : item?.displayInTable
      )
      .map((item) => item.columnId);

    this.displayedColumns.push('actions');
  }

  public form: FormGroup;
  get columns(): ColumnDefinitionInterface[] {
    return [...this._columns];
  }

  public ngOnInit(): void {
    this.form = this.parentContainer.control as FormGroup;
    this.form.addControl('rows', new FormArray([]));
    super.ngOnInit();
  }

  public ngOnChanges(changes: SimpleChanges) {
    if (changes.data?.currentValue?.length) {
      // store data received from the parent
      const oldData = [...this.data.map((obj) => Object.assign({}, obj))];
      // Do not render the data until the form controls are ready
      this.data = [];
      setTimeout(() => {
        // Render form controls
        const groupItems = [];
        oldData.forEach((record) => {
          const formGroupObject = {};
          Object.keys(record).map((key) => {
            formGroupObject[key] = new FormControl(record[key]);
          });

          groupItems.push(
            new FormGroup({
              ...formGroupObject,
              checked: new FormControl(record.checked),
            })
          );
        });
        // render data
        this.form.removeControl('rows');
        this.form.addControl('rows', new FormArray(groupItems));
        this.data = oldData;
      });
    }
  }

  public onCheckboxClickHandler(event: MouseEvent, idx: number) {
    // Do not check the checkbox
    event.preventDefault();
    // get currentn value
    const value = ((this.form.get('rows') as FormArray).at(
      idx
    ) as FormGroup).get('checked').value;

    // Change the value manually
    ((this.form.get('rows') as FormArray).at(idx) as FormGroup)
      .get('checked')
      .setValue(!value);

    this.onRowStateChanged.emit(idx);
  }
  public clickRow($event: Event, row: number): void {}
}
