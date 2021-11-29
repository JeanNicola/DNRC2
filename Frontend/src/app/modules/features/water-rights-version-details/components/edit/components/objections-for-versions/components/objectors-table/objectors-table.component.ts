import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ObjectorsVersionService } from './services/objectors-version.service';

@Component({
  selector: 'app-objectors-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './objectors-table.component.scss',
  ],
  providers: [ObjectorsVersionService],
})
export class ObjectorsTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: ObjectorsVersionService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined)) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public primarySortColumn = 'name';
  public title = 'Objectors';
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
      columnId: 'contactId',
      title: 'Contact ID#',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'name',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
    },
  ];

  private redirectToContactEditScreen(contactId: number) {
    this.router.navigate(['/wris', 'contacts', contactId]);
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToContactEditScreen(data.contactId);
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
