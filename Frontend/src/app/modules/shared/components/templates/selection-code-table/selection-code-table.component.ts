import { Component, EventEmitter, Output } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseDataService } from '../../../services/base-data.service';
import { BaseCodeTableComponent } from '../code-table/code-table.template';

@Component({
  selector: 'app-selection-code-table',
  templateUrl: './selection-code-table.component.html',
  styleUrls: ['../code-table/code-table.template.scss'],
})
export class SelectionCodeTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: BaseDataService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() public onRowStateChanged: EventEmitter<any> = new EventEmitter();

  public checkboxesForm: FormGroup = new FormGroup({});

  protected _onRowStateChangedHandler(idx) {
    let row = this.rows[idx];
    let formGroup = (this.checkboxesForm.get('rows') as FormArray).at(idx);

    this.onRowStateChanged.emit({ row, formGroup });
  }
}
