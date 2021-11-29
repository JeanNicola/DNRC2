import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ApplicationsObjectionsCriteriaService } from '../../services/applications-objections-criteria.service';

@Component({
  selector: 'app-objections-categories',
  templateUrl: '../../templates/objections-subtable.html',
  styleUrls: [
    '../../../templates/code-table/code-table.template.scss',
    '../../templates/objections-subtable.scss',
  ],
  providers: [ApplicationsObjectionsCriteriaService],
})
export class ObjectionsCategoriesComponent extends BaseCodeTableComponent {
  // This block sets the value of idArray when idArray is updated.
  // Clear existing data when a null objectionId is received
  @Input() set idArray(value: string[]) {
    this._idArray = value;
    if (value.includes(null)) {
      this.data = null;
      this.rows = null;
      this.dataMessage = 'No data found';
    } else {
      this._get();
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public hideActions = true;
  public zHeight = 1;
  public title = 'Objection Criteria';
  public primarySortColumn = 'determinationDate';
  protected clickableRow = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'categoryTypeDescription',
      title: 'Category',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'determinationDate',
      title: 'Determination Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  constructor(
    public service: ApplicationsObjectionsCriteriaService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}
}
