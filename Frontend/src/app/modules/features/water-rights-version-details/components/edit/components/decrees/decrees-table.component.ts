import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DecreesService } from './services/decrees.service';

@Component({
  selector: 'app-decrees',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './decrees-table.component.scss',
  ],
  providers: [DecreesService],
})
export class DecreesComponent extends BaseCodeTableComponent {
  constructor(
    public service: DecreesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = '';
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

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'decreeId',
      title: 'Decree #',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
    },
    {
      columnId: 'description',
      title: 'Decree Type',
      type: FormFieldTypeEnum.Input,
      width: 300,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      width: 160,
    },
    {
      columnId: 'eventDate',
      title: 'Issue Date',
      type: FormFieldTypeEnum.Date,
      width: 240,
    },
    {
      columnId: 'missedInDecree',
      title: 'Missed In Decree',
      type: FormFieldTypeEnum.Checkbox,
      noSort: true,
      width: 160,
    },
  ];

  protected initFunction(): void {
    this.idArray = [
      this.route.snapshot.params.waterRightId,
      this.route.snapshot.params.versionId,
    ];
    this._get();
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate([
      '/wris',
      'water-court',
      'decrees',
      data.decreeId,
    ]);
  }

  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
