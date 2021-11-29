/* eslint-disable max-len */
import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ResponsibleOfficeComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/responsible-office/responsible-office.component';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WaterRightResponsibleOfficeService } from '../../services/water-right-responsible-office';

@Component({
  selector: 'app-water-right-responsible-office',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [WaterRightResponsibleOfficeService],
})
export class WaterRightResponsibleOfficeComponent extends ResponsibleOfficeComponent {
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;

  constructor(
    public service: WaterRightResponsibleOfficeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  initFunction(): void {
    this._get();
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }
}
