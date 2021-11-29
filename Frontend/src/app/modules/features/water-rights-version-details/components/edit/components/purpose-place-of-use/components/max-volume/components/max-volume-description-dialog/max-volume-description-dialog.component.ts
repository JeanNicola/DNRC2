import { Component } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { MaxVolumeDescriptionService } from './components/max-volume-description.service';

@Component({
  selector: 'app-max-volume-description-dialog',
  templateUrl: './max-volume-description-dialog.component.html',
  styleUrls: [],
  providers: [MaxVolumeDescriptionService],
})
export class MaxVolumeDescriptionDialogComponent extends DataManagementDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<MaxVolumeDescriptionDialogComponent>,
    public service: MaxVolumeDescriptionService
  ) {
    super(dialogRef);
  }

  public title = 'Select a Max Volume Description';
  public tooltip = 'Insert Description';
  public row = null;
  public rows = null;
  public dataFound = false;

  public displayFields: ColumnDefinitionInterface[] = [
    {
      columnId: 'value',
      title: 'Max Volume Code',
      type: FormFieldTypeEnum.Input,
      width: 150,
      noSort: true,
    },
    {
      columnId: 'description',
      title: 'Max Volume Description Text',
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

  public save() {
    this.dialogRef.close(this.row);
  }
}
