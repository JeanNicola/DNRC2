import { Component, HostListener } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-change-description-confirm-dialog',
  templateUrl: './change-description-confirm-dialog.component.html',
  styleUrls: ['./change-description-confirm-dialog.component.scss'],
})
export class ChangeDescriptionConfirmDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<ChangeDescriptionConfirmDialogComponent>
  ) {}

  @HostListener('window:keyup.esc') onKeyUp(): void {
    this.dialogRef.close();
  }
}
