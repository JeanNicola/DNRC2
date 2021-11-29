import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WaterRightService } from '../../../../services/water-right.service';
import { ChangeCompactDialogComponent } from './components/change-compact-dialog/change-compact-dialog.component';

@Component({
  selector: 'app-compacts',
  templateUrl: './compacts.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [WaterRightService],
})
export class CompactsComponent extends DataRowComponent {
  @Input() canEdit = false;
  @Input() set headerData(value: any) {
    this.data = value;
    this.displayData = this.data;
    this.disableEdit = !this.canEdit;
    this.disableDelete = !this.canEdit || !this.displayData?.subcompactId;
  }
  @Output() reloadEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(
    public service: WaterRightService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'subcompactId',
      title: 'Subcompact Id',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      displayInTable: false,
    },
    {
      columnId: 'compact',
      title: 'Compact',
      type: FormFieldTypeEnum.Input,
      dblClickable: true,
      width: 400,
    },
    {
      columnId: 'subcompact',
      title: 'Subcompact',
      type: FormFieldTypeEnum.Input,
      width: 500,
    },
  ];

  public disableDelete = false;
  public dialogWidth = '1000px';
  public loadEventSubscription: Subscription;
  public hideEdit = true;
  public hideDelete = true;

  public initFunction() {}

  public _get() {
    this.reloadEvent.emit();
  }

  public _buildUpdateDto(subcompactId: number) {
    return {
      basin: this.data.basin,
      subBasin: this.data.subBasin,
      ext: this.data.ext,
      typeCode: this.data.typeCode,
      dividedOwnership: this.data.dividedOwnership,
      severed: this.data.severed,
      conservationDistrictNumber: this.data.conservationDistrictNumber,
      conservationDistrictDate: this.data.conservationDistrictDate,
      waterReservationId: this.data.waterReservationId,
      originalWaterRightId: this.data.originalWaterRightId,
      subcompactId,
    };
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkDecree(
      this.data.isDecreed,
      this.data.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }

  public _displayEditDialog(): void {
    const dialogRef = this.dialog.open(ChangeCompactDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Search Compacts',
        columns: this.columns,
        values: [],
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(this._buildUpdateDto(result.subcompactId));
      }
    });
  }

  // Handle the onEdit event
  public onDelete(): void {
    WaterRightsPrivileges.checkDecree(
      this.data.isDecreed,
      this.data.isEditableIfDecreed,
      this.dialog,
      this.displayDeleteDialog.bind(this)
    );
  }

  public displayDeleteDialog() {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._update(this._buildUpdateDto(null));
      }
    });
  }

  public compactClick() {
    void this.router.navigate(['wris', 'compacts', this.data.compactId]);
  }
}
