import { Component, Input, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { PopulateByGeocodesService } from '../../../../services/populate-by-geocodes.service';

@Component({
  selector: 'app-water-right-row',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [PopulateByGeocodesService],
})
export class WaterRightRowComponent
  extends DataRowComponent
  implements OnDestroy
{
  constructor(
    public service: PopulateByGeocodesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() data = {};
  @Input() displayData = {};

  public showEdit = false;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'version',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
      width: 80,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type Description',
      type: FormFieldTypeEnum.Input,
      width: 300,
    },
  ];

  protected _getHelperFunction(data: any): { [key: string]: any } {
    return {
      ...data.get,
    };
  }

  public ngOnDestroy() {}
}
