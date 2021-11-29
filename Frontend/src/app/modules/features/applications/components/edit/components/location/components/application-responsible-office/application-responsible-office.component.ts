import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ResponsibleOfficeComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/responsible-office/responsible-office.component';
import { ResponsibleOfficeService } from '../../services/responsible-office.service';

@Component({
  selector: 'app-application-responsible-office',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [ResponsibleOfficeService],
})
export class ApplicationResponsibleOfficeComponent extends ResponsibleOfficeComponent {
  constructor(
    public service: ResponsibleOfficeService,
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
