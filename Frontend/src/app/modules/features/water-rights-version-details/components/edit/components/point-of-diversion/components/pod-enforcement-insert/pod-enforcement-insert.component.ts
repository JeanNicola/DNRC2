import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import {
  Enforcement,
  EnforcementsService,
} from 'src/app/modules/shared/services/enforcements.service';

@Component({
  selector: 'app-pod-enforcement-insert',
  templateUrl: './pod-enforcement-insert.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
    './pod-enforcement-insert.component.scss',
  ],
  providers: [EnforcementsService],
})
export class PodEnforcementInsertComponent extends InsertDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public endpointService: EndpointsService,
    public enforcementService: EnforcementsService
  ) {
    super(dialogRef, data);
    this.mode = this.data.mode ?? DataManagementDialogModes.Insert;
  }
  public canInsert = false;

  public createColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'areaId',
      title: 'Enf Area',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(10)],
    },
    {
      columnId: 'name',
      title: 'Enf Name',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(60)],
    },
  ];

  protected initFunction(): void {
    this.canInsert = this.endpointService.canPOST(this.enforcementService.url);
  }

  public onCreateEnforcement(data: any = {}): void {
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: '500px',
      data: {
        title: 'Create New Enforcement',
        columns: this.createColumns,
        values: data,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this.enforcementService.insert(result).subscribe(
          (savedEnforcement: Enforcement) => {
            this.addEnforcementSelection(savedEnforcement);
            this.formGroup.controls.areaId.setValue(savedEnforcement.areaId);
            this.formGroup.markAsDirty();
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error as ErrorBodyInterface;
            let message = 'Cannot insert new record. ';
            message += errorBody.userMessage || ErrorMessageEnum.POST;
            this.snackBar.open(message);
            this.onCreateEnforcement(result);
          }
        );
      }
    });
  }

  private addEnforcementSelection(enforcement: Enforcement): void {
    const column = this.displayFields.find(
      (c: ColumnDefinitionInterface) => c.columnId === 'areaId'
    );
    column.selectArr.unshift({
      name: `${enforcement.areaId}, ${enforcement.name}`,
      value: enforcement.areaId,
    });
    column.selectArr.sort(
      (first: SelectionInterface, second: SelectionInterface) => {
        if (first.name < second.name) {
          return -1;
        } else if (first.name > second.name) {
          return 1;
        } else {
          return 0;
        }
      }
    );
  }
}
