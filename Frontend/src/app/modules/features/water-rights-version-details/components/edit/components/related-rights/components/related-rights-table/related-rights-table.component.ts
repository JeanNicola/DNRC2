import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { RelationshipTypesService } from 'src/app/modules/features/related-rights/components/search/services/relationship-types.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { RelatedRightsForVersionService } from '../../services/related-rights-for-version.service';

@Component({
  selector: 'app-related-rights-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './related-rights-table.component.scss',
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [RelatedRightsForVersionService, RelationshipTypesService],
})
export class RelatedRightsTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: RelatedRightsForVersionService,
    public relationshipTypesService: RelationshipTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() selectRelatedRight = new EventEmitter<any>();

  public title = 'Relationships';
  public hideActions = true;
  public hideEdit = true;
  public hideInsert = true;
  public hideDelete = true;
  public searchable = false;
  public clickableRow = true;
  public dblClickableRow = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public isInMain = false;
  public waterRightId;
  public versionId;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'relatedRightId',
      title: 'Related ID',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
      displayInEdit: false,
      width: 160,
    },
    {
      columnId: 'relationshipTypeVal',
      title: 'Relationship Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      width: 300,
    },
    {
      columnId: 'maxFlowRate',
      title: 'Flow Rate',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      width: 160,
    },
    {
      columnId: 'flowRateUnitVal',
      title: 'Unit',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      width: 160,
    },
    {
      columnId: 'maxVolume',
      title: 'Volume',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      width: 160,
    },
    {
      columnId: 'maxAcres',
      title: 'Acres',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      width: 160,
    },
  ];

  protected initFunction(): void {
    this.waterRightId = this.route.snapshot.params.waterRightId;
    this.versionId = this.route.snapshot.params.versionId;
    this.idArray = [this.waterRightId, this.versionId];
    this._get();
  }

  private redirectToRelatedRightEdit(relatedRightId: number) {
    this.router.navigate(['/wris', 'related-rights', relatedRightId]);
  }

  public rowClick(data: any): void {
    this.selectRelatedRight.emit(data.relatedRightId);
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToRelatedRightEdit(data.relatedRightId);
  }

  protected _getHelperFunction(data: any) {
    if (data.get?.results?.length) {
      this.selectRelatedRight.emit(data.get.results[0].relatedRightId);
    } else {
      this.selectRelatedRight.emit(null);
    }

    return data.get;
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
