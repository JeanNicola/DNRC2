import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { WaterRightVersionsService } from '../../../applications/components/edit/components/water-rights/services/water-right-versions.service';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [WaterRightVersionsService],
})
export class SearchComponentForVersions extends BaseCodeTableComponent {
  constructor(
    public service: WaterRightVersionsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Version Details';
  public hideActions = true;
  public hideDelete = true;
  public hideInsert = true;
  public hideEdit = true;
  public dblClickableRow = true;
  public clickableRow = true;
  public primarySortColumn = 'completeWaterRightNumber';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'asc',
    sortColumn: 'completeWaterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'waterRightTypeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'completeVersion',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'operatingAuthority',
      title: 'Operating Authority Date',
      type: FormFieldTypeEnum.Date,
      displayInSearch: false,
    },
    {
      columnId: 'versionStatusDescription',
      title: 'Version Status',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];

  public initFunction(): void {
    this.dataMessage = 'Search for a Water Right Version';
  }

  private redirectToVersionEdit(waterRightId: number, version: number) {
    void this.router.navigate(['..', waterRightId, 'versions', version], {
      relativeTo: this.route,
    });
  }

  protected _getHelperFunction(data: any): any {
    if (data.get?.results?.length === 1 && data.get.currentPage === 1) {
      this.redirectToVersionEdit(
        data.get.results[0].waterRightId,
        data.get.results[0].version
      );
    }
    return data.get;
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToVersionEdit(data.waterRightId, data.version);
  }
}
