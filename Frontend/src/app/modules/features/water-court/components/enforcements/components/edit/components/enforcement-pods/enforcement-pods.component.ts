import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { EnforcementPodsService } from './services/enforcement-pods.service';

@Component({
  selector: 'app-enforcement-pods',
  templateUrl: './enforcement-pods.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [EnforcementPodsService],
})
export class EnforcementPodsComponent extends BaseCodeTableComponent {
  constructor(
    public service: EnforcementPodsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public hideActions = true;
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  public isInMain = false;
  public searchable = false;
  public title = '';
  public primarySortColumn = 'completeWaterRightNumber';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'completeWaterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'completeVersion',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'podNumber',
      title: 'POD #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'legalLandDescription',
      title: 'POD Legal Land Description',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'enforcementNumber',
      title: 'Enforcement #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ditchName',
      title: 'Ditch / Diversion Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ditchLegalLandDescription',
      title: 'Ditch / Diversion Legal Land Description',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'shortComment',
      title: 'Enforcement Comment',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected initFunction(): void {
    this.idArray = [this.route.snapshot.params.areaId];
    this._get();
  }

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    return {
      ...data.get,
      results: data.get.results.map((enf) => {
        return {
          ...enf,
          shortComment: enf.comment
            ? enf.comment.substring(0, 300) +
              (enf.comment.length > 300 ? '...' : '')
            : '',
        };
      }),
    };
  }

  // Handle the onDblCellClick event
  public cellDblClick(data: any): void {
    if (data.columnId === 'completeWaterRightNumber') {
      void this.router.navigate([
        'wris',
        'water-rights',
        this.rows[data.row].waterRightId,
      ]);
    }
    if (data.columnId === 'completeVersion') {
      void this.router.navigate([
        'wris',
        'water-rights',
        this.rows[data.row].waterRightId,
        'versions',
        this.rows[data.row].versionNumber,
      ]);
    }
    if (data.columnId === 'podNumber') {
      void this.router.navigate(
        [
          'wris',
          'water-rights',
          this.rows[data.row].waterRightId,
          'versions',
          this.rows[data.row].versionNumber,
        ],
        {
          queryParams: { podNumber: this.rows[data.row].podNumber },
        }
      );
    }
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
