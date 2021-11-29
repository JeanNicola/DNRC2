import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { EditVersionCompactComponent } from './components/edit-version-compact/edit-version-compact.component';
import { InsertVersionCompactComponent } from './components/insert-version-compact/insert-version-compact.component';
import { CompactsService } from './services/compacts.service';

@Component({
  selector: 'app-compacts',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './compacts.component.scss',
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CompactsService],
})
export class CompactsComponent extends BaseCodeTableComponent {
  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canCompact = false;
  constructor(
    public service: CompactsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'compact',
      title: 'Compact Name',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
      displayInSearch: false,
    },
    {
      columnId: 'subcompact',
      title: 'Sub-Compact Name',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'exemptCompact',
      title: 'Exempt From Compact',
      type: FormFieldTypeEnum.Checkbox,
      displayInSearch: false,
    },
    {
      columnId: 'allocation',
      title: 'Version Affects Allocation',
      type: FormFieldTypeEnum.Checkbox,
      displayInSearch: false,
    },
    {
      columnId: 'blm',
      title: 'BLM Transbasin',
      type: FormFieldTypeEnum.Checkbox,
      displayInSearch: false,
    },
  ];
  public title = '';
  public searchable = false;
  public primarySortColumn = 'subcompact';

  public initFunction(): void {
    this._get();
  }

  protected setInitialFocus(): void {}

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url) && this.canEdit,
    };
  }

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  // Handle the onEdit event
  public onEdit(updatedData: any): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertVersionCompactComponent, {
      width: this.dialogWidth,
      data: {
        // Only use the Compact and Subcompact in the seach table
        columns: [this._getColumn('subcompact'), this._getColumn('compact')],
        formColumns: this.columns.filter(
          (c: ColumnDefinitionInterface) => c.displayInInsert ?? true
        ),
        values: data,
        validators: this.validators,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });
  }

  protected _buildEditIdArray(dto: any, originalData: any): string[] {
    return [...this.idArray, originalData.id];
  }

  protected _buildEditDto(originalData: any, editedData: any): any {
    return { ...editedData, subcompactId: originalData.subcompactId };
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(EditVersionCompactComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Compact Record',
        columns: this.columns,
        values: data,
        validators: this.validators,
        canCompact: this.canCompact,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }

  protected _buildDeleteIdArray(rowNumber: number) {
    return [...this.idArray, this.rows[rowNumber].id];
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'compacts', data.compactId]);
  }
}
