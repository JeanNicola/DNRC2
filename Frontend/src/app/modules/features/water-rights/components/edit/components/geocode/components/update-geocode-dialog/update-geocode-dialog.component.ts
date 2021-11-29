import { AfterViewInit, Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';

@Component({
  selector: 'app-update-geocode-dialog',
  templateUrl: './update-geocode-dialog.component.html',
  styleUrls: ['./update-geocode-dialog.component.scss'],
})
export class UpdateGeocodeDialogComponent extends UpdateDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {
    super(dialogRef, data);
  }
}
