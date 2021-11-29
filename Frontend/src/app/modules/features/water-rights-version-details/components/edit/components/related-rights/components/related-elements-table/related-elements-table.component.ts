import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { RelatedElementsService } from 'src/app/modules/shared/services/related-elements.service';

@Component({
  selector: 'app-related-elements-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './related-elements-table.component.scss',
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [RelatedElementsService],
})
export class RelatedElementsTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: RelatedElementsService,
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

  public title = 'Related Elements';
  public hideActions = true;
  public hideInsert = true;
  public searchable = false;
  public hideEdit = true;
  public primarySortColumn = 'elementTypeValue';
  public sortDirection = 'asc';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'elementTypeValue',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'elementTypeValue',
      title: 'Element',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
