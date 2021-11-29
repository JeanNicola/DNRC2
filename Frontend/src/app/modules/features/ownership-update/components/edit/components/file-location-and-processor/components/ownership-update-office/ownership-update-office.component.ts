import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { OfficeComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/office/office.component';
import { OwnershipUpdateOfficeService } from '../../services/ownership-update-office.service';

@Component({
  selector: 'app-ownership-updates-office',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '../../../../../../../../shared/components/templates/file-location-processor/components/office/office.component.scss',
  ],
  providers: [OwnershipUpdateOfficeService],
})
export class OwnershipUpdateOfficeComponent extends OfficeComponent {
  constructor(
    public service: OwnershipUpdateOfficeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
}
