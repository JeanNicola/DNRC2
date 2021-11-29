import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { OfficeComponent } from 'src/app/modules/shared/components/templates/file-location-processor/components/office/office.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WaterRightOfficeService } from '../../services/water-right-office.service';

@Component({
  selector: 'app-water-right-office',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '../../../../../../../../shared/components/templates/file-location-processor/components/office/office.component.scss',
  ],
  providers: [WaterRightOfficeService],
})
export class WaterRightOfficeComponent extends OfficeComponent {
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;

  constructor(
    public service: WaterRightOfficeService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'id',
      title: 'Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'officeId',
      title: 'Office',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      editable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'officeDescription',
      title: 'Office',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'receivedDate',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
      validators: [],
    },
    {
      columnId: 'sentDate',
      title: 'Sent Date',
      type: FormFieldTypeEnum.Date,
      displayInInsert: false,
      customErrorMessages: {
        required: 'Required: only one Office can have no Sent Date',
      },
      validators: [],
    },
    {
      columnId: 'notes',
      title: 'Notes',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
  ];

  // Handle the onEdit event
  public onEdit(updatedData: any): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  // Handle the OnInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  // Handle the OnDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }
}
