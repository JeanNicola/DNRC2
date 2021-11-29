import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { WaterRightService } from '../../../../services/water-right.service';
import { UpdateConservationDialogComponent } from './components/update-conservation-dialog/update-conservation-dialog.component';

@Component({
  selector: 'app-conservation',
  templateUrl: './conservation.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [WaterRightService],
})
export class ConservationComponent extends DataRowComponent {
  @Input() set headerData(value: any) {
    this.data = value;
    this.displayData = this.data;
    if (['CDWR', 'WRWR'].includes(this.data.typeCode)) {
      this.disableEdit = false;
    } else {
      this.disableEdit = true;
    }
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
      columnId: 'waterReservationId',
      title: 'Water Reservation #',
      type: FormFieldTypeEnum.Input,
      dblClickable: true,
      validators: [WRISValidators.isNumber(10, 0)],
    },
    {
      columnId: 'conservationDistrictNumber',
      title: 'Conservation District #',
      type: FormFieldTypeEnum.Input,
      validators: [
        WRISValidators.requireOtherFieldIfNonNull('conservationDistrictDate'),
      ],
    },
    {
      columnId: 'conservationDistrictDate',
      title: 'CD Internal Priority Date',
      type: FormFieldTypeEnum.DateTime,
      validators: [
        WRISValidators.requireOtherFieldIfNonNull('conservationDistrictNumber'),
      ],
    },
  ];

  public _get() {
    this.reloadEvent.emit();
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

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateConservationDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Water Reservation and Conservation District',
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(this._buildEditDto(result));
      }
    });
  }

  protected _buildEditDto(result: any) {
    return {
      basin: this.data.basin,
      subBasin: this.data.subBasin,
      ext: this.data.ext,
      typeCode: this.data.typeCode,
      dividedOwnership: this.data.dividedOwnership,
      severed: this.data.severed,
      waterReservationId: result.waterReservationId,
      originalWaterRightId: this.data.originalWaterRightId,
      subcompactId: this.data.subcompactId,
      conservationDistrictNumber: result.conservationDistrictNumber,
      conservationDistrictDate: result.conservationDistrictDate,
    };
  }

  protected onIdDoubleClick() {
    void this.router.navigate([
      'wris',
      'water-reservations',
      this.data.waterReservationId,
    ]);
  }
}
