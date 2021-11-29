import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { VersionService } from '../../../../services/version.service';

@Component({
  selector: 'app-version-table',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './version-table.component.scss',
  ],
  providers: [VersionService],
})
export class VersionTableComponent extends BaseCodeTableComponent {
  @Input() set waterRightId(value: string) {
    this.idArray = [value];
  }
  constructor(
    public service: VersionService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'version',
      title: 'Version #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'versionTypeDescription',
      title: 'Version Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'versionStatusDescription',
      title: 'Version Status',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'operatingAuthority',
      title: 'Version Operating Authority Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  public primarySortColumn = 'version';
  public sortDirection = 'asc';

  public title = '';
  public searchable = false;
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  protected hideActions = true;
  public isInMain = false;

  public initFunction(): void {
    this._get();
  }

  public ngOnDestroy(): void {}
}
