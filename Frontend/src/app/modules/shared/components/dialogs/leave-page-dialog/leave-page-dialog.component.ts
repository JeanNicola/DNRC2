import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-leave-page-dialog',
  templateUrl: './leave-page-dialog.component.html',
  styleUrls: ['./leave-page-dialog.component.scss'],
})
export class LeavePageDialogComponent implements OnInit {
  constructor(private dialogRef: MatDialogRef<LeavePageDialogComponent>) {}

  ngOnInit(): void {}
}
