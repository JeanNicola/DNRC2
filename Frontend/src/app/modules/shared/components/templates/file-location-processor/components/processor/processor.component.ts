import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { DataRowComponent } from '../../../data-row/data-row.component';
import { Office, Staff } from '../../file-location-processor.component';

@Component({
  selector: 'app-processor',
  templateUrl: '../../../data-row/data-row.component.html',
  styleUrls: ['../../../data-row/data-row.component.scss'],
  providers: [BaseDataService],
})
export class ProcessorComponent extends DataRowComponent {
  @Input() officeSubject: ReplaySubject<any>;
  @Input() staffSubject: ReplaySubject<any>;
  constructor(
    public service: BaseDataService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'officeId',
      title: 'Office',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      width: 400,
      validators: [Validators.required],
    },
    {
      columnId: 'office',
      title: 'Office',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 400,
    },
    {
      columnId: 'staffId',
      title: 'Staff',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      width: 400,
      validators: [Validators.required],
    },
    {
      columnId: 'staff',
      title: 'Staff',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 400,
    },
  ];

  initFunction(): void {
    this._get();
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0),
    };
  }

  public populateDropdowns(): void {
    this.observables.offices = new ReplaySubject(1);
    this.observables.staff = new ReplaySubject(1);

    this.officeSubject.subscribe((offices: { results: Office[] }) => {
      this._getColumn('officeId').selectArr = offices.results.map(
        (office: Office) => ({
          name: office.description,
          value: office.officeId,
        })
      );
      this.observables.offices.next(offices);
      this.observables.offices.complete();
    });
    this.staffSubject.subscribe((staffMembers: { results: Staff[] }) => {
      this._getColumn('staffId').selectArr = staffMembers.results.map(
        (staff: Staff) => ({
          name: staff.name,
          value: staff.staffId,
        })
      );
      this.observables.staff.next(staffMembers);
      this.observables.staff.complete();
    });
  }
}
