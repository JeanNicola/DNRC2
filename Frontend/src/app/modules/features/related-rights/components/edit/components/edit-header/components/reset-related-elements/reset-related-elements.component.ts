import { Component, HostListener, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';

@Component({
  selector: 'app-reset-related-elements',
  templateUrl: './reset-related-elements.component.html',
  styleUrls: ['./reset-related-elements.component.scss'],
})
export class ResetRelatedElementsComponent {
  constructor(
    private dialogRef: MatDialogRef<ResetRelatedElementsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {}

  @HostListener('window:keyup.esc') onKeyUp(): void {
    this.dialogRef.close();
  }
}
