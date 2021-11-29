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

import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'app-customers-table',
  templateUrl: './customers-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [DatePipe, CurrencyPipe],
})
export class CustomersTableComponent
  extends DataTableComponent
  implements OnInit, OnChanges {
  @Output() public onRowStateChanged: EventEmitter<any> = new EventEmitter();
  @Input() public form: FormGroup;

  public fieldTypes = FormFieldTypeEnum;
  public hideActions = true;
  public hideEdit = false;
  public hideDelete = false;
  public hideHeader = false;
  public primarySortColumn = 'name';
  public sortDirection = 'asc';
  public isInMain = false;

  constructor(
    datePipe: DatePipe,
    currencyPipe: CurrencyPipe,
    private parentContainer: ControlContainer
  ) {
    super(datePipe, currencyPipe);
  }

  @Input() set columns(value: ColumnDefinitionInterface[]) {
    if (!value) return;

    this._columns = value;

    // Set the table columns to display and add the "Action Button" column
    this.displayedColumns = value
      .filter((item) =>
        item?.displayInTable == null ? true : item?.displayInTable
      )
      .map((item) => item.columnId);

    // Add column at end of table to contain action buttons
    if (!this.hideActions) {
      this.displayedColumns.push('actions');
    }
  }
  get columns(): ColumnDefinitionInterface[] {
    return [...this._columns];
  }

  ngOnInit() {
    this.form = this.parentContainer.control as FormGroup;
    this.form.addControl('customers', new FormArray([]));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.data?.currentValue?.length) {
      // store data received from the parent
      const oldData = [...this.data.map((obj) => Object.assign({}, obj))];
      // Do not render the data until the form controls are ready
      this.data = [];
      setTimeout(() => {
        // Render form controls
        const groupItems = [];
        oldData.forEach((contact) => {
          groupItems.push(
            new FormGroup({
              contactId: new FormControl(contact.contactId),
              name: new FormControl(contact.name),
              checked: new FormControl(contact.checked),
            })
          );
        });
        // render data
        this.form.removeControl('customers');
        this.form.addControl('customers', new FormArray(groupItems));
        this.data = oldData;
      });
    }
  }

  onCheckboxClickHandler(event: MouseEvent, idx: number) {
    // Do not check the checkbox
    event.preventDefault();
    // get currentn value
    const value = ((this.form.get('customers') as FormArray).at(
      idx
    ) as FormGroup).get('checked').value;

    // Change the value manually
    ((this.form.get('customers') as FormArray).at(idx) as FormGroup)
      .get('checked')
      .setValue(!value);

    this.onRowStateChanged.emit(idx);
  }

  public clickRow() {}
}
