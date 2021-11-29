import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { BaseCodeTableComponent } from '../../../../../shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { EventTypesService } from '../services/event-types.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { Validators } from '@angular/forms';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

@Component({
  selector: 'code-table-event-types',
  templateUrl:
    '../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [EventTypesService],
})
export class EventTypesComponent extends BaseCodeTableComponent {
  protected url = '/event-types';
  public title = 'Event Types';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'code',
      title: 'Code',
      type: FormFieldTypeEnum.Input,
      editable: false,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(4),
      ],
    },
    {
      columnId: 'description',
      title: 'Event Type Description',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.required, Validators.maxLength(70)],
    },
    {
      columnId: 'responseDueDays',
      title: 'Response Due Days',
      type: FormFieldTypeEnum.Input,
      validators: [
        Validators.min(0),
        Validators.max(999),
        WRISValidators.integer,
      ],
    },
  ];

  initFunction(): void {
    this.dataMessage = null;
    this.onSearch();
  }

  // Override the table row focus to call the button focus
  // This is here because for some reason the Events page doesn't seem to process the @ViewChildren the
  // same way the other code tables
  protected setTableFocus(): void {
    this.setInitialFocus();
  }

  constructor(
    public service: EventTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
}
