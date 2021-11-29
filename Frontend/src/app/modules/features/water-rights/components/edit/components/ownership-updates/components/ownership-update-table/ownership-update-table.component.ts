import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { OwnershipUpdatesService } from 'src/app/modules/features/water-rights/services/ownership-updates.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'app-ownership-update-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './ownership-update-table.component.scss',
  ],
  providers: [OwnershipUpdatesService],
})
export class OwnershipUpdateTableComponent extends BaseCodeTableComponent {
  @Output() ownerUpdateClick = new EventEmitter<number>();
  constructor(
    public service: OwnershipUpdatesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'ownerUpdateId',
      title: 'Owner Update #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'updateType',
      title: 'Ownership Update Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dateReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'dateProcessed',
      title: 'Processed Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'dateTerminated',
      title: 'Terminated Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  public title = '';

  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  public searchable = false;
  public hideActions = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;
  public selectedOwnerUpdateId;
  public isInMain = false;

  initFunction(): void {
    this._get();
  }

  protected _getHelperFunction(data: any): any {
    if (data.get?.results?.length) {
      this.selectedOwnerUpdateId = data.get.results[0].ownerUpdateId;
    } else {
      this.selectedOwnerUpdateId = null;
    }
    this.ownerUpdateClick.emit(this.selectedOwnerUpdateId);
    return data.get;
  }

  public rowClick(data: any): void {
    this.ownerUpdateClick.emit(data.ownerUpdateId);
    this.selectedOwnerUpdateId = data.ownerUpdateId;
  }

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    void this.router.navigate([
      'wris',
      'ownership-updates',
      data.ownerUpdateId,
    ]);
  }

  protected setTableFocus(): void {}
}
