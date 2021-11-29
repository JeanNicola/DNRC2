import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { VersionCasesService } from './services/version-cases.service';

@Component({
  selector: 'app-cases',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './cases.component.scss',
  ],
  providers: [VersionCasesService],
})
export class CasesComponent extends BaseCodeTableComponent {
  constructor(
    public service: VersionCasesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public primarySortColumn = 'caseNumber';
  public title = '';
  public hideInsert = true;
  public hideDelete = true;
  public hideActions = true;
  public hideEdit = true;
  public searchable = false;
  public isInMain = false;
  public clickableRow = true;
  public dblClickableRow = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'caseNumber',
      title: 'Case Number',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterCourtCase',
      title: 'Water Court Case',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'description',
      title: 'Description',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'statusDescription',
      title: 'Status',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'hearingDate',
      title: 'Hearing Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'versionNumber',
      title: 'Version #',
      type: FormFieldTypeEnum.Input,
    },
  ];

  initFunction(): void {
    this.dataMessage = 'No data found';
    this.idArray = [
      this.route.snapshot.params.waterRightId,
      this.route.snapshot.params.versionId,
    ];
    this._get();
  }

  private redirectToCasesHearings(caseNumber: number) {
    this.router.navigate(['/wris', 'water-court', 'case-hearings', caseNumber]);
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToCasesHearings(data.caseNumber);
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
