import { Component, HostListener, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
})
export class ConfirmationDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      title: string;
      message: string;
      confirmButtonName: string;
      cancelButtonName?: string;
      hideCancelButton?: boolean;
    },
    private dialogRef: MatDialogRef<ConfirmationDialogComponent>
  ) {}

  @HostListener('window:keyup.esc') onKeyUp(): void {
    this.dialogRef.close();
  }
}
