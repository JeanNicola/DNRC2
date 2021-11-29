import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { OwnershipUpdateService } from '../../../../services/ownership-update.service';
import { getOwnershipUpdateColumns } from '../../../../shared/ownership-update-columns';

@Component({
  selector: 'app-ownership-update-data-row',
  templateUrl:
    '../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [OwnershipUpdateService],
})
export class OwnershipUpdateDataRowComponent extends DataRowComponent {
  constructor(
    public service: OwnershipUpdateService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() data;
  @Input() displayData;

  public showEdit = false;
  public disableEdit = true;
  public columns: ColumnDefinitionInterface[] = [
    ...getOwnershipUpdateColumns(),
    {
      columnId: 'dateProcessed',
      title: 'Processed Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
      width: 130,
    },
    {
      columnId: 'dateTerminated',
      title: 'Terminated Date',
      type: FormFieldTypeEnum.Date,
      width: 120,
    },
  ];
}
