import { Component, OnInit } from '@angular/core';
import { ControlContainer, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-form-errors',
  templateUrl: './form-errors.component.html',
  styleUrls: ['./form-errors.component.scss'],
})
export class FormErrorsComponent implements OnInit {
  public form: FormGroup;

  constructor(private parentContainer: ControlContainer) {}

  ngOnInit(): void {
    this.form = this.parentContainer.control as FormGroup;
  }
}
