import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { OwnershipUpdateTypes } from '../../../../interfaces/ownership-update';
import { OwnershipUpdateService } from '../../../../services/ownership-update.service';
import { getOwnershipUpdateColumns } from '../../../../shared/ownership-update-columns';
import { WaterRightsDialogComponent } from '../water-rights-dialog/water-rights-dialog.component';
import { OwnershipUpdatesByCustomerService } from './services/ownership-updates-by-customer.service';

@Component({
  selector: 'app-ownership-update-table',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './ownership-update-table.component.scss',
  ],
  providers: [OwnershipUpdateService, OwnershipUpdatesByCustomerService],
})
export class OwnershipUpdateTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: OwnershipUpdatesByCustomerService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() dblClickEvent: EventEmitter<number> = new EventEmitter<number>();

  @Input() contactId;
  @Input() customerType;

  public columns: ColumnDefinitionInterface[] = getOwnershipUpdateColumns();
  public primarySortColumn = 'ownershipUpdateId';
  public sortDirection = 'asc';

  public title = '';
  public searchable = false;
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  protected hideActions = true;
  protected clickableRow = false;
  protected dblClickableRow = true;
  public isInMain = false;

  public initFunction(): void {
    if (this.customerType === OwnershipUpdateTypes.SELLER) {
      this.queryParameters.filters = { ownershipUpdateRole: 'SEL' };
    } else if (this.customerType === OwnershipUpdateTypes.BUYER) {
      this.queryParameters.filters = { ownershipUpdateRole: 'BUY' };
    }

    this.idArray = [this.contactId];
    this._get();
  }

  public ngOnDestroy(): void {}

  protected _getHelperFunction(data: any): any {
    return {
      ...data.get,
      results: data.get.results.map((ou) => ({
        ...ou,
        receivedDate: ou.receivedDate || ou.saleDate,
        ownershipUpdateTypeValue: this.getRealValueFore(
          'ownershipUpdateType',
          ou.ownershipUpdateType
        ),
      })),
    };
  }

  private getRealValueFore(fieldName, value: string) {
    const options = this._getColumn(fieldName).selectArr.filter(
      (option) => option.value === value
    );
    if (options[0]) {
      return options[0].name;
    }
  }

  public onRowDoubleClick(data: any): void {
    this.dblClickEvent.emit(data.ownershipUpdateId);
  }

  public cellClick(data: any): void {
    if (data?.columnId === 'waterRights') {
      const waterRightsDialog = this.dialog.open(WaterRightsDialogComponent, {
        data: {
          title: 'Water Rights',
          ownershipUpdateId: this.rows[data.row].ownershipUpdateId,
        },
      });

      waterRightsDialog.afterClosed().subscribe(() => {
        this.focusRowByIndex(data.row);
      });
    }
  }
}
