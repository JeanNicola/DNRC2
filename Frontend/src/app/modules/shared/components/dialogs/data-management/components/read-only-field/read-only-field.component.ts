import { Component, Input, OnInit } from '@angular/core';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
// Imports are used in template
import {
  dateFormatString,
  dateTimeFormatString,
} from 'src/app/modules/shared/constants/date-format-strings';

@Component({
  selector: 'shared-read-only-field',
  templateUrl: './read-only-field.component.html',
  styleUrls: ['./read-only-field.component.scss'],
})
export class ReadOnlyFieldComponent implements OnInit {
  @Input() field: ColumnDefinitionInterface;
  public fieldTypes: any;

  // Strings used for formatting with template date pipe
  public dateFormatString = dateFormatString;
  public dateTimeFormatString = dateTimeFormatString;

  public ngOnInit(): void {
    // Get the field types enum for use in the template
    this.fieldTypes = FormFieldTypeEnum;
  }
}
