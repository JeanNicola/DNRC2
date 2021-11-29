import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import {
  Enforcement,
  EnforcementsService,
} from 'src/app/modules/shared/services/enforcements.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { PodEnforcementsService } from '../../services/pod-enforcements.service';
import { PodEnforcementInsertComponent } from '../pod-enforcement-insert/pod-enforcement-insert.component';

@Component({
  selector: 'app-pod-enforcements',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './pod-enforcements.component.scss',
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [PodEnforcementsService, EnforcementsService],
})
export class PodEnforcementsComponent extends BaseCodeTableComponent {
  constructor(
    public service: PodEnforcementsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public enforcementsService: EnforcementsService,
    public router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() set idArray(id: string[]) {
    if (id?.includes(null)) {
      this.dataMessage = 'No data found';
    } else if (!id?.includes(undefined)) {
      this._idArray = id;
      this._get();
    }
  }
  get idArray(): string[] {
    return this._idArray;
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'areaId',
      title: 'Enf Area',
      width: 100,
      formWidth: 610,
      type: FormFieldTypeEnum.Autocomplete,
      validators: [Validators.required],
    },
    {
      columnId: 'enforcementNumber',
      title: 'Enf #',
      width: 100,
      formWidth: 610,
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(20)],
    },
    {
      columnId: 'comments',
      title: 'Enf Comment',
      width: 600,
      formWidth: 610,
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(4000)],
    },
  ];
  public title = 'Enforcements';
  public searchable = false;

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [
      ...this.idArray,
      originalData.areaId,
      originalData.enforcementNumber,
    ];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [
      ...this.idArray,
      this.rows[rowNumber].areaId,
      this.rows[rowNumber].enforcementNumber,
    ];
  }

  protected populateDropdowns(): void {
    this.observables.enforcements = new ReplaySubject(1);
    this.enforcementsService
      .getAll()
      .subscribe((enforcements: { results: Enforcement[] }) => {
        this._getColumn('areaId').selectArr = enforcements.results.map(
          (enf: Enforcement) => ({
            name: `${enf.areaId}, ${enf.name}`,
            value: enf.areaId,
          })
        );
        this.observables.enforcements.next(enforcements);
        this.observables.enforcements.complete();
      });
    this.observables.enforcements = new ReplaySubject(1);
  }

  public rowClick(data: any): void {
    void this.router.navigate([
      'wris',
      'water-court',
      'enforcement-projects',
      data.areaId,
    ]);
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url, 2) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url, 2) && this.canEdit,
    };
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(PodEnforcementInsertComponent, {
      width: this.dialogWidth,
      data: {
        title: `Add New ${this.title}`,
        columns: this.columns,
        values: data,
        validators: this.validators,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(PodEnforcementInsertComponent, {
      width: this.dialogWidth,
      data: {
        mode: DataManagementDialogModes.Update,
        title: `Update ${this.title}`,
        columns: this.columns,
        values: data,
        validators: this.validators,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }

  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}
}
