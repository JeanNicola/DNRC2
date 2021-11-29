import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { DataRowComponent } from '../../../data-row/data-row.component';
import { Office } from '../../file-location-processor.component';

@Component({
  selector: 'app-responsible-office',
  templateUrl: '../../../data-row/data-row.component.html',
  styleUrls: ['../../../data-row/data-row.component.scss'],
  providers: [BaseDataService],
})
export class ResponsibleOfficeComponent extends DataRowComponent {
  @Input() officeSubject: ReplaySubject<any>;
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
      title: 'Responsible Office',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'office',
      title: 'Responsible Office',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 400,
    },
  ];
  public dialogWidth = '400px';

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

    this.observables.endpoint = new ReplaySubject(1);
  }

  public populateDropdowns(): void {
    this.observables.offices = new ReplaySubject(1);

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
  }
}
