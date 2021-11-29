import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { OfficeComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/office/office.component';
import { ApplicationOfficeService } from '../../services/application-office.service';

@Component({
  selector: 'app-application-office',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '../../../../../../../../shared/components/templates/file-location-processor/components/office/office.component.scss',
  ],
  providers: [ApplicationOfficeService],
})
export class ApplicationOfficeComponent extends OfficeComponent {
  constructor(
    public service: ApplicationOfficeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
}
