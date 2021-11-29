import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { InsertCaseWaterRightComponent } from '../insert-case-water-right/insert-case-water-right.component';
import { CaseDeleteWaterRightsService } from './services/case-delete-water-rights.service';
import { CaseWaterRightsService } from './services/case-water-rights.service';

@Component({
  selector: 'app-water-rights-code-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CaseWaterRightsService, CaseDeleteWaterRightsService],
})
export class WaterRightsCodeTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: CaseWaterRightsService,
    public caseDeleteWaterRightsService: CaseDeleteWaterRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() onWaterRightSelect = new EventEmitter<any>();
  private _decreeId = null;

  @Input() set decreeId(value: number) {
    this._decreeId = value;
    this.hideInsert = !value;
  }
  get decreeId(): number {
    return this._decreeId;
  }
  @Input() decreeBasin = null;
  @Input() hasCaseAdminRole: boolean = false;

  public hideEdit = true;
  public dblClickableRow = true;
  public clickableRow = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dialogWidth = '500px';
  public isInMain = false;
  public searchable = false;
  public title = 'Water Rights';
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
      title: 'Water Right',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'waterRightTypeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      displayInInsert: false,
    },
    {
      columnId: 'waterRightStatusDescription',
      title: 'Water Right Status',
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
      columnId: 'waterNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
  ];

  protected initFunction() {
    this.hideActions = !this.hasCaseAdminRole;
    this.idArray = [this.route.snapshot.params.caseId];
    this._get();
  }

  protected _getHelperFunction(data: any): any {
    if (data.get.results?.length) {
      this.onWaterRightSelect.emit(data.get.results[0]);
    } else {
      this.onWaterRightSelect.emit(null);
    }

    return data.get;
  }

  // Handle the onRowClick event
  public rowClick(data: any): void {
    this.onWaterRightSelect.emit(data);
  }

  private redirectToWaterRightVersion(waterRightId, versionId) {
    this.router.navigate([
      'wris',
      'water-rights',
      waterRightId,
      'versions',
      versionId,
    ]);
  }

  private redirectToWaterRight(waterRightId) {
    this.router.navigate(['wris', 'water-rights', waterRightId]);
  }

  // Handle the onDblCellClick event
  public cellDblClick(data: any): void {
    if (data.columnId === 'completeVersion') {
      this.redirectToWaterRightVersion(
        this.rows[data.row].waterRightId,
        this.rows[data.row].versionId
      );
    }

    if (data.columnId === 'completeWaterRightNumber') {
      this.redirectToWaterRight(this.rows[data.row].waterRightId);
    }
  }

  protected getInsertDialogTitle() {
    return 'Add New Water Right Record';
  }

  protected getEditDialogTitle() {
    return 'Update Water Right Record';
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [
      ...this.idArray,
      this.rows[rowNumber].waterRightId,
      this.rows[rowNumber].versionId,
    ];
  }

  protected _displayInsertDialog(data: any): void {
    const dialogRef = this.dialog.open(InsertCaseWaterRightComponent, {
      data: {
        title: 'Add New Water Right',
        values: {
          ...data,
          idArray: [this.decreeId, this.decreeBasin],
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
          this._insert({ waterRightVersions: resultsToInsert });
        }
      }
      this.firstInsert.focus();
    });
  }

  protected setPermissions(): void {
    this.permissions = {
      canGET: this.endpointService.canGET(this.service.url),
      canPOST: this.endpointService.canPOST(this.service.url),
      canDELETE: this.endpointService.canDELETE(
        this.caseDeleteWaterRightsService.url
      ),
      canPUT: this.endpointService.canPUT(this.service.url),
    };
  }

  protected _getDeleteService(): BaseDataService {
    return this.caseDeleteWaterRightsService;
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
