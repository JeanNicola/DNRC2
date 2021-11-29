import { Component, Input, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { GeocodesService } from '../../services/geocodes.service';

@Component({
  selector: 'app-geocodes-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [GeocodesService],
})
export class GeocodesTableComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  constructor(
    public service: GeocodesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() waterRightId;

  protected clickableRow = false;
  protected dblClickableRow = false;
  protected searchable = false;
  public sortDirection = 'desc';
  public hideEdit = true;
  public hideDelete = true;
  public hideActions = true;
  public hideInsert = true;
  public isInMain = false;
  public title = '';
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'formattedGeocode',
      title: 'Geocode',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'valid',
      title: 'Geocode Validity Status',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected initFunction(): void {
    this.idArray = [this.waterRightId];
    this._get();
  }

  protected _getHelperFunction(data: any): any {
    if (data.get.results?.length) {
      data.get.results = data.get.results.map((r) => ({
        ...r,
        valid: r.valid ? 'VALID' : 'INVALID',
      }));
    }
    return data.get;
  }

  public ngOnDestroy() {}
}
