import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ProcessorComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/processor/processor.component';
import { ProcessorService } from '../../services/processor.service';

@Component({
  selector: 'app-application-processor',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [ProcessorService],
})
export class ApplicationProcessorComponent extends ProcessorComponent {
  constructor(
    public service: ProcessorService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
}
