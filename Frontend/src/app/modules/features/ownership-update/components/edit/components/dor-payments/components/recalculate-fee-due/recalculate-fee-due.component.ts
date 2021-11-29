import { Component, HostListener } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-recalculate-fee-due',
  templateUrl: './recalculate-fee-due.component.html',
  styleUrls: ['./recalculate-fee-due.component.scss'],
})
export class RecalculateFeeDueComponent {
  constructor(private dialogRef: MatDialogRef<RecalculateFeeDueComponent>) {}

  @HostListener('window:keyup.esc') onKeyUp(): void {
    this.dialogRef.close();
  }
}
