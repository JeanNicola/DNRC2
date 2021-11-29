import { Component, HostListener, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-error-dialog',
  templateUrl: './error-dialog.component.html',
})
export class ErrorDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      title: string;
      message: string;
    },
    private dialogRef: MatDialogRef<ErrorDialogComponent>
  ) {}

  @HostListener('window:keyup.esc') onKeyUp(): void {
    this.dialogRef.close();
  }
}
