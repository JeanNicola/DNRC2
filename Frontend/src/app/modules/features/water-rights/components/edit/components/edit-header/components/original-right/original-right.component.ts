import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { WaterRightService } from 'src/app/modules/features/water-rights/services/water-right.service';
import { ConfirmationDialogComponent } from 'src/app/modules/shared/components/dialogs/confirmation-dialog/confirmation-dialog.component';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { UpdateOriginalRightComponent } from '../update-original-right/update-original-right.component';

@Component({
  selector: 'app-original-right',
  templateUrl: './original-right.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './original-right.component.scss',
  ],
  providers: [WaterRightService],
})
export class OriginalRightComponent extends DataRowComponent {
  @Input() set inputData(data: DataPageInterface<any>) {
    this.data = this._getHelperFunction({ get: data });
    this.displayData = this._getDisplayData(this.data);
  }
  constructor(
    public service: WaterRightService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Original Water Right';
  public columns = [
    {
      columnId: 'originalWaterRightId',
      title: 'Water Right Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'originalBasin',
      title: 'Basin',
      type: FormFieldTypeEnum.Select,
      width: 100,
      fontWeight: 700,
      validators: [Validators.required],
    },
    {
      columnId: 'originalWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
      displayInEdit: false,
      dblClickable: true,
    },
    {
      columnId: 'originalExt',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      width: 80,
      displayInEdit: false,
    },
    {
      columnId: 'originalTypeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      width: 300,
      displayInEdit: false,
    },
    {
      columnId: 'originalStatusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
      width: 300,
      displayInEdit: false,
    },
  ];

  public searchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightId',
      title: 'Water Right Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInSearch: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'statusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];

  public initFunction() {
    this.idArray = [this.route.snapshot.params.id];
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkDecree(
      this.data.isDecreed,
      this.data.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateOriginalRightComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Edit Original Water Right',
        columns: this.searchColumns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }
      if (result.activeChangeAuthorizationVersions === 0) {
        this._update(this._buildUpdateDto(result.waterRightId));
      } else {
        const confirmationDialog = this.dialog.open(
          ConfirmationDialogComponent,
          {
            data: {
              title: 'Confirm Version information is included',
              message: `The selected Original WR (# ${result.waterRightNumber}) has a Change Authorization version.\nDoes this WR (# ${this.data.waterRightNumber}) need the Change Authorization Version from that WR as a Split Version.`,
              confirmButtonName: 'Add',
            },
          }
        );

        confirmationDialog.afterClosed().subscribe((confirmation) => {
          if (confirmation === 'confirmed') {
            this._update(this._buildUpdateDto(result.waterRightId));
          }
        });
      }
    });
  }

  // Handle the onDelete event
  public onDelete(): void {
    WaterRightsPrivileges.checkDecree(
      this.data.isDecreed,
      this.data.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this)
    );
  }

  public _delete(): void {
    this._update(this._buildUpdateDto(null));
  }

  public _displayDeleteDialog(): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._delete();
      }
    });
  }

  private _buildUpdateDto(originalWaterRightId: number): any {
    return {
      originalWaterRightId,
      basin: this.data.basin,
      subBasin: this.data.subBasin,
      typeCode: this.data.typeCode,
      dividedOwnership: this.data.dividedOwnership,
      severed: this.data.severed,
      conservationDistrictNumber: this.data.conservationDistrictNumber,
      conservationDistrictDate: this.data.conservationDistrictDate,
      waterReservationId: this.data.waterReservationId,
    };
  }

  public clickWaterRight(): void {
    void this.router.navigate([
      'wris',
      'water-rights',
      this.data.originalWaterRightId,
    ]);
  }
}
