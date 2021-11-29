import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { UsgsQuadMapService } from '../../services/usgs-quad-map.service';
import { InsertUsgsQuadMapComponent } from './components/insert-usgs-quad-map/insert-usgs-quad-map.component';

@Component({
  selector: 'app-usgs-quad-map-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [UsgsQuadMapService],
})
export class UsgsQuadMapTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: UsgsQuadMapService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined) && this.service) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public hideEdit = true;
  public clickableRow = false;
  public isInMain = false;
  public title = 'USGS Quad Maps';
  public primarySortColumn = 'name';
  public searchable = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'name',
      title: 'USGS Quad Map Name',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected getInsertDialogTitle() {
    return 'Add New USGS Quad Map Record';
  }

  protected getEditDialogTitle() {
    return `Update USGS Quad Map Record`;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.utmpId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].utmpId];
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUsgsQuadMapComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getInsertDialogTitle(),
        columns: this.columns,
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

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    this.hideActions = data.get.results?.length === 1;

    return data.get;
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
