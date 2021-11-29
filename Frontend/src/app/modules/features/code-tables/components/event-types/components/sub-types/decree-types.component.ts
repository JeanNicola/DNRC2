import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DecreeTypesService } from '../../services/decree-types.service';
import { EventTypesDecreeTypesService } from '../../services/sub-types/event-types-decree-types.service';
import { EventSubtypeComponent } from '../../templates/event-subtype.component';

@Component({
  selector: 'event-types-decree-types',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '../../templates/event-subtype.component.scss',
  ],
  providers: [EventTypesDecreeTypesService, DecreeTypesService],
})
export class EventTypesDecreeTypesComponent extends EventSubtypeComponent {
  url = '/event-types/{eventCode}/decree-types';
  title = '';

  constructor(
    public service: EventTypesDecreeTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public decreeTypesService: DecreeTypesService
  ) {
    super(service, endpointService, dialog, snackBar, decreeTypesService);
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}
}
