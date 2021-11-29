import { Component, ElementRef, Inject, ViewChild } from '@angular/core';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';
import { WaterRightOwnerSearchDialogComponent } from '../water-right-owner-search-dialog/water-right-owner-search-dialog.component';

@Component({
  selector: 'app-water-right-insert-dialog',
  templateUrl: './water-right-insert-dialog.component.html',
  styleUrls: ['./water-right-insert-dialog.component.scss'],
})
export class WaterRightInsertDialogComponent extends InsertDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialog: MatDialog,
    private snackBar: SnackBarService
  ) {
    super(dialogRef, data);
  }

  // Used to reset the focus to the insert button
  @ViewChild('insert', { read: ElementRef }) private insertButton: ElementRef;

  public permissions: PermissionsInterface = {
    canDELETE: true,
    canPUT: false,
    canPOST: false,
    canGET: true,
  };

  public owners = [];
  public dataSource = new MatTableDataSource(this.owners);

  private onInsertOwner(data: any): void {
    const dialogRef = this.dialog.open(WaterRightOwnerSearchDialogComponent, {
      data: {
        columns: this.getColumn('contactIds').list,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.contactId != null) {
        const contactIds = this.owners.map((own) => own.contactId);
        if (contactIds.indexOf(result.contactId) === -1) {
          this.owners.push(result);
          this.dataSource.data = this.owners;
        } else {
          this.onInsertOwner(data);
          this.snackBar.open(`${result.name} is already selected`);
        }
      }
      // set the focus on the insert button...
      if (this.insertButton) {
        this.insertButton.nativeElement.focus();
      }
    });
  }

  public onDelete(row: number): void {
    this.owners.splice(row, 1);
    this.dataSource._updateChangeSubscription();
  }

  public save(): any {
    const data = { ...this.formGroup.getRawValue() };
    data.contactIds = this.owners.map((owner) => owner.contactId);
    this.dialogRef.close(data);
  }

  public getColumn(columnId: string): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = this.data.columns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }
}
