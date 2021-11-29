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
import { InsertWaterRightComponent } from './components/insert-water-right/insert-water-right.component';

@Component({
  selector: 'app-water-rights',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './water-rights.component.scss',
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [RelatedWaterRightsService],
})
export class WaterRightsComponent extends BaseCodeTableComponent {
  constructor(
    public service: RelatedWaterRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() relatedRightId;
  @Input() hideInsert = true;
  @Input() hideDelete = true;
  @Input() hideActions = false;

  public title = '';
  public clickableRow = false;
  public dblClickableRow = true;
  public hideEdit = true;
  public searchable = false;
  public isInMain = false;
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
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'status',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'version',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      dblClickable: true,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
  ];

  private redirectToWaterRightsPage(waterRightId) {
    void this.router.navigate(['wris', 'water-rights', waterRightId]);
  }

  protected initFunction(): void {
    this.idArray = [this.relatedRightId];
    this._get();
  }

  protected _displayInsertDialog(data: any): void {
    const dialogRef = this.dialog.open(InsertWaterRightComponent, {
      data: {
        title: 'Add New Water Right',
        values: {
          ...data,
          relatedRightId: this.relatedRightId,
        },
        columns: this.columns,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // Get array of all the current water right ids
        const waterRightIds = this.rows.map(
          (waterRight) => `${waterRight.waterRightId}-${waterRight.versionId}`
        );
        // Filter to avoid duplicate water rights
        const resultsToInsert = result
          .filter(
            (wr) =>
              waterRightIds.indexOf(`${wr.waterRightId}-${wr.versionId}`) === -1
          )
          .map((wr) => ({
            waterRightId: wr.waterRightId,
            versionId: wr.versionId,
          }));
        // Insert water rights
        if (resultsToInsert.length) {
          this._insert({ waterRights: resultsToInsert });
        }
      }
      this.firstInsert.focus();
    });
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canDELETE value
    this.permissions = {
      ...this.permissions,
      canDELETE: this.endpointService.canDELETE(this.service.deleteUrl),
    };
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [
      this.relatedRightId,
      this.rows[rowNumber].waterRightId,
      this.rows[rowNumber].versionId,
    ];
  }

  // Handle the onDblCellClick event
  public cellDblClick(data: any): void {
    if (data.columnId === 'version') {
      void this.router.navigate([
        'wris',
        'water-rights',
        this.rows[data.row].waterRightId,
        'versions',
        this.rows[data.row].versionId,
      ]);
    } else {
      this.redirectToWaterRightsPage(this.rows[data.row].waterRightId);
    }
  }
  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    this.redirectToWaterRightsPage(data.waterRightId);
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
