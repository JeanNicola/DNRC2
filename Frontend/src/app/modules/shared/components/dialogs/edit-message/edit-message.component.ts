import { Component, HostListener, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-message',
  templateUrl: './edit-message.component.html',
  styleUrls: ['./edit-message.component.scss'],
})
export class EditMessageComponent implements OnInit {
  constructor(
    private dialogRef: MatDialogRef<EditMessageComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}
  public message = this.data?.message || 'Your message here...';

  public ngOnInit() {
    if (!this.data || !this.data?.message) {
      this.data = {
        title: 'Your Title Here',
        message:
          'WARNING - You are modifying a locked record. Do you want to continue?',
      };
    }
  }

  @HostListener('window:keyup.esc') onKeyUp(): void {
    this.dialogRef.close();
  }
}
