import { Component, Input, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { OwnersService } from '../../services/owners.service';

@Component({
  selector: 'app-owners-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [OwnersService],
})
export class OwnersTableComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  constructor(
    public service: OwnersService,
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
  public hideEdit = true;
  public hideDelete = true;
  public hideActions = true;
  public hideInsert = true;
  public isInMain = false;
  public title = '';
  public primarySortColumn = 'name';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'name',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'name',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected initFunction(): void {
    this.idArray = [this.waterRightId];
    this._get();
  }

  public ngOnDestroy() {}
}
