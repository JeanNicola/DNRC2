import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { RelatedApplicationsService } from './services/related-applications.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-related-applications',
  templateUrl: './related-applications.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [RelatedApplicationsService],
})
export class RelatedApplicationsComponent extends BaseCodeTableComponent {
  protected url = '/applications/{applicationId}/related-applications';
  hideActions = true;
  zHeight = 1;
  protected clickableRow = true;
  protected dblClickableRow = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'typeCode',
      title: 'Type Code',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'typeDescription',
      title: 'Appl. Type and Description',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dateReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  constructor(
    public service: RelatedApplicationsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  initFunction(): void {
    this._get();
  }

  protected _getHelperFunction(data: any): any {
    data.get.results = data.get.results.map((row) => {
      if (row.typeCode !== undefined && row.typeDescription !== undefined) {
        row.typeDescription = `${row.typeCode} - ${row.typeDescription}`;
      }
      return row;
    });

    return { ...data.get };
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'applications', data.applicationId]);
  }
}
