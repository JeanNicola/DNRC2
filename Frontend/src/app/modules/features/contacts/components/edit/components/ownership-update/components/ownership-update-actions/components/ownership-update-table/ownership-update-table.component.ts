import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { OwnershipUpdateForContactService } from '../../../../services/ownership-update.service';

@Component({
  selector: 'app-ownership-update-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './ownership-update-table.component.scss',
  ],
  providers: [OwnershipUpdateForContactService],
})
export class OwnershipUpdateTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: OwnershipUpdateForContactService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() ownerUpdateClick = new EventEmitter<number>();

  public title = 'Ownership Update Actions';
  public titleStyles = {
    fontSize: '16px',
  };

  public containerStyles = {
    width: '100%',
    height: 'fit-content',
    margin: '20px 0',
    background: 'rgb(250, 250, 250)',
  };

  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  public searchable = false;
  public hideActions = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;
  public selectedOwnerUpdateId;

  initFunction(): void {
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  protected _getHelperFunction(data: any): any {
    if (data.get?.results?.length) {
      this.selectedOwnerUpdateId = data.get.results[0].ownerUpdateId;
    } else {
      this.selectedOwnerUpdateId = null;
    }
    this.ownerUpdateClick.emit(this.selectedOwnerUpdateId);

    return data.get;
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'ownerUpdateId',
      title: 'Owner Updated #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'updateType',
      title: 'Update Type',
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
    {
      columnId: 'contractForDeed',
      title: 'Contract for Deed/RLE',
      type: FormFieldTypeEnum.Input,
      noSort: true,
    },
  ];

  public rowClick(data: any): void {
    this.ownerUpdateClick.emit(data.ownerUpdateId);
    this.selectedOwnerUpdateId = data.ownerUpdateId;
  }

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    this.router.navigate(['wris', 'ownership-updates', data.ownerUpdateId]);
  }
}
