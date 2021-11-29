import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ObjectorsRepresentativeService } from '../../services/objectors-representative.service';

@Component({
  selector: 'app-objectors-representative-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
    './objectors-representative-table.component.scss',
  ],
  providers: [ObjectorsRepresentativeService],
})
export class ObjectorsRepresentativeTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: ObjectorsRepresentativeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'lastName',
      title: 'Last Name/Corporation',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'firstName',
      title: 'Contact name',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'middleInitial',
      title: 'MI',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'suffix',
      title: 'Suffix',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
      displayInTable: false,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      // Validators are set in DisplayEditDialog function
    },
    {
      columnId: 'roleTypeCode',
      title: 'Role Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'roleTypeDescription',
      title: 'Role Type',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public title = '';
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  public hideActions = true;
  public searchable = false;
  public isInMain = false;
  protected clickableRow = true;
  protected dblClickableRow = false;

  protected initFunction(): void {
    this._get();
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'contacts', data.contactId]);
  }

  protected _getHelperFunction(data: any): any {
    data.get.results.forEach((row) => {
      if (row?.firstName) {
        row.lastName += `, ${row.firstName}`;
        if (row?.middleInitial) {
          row.lastName += ` ${row.middleInitial}`;
        }
      }
      if (row?.suffix) {
        row.lastName += `, ${row.suffix}`;
      }
    });
    return data.get;
  }
}
