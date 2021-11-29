import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { RelatedRightsService } from '../../../../services/related-rights.service';

@Component({
  selector: 'app-related-right-row',
  templateUrl:
    '../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    './related-right-row.component.scss',
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [RelatedRightsService],
})
export class RelatedRightRowComponent extends DataRowComponent {
  constructor(
    public service: RelatedRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() relatedRightId;

  public showEdit = false;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'relationshipTypeVal',
      title: 'Relationship Type',
      type: FormFieldTypeEnum.Input,
      width: 180,
    },
    {
      columnId: 'relatedRightId',
      title: 'Related Right #',
      type: FormFieldTypeEnum.Input,
      width: 150,
    },
  ];

  public initFunction(): void {
    this.idArray = [this.relatedRightId];
    this._get();
  }
}
