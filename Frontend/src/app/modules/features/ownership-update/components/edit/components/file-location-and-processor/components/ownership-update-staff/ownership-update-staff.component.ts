import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { StaffComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/staff/staff.component';
import { OwnershipUpdateStaffService } from '../../services/ownership-update-staff.service';

@Component({
  selector: 'app-ownership-updates-staff',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '../../../../../../../../shared/components/templates/file-location-processor/components/staff/staff.component.scss',
  ],
  providers: [OwnershipUpdateStaffService],
})
export class OwnershipUpdateStaffComponent extends StaffComponent {
  constructor(
    public service: OwnershipUpdateStaffService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
}
