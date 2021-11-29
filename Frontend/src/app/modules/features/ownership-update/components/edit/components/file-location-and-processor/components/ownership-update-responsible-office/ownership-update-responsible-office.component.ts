import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ResponsibleOfficeComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/responsible-office/responsible-office.component';
import { OwnershipUpdateResponsibleOfficeService } from '../../services/ownership-update-responsible-office';

@Component({
  selector: 'app-ownership-updates-responsible-office',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [OwnershipUpdateResponsibleOfficeService],
})
export class OwnershipUpdateResponsibleOfficeComponent extends ResponsibleOfficeComponent {
  constructor(
    public service: OwnershipUpdateResponsibleOfficeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  initFunction(): void {
    this._get();
  }
}
