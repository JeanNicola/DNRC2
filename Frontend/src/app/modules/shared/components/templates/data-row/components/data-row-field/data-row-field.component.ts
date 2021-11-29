import { Component, Input, OnInit } from '@angular/core';
import { Moment } from 'moment';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'shared-data-row-field',
  templateUrl: './data-row-field.component.html',
  styleUrls: ['./data-row-field.component.scss'],
})
export class DataRowFieldComponent implements OnInit {
  @Input() field: ColumnDefinitionInterface;
  @Input() dataValue: any;
  @Input() noTitle?: boolean = false;
  @Input() isDisabled?: boolean = false;
  @Input() removePadding?: boolean = false;
  @Input() inputStyles? = {};

  public fieldTypes: any;

  ngOnInit(): void {
    // Get the field types enum for use in the template
    this.fieldTypes = FormFieldTypeEnum;
  }

  getInputStyles() {
    return {
      fontWeight: this.field?.fontWeight,
      ...this.inputStyles,
    };
  }

  getDate(date: Moment) {
    if (date?.toDate) {
      return new Date(
        date.toDate().getTime() + date.toDate().getTimezoneOffset() * 60000
      );
    } else {
      return date;
    }
  }
}
