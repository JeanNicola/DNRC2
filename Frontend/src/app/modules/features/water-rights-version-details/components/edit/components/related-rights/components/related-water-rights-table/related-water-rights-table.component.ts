import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { RelatedWaterRightsService } from 'src/app/modules/shared/services/related-water-rights.service';

@Component({
  selector: 'app-related-water-rights-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './related-water-rights-table.component.scss',
  ],
  providers: [RelatedWaterRightsService],
})
export class RelatedWaterRightsTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: RelatedWaterRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined)) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public title = 'Related Water Rights';
  public hideActions = true;
  public hideInsert = true;
  public hideEdit = true;
  public searchable = false;
  public clickableRow = true;
  public dblClickableRow = true;
  public primarySortColumn = 'completeWaterRightNumber';
  public sortDirection = 'asc';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'completeWaterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: { returnVersions: 'Y' },
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'status',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'version',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public onRowDoubleClick(data: any) {
    this.router.navigate(['wris', 'water-rights', data.waterRightId]);
  }

  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
