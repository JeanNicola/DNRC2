import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { FlowRateDescriptionService } from '../../services/flow-rate-description.service';

@Component({
  selector: 'app-flow-rate-description-dialog',
  templateUrl: './flow-rate-description-dialog.component.html',
  styleUrls: ['./flow-rate-description-dialog.component.scss'],
  providers: [FlowRateDescriptionService]
})
export class FlowRateDescriptionDialogComponent extends DataManagementDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<FlowRateDescriptionDialogComponent>,
    public service: FlowRateDescriptionService
    ) {
      super(dialogRef);
    }

  public title = 'Select a Flow Rate Description';
  public row = null;
  public rows = null;
  public dataFound = false;

  public displayFields: ColumnDefinitionInterface[] = [
    {
      columnId: 'value',
      title: 'Flow Rate Description Code',
      type: FormFieldTypeEnum.Input,
      width: 150,
      noSort: true,
    },
    {
      columnId: 'description',
      title: 'Flow Rate Description',
      type: FormFieldTypeEnum.Input,
      noSort: true,
    },
  ];

  public initFunction(): void {
    this.service.get({}).subscribe((data) => {
      this.rows = data.results;
      this.row = null;
      this.dataFound = this.rows.length > 0;
    });
  }

  public onRowClick(idx: number): void {
    this.row = this.rows[idx];
  }

  public onRowDoubleClick(idx: number): void {
    this.dialogRef.close(this.rows[idx]);
  }
}
