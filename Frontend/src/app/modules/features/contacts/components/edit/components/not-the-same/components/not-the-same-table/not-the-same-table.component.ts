import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { NotTheSameService } from '../../services/not-the-same.service';

@Component({
  selector: 'app-not-the-same-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './not-the-same-table.component.scss',
  ],
  providers: [NotTheSameService],
})
export class NotTheSameTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: NotTheSameService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() containerStyles;

  public title = '';
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  public searchable = false;
  public hideActions = true;
  public primarySortColumn = 'name';
  public sortDirection = 'asc';

  initFunction(): void {
    this.dataMessage = 'No data found';
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'name',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
    },
  ];
}
