import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-representatives-dialog',
  templateUrl: './representatives-dialog.component.html',
  styleUrls: ['./representatives-dialog.component.scss'],
})
export class RepresentativesDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {}
}
