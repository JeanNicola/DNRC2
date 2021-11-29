import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { AssociateBuyersService } from 'src/app/modules/shared/services/associate-buyers.service';
import { OwnershipUpdateBuyersService } from 'src/app/modules/shared/services/ownership-update-buyers.service';

@Component({
  selector: 'app-ownership-update-buyers',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './ownership-update-buyers.component.scss',
  ],
  providers: [AssociateBuyersService],
})
export class OwnershipUpdateBuyersComponent extends BaseCodeTableComponent {
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
    return this._idArray;
  }
  constructor(
    public service: AssociateBuyersService,
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
      columnId: 'name',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'startDate',
      title: 'Start Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  public title = 'Buyers';
  public primarySortColumn = 'name';
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  public searchable = false;
  public hideActions = true;
  public dblClickableRow = true;

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'contacts', data.contactId]);
  }

  protected setTableFocus(): void {}
}
