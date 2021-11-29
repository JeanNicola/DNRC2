import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { ControlContainer, FormGroup } from '@angular/forms';
import { ColumnDefinitionInterface } from '../../interfaces/column-definition.interface';
import { DataManagementDialogModes } from '../dialogs/data-management/data-management-dialog.enum';

@Component({
  selector: 'app-file-form-field',
  templateUrl: './file-form-field.component.html',
  styleUrls: ['./file-form-field.component.scss']
})
export class FileFormFieldComponent implements OnInit {

  @Input() public field: ColumnDefinitionInterface;
  @Input() public mode: DataManagementDialogModes;

  @Output() public shiftTabEvent = new EventEmitter<KeyboardEvent>();

  constructor(private parentContainer: ControlContainer) { }

  public parentForm: FormGroup;
  public file: File;

  ngOnInit(): void {
    this.parentForm = this.parentContainer.control as FormGroup;
  }

  public onFileSelect($event: any) {
    this.file = $event.target.files[0];
    this.parentForm.get(this.field.columnId).setValue(this.file);
    this.parentForm.get(this.field.columnId).markAsDirty();
  }

}
