import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { StaffComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/staff/staff.component';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WaterRightStaffService } from '../../services/water-right-staff.service';

@Component({
  selector: 'app-water-right-staff',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '../../../../../../../../shared/components/templates/file-location-processor/components/staff/staff.component.scss',
  ],
  providers: [WaterRightStaffService],
})
export class WaterRightStaffComponent extends StaffComponent {
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;

  constructor(
    public service: WaterRightStaffService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Handle the onEdit event
  public onEdit(updatedData: any): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  // Handle the OnInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  // Handle the OnDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }
}
