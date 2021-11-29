import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CaseTypesService } from '../../../case-types/services/case-types.service';
import { EventTypesCaseTypesService } from '../../services/sub-types/event-types-case-types.service';
import { EventSubtypeComponent } from '../../templates/event-subtype.component';

@Component({
  selector: 'event-types-case-types',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '../../templates/event-subtype.component.scss',
  ],
  providers: [EventTypesCaseTypesService, CaseTypesService],
})
export class EventTypesCaseTypesComponent extends EventSubtypeComponent {
  url = '/event-types/{eventCode}/case-types';
  title = '';

  constructor(
    public service: EventTypesCaseTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public caseTypesService: CaseTypesService
  ) {
    super(service, endpointService, dialog, snackBar, caseTypesService);
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}
}
