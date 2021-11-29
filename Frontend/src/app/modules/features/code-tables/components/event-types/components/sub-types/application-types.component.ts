import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ApplicationTypesService } from '../../services/application-types.service';
import { EventTypesApplicationTypesService } from '../../services/sub-types/event-types-application-types.service';
import { EventSubtypeComponent } from '../../templates/event-subtype.component';

@Component({
  selector: 'event-types-application-types',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '../../templates/event-subtype.component.scss',
  ],
  providers: [EventTypesApplicationTypesService, ApplicationTypesService],
})
export class EventTypesApplicationTypesComponent extends EventSubtypeComponent {
  url = '/event-types/{eventCode}/application-types';
  title = '';

  constructor(
    public service: EventTypesApplicationTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public applicationTypesService: ApplicationTypesService
  ) {
    super(service, endpointService, dialog, snackBar, applicationTypesService);
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}
}
