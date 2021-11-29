import { Component, Input, OnInit } from '@angular/core';
import { ControlContainer, FormGroup } from '@angular/forms';
import { ColumnDefinitionInterface } from '../../interfaces/column-definition.interface';

@Component({
  selector: 'shared-data-field-errors',
  templateUrl: './data-field-errors.component.html',
  styleUrls: ['./data-field-errors.component.scss'],
})
export class DataFieldErrorsComponent implements OnInit {
  @Input() public field: ColumnDefinitionInterface;
  public form: FormGroup;

  constructor(private parentContainer: ControlContainer) {}

  public ngOnInit(): void {
    this.form = this.parentContainer.control as FormGroup;
  }
}
