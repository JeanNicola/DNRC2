import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ContactWaterRightsService } from '../../services/contact-water-rights.service';

@Component({
  selector: 'app-water-rights',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './water-rights.component.scss',
  ],
  providers: [ContactWaterRightsService],
})
export class WaterRightsComponent extends BaseCodeTableComponent {
  constructor(
    public service: ContactWaterRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }
  @Input() containerStyles = {};

  public title = '';
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  public searchable = false;
  public hideActions = true;
  protected clickableRow = false;
  protected dblClickableRow = true;

  public primarySortColumn = 'waterRightNumber';
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
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ext',
      title: 'Ext',
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
      columnId: 'conDistNo',
      title: 'Conservation District #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'objection',
      title: 'Objection?',
      type: FormFieldTypeEnum.Input,
      noSort: true,
    },
  ];

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    this.router.navigate(['wris', 'water-rights', data.waterRightId]);
  }
}
