import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { BaseCodeTableComponent } from '../../../../../shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { CaseStatusService } from '../services/case-status-types.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { Validators } from '@angular/forms';

@Component({
  selector: 'code-table-case-status',
  templateUrl:
    '../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CaseStatusService],
})
export class CaseStatusComponent extends BaseCodeTableComponent {
  protected url = '/case-status';
  public title = 'Case Status';
  protected searchable = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'code',
      title: 'Code',
      type: FormFieldTypeEnum.Input,
      editable: false,
      validators: [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(4),
      ],
    },
    {
      columnId: 'description',
      title: 'Status',
      type: FormFieldTypeEnum.TextArea,
      validators: [
        Validators.required,
        Validators.maxLength(40),
        WRISValidators.preventNewLineCharacter,
      ],
    },
  ];

  initFunction(): void {
    this._get();
  }

  constructor(
    public service: CaseStatusService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
}
