import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ProcessorComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/processor/processor.component';
import { OwnershipUpdateProcessorService } from '../../services/ownership-update-processor.service';

@Component({
  selector: 'app-ownership-updates-processor',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [OwnershipUpdateProcessorService],
})
export class OwnershipUpdateProcessorComponent extends ProcessorComponent {
  constructor(
    public service: OwnershipUpdateProcessorService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
}
