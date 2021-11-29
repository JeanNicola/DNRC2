import { Component, HostListener, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';

@Component({
  selector: 'app-reset-sellers-dialog',
  templateUrl: './reset-sellers-dialog.component.html',
  styleUrls: ['./reset-sellers-dialog.component.scss'],
})
export class ResetSellersDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<ResetSellersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {}

  @HostListener('window:keyup.esc') onKeyUp(): void {
    this.dialogRef.close();
  }
}
