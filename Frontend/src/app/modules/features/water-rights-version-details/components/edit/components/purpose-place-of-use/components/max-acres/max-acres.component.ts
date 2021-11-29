/* eslint-disable max-len */
import { Component, Input, OnDestroy } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { PurposeDropdownsService } from 'src/app/modules/features/purposes/components/edit/components/edit-header/services/purpose-dropdowns.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { MaxAcresService } from './services/max-acres.service';

@Component({
  selector: 'app-max-acres',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row-top-button.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [MaxAcresService],
})
export class MaxAcresComponent extends DataRowComponent implements OnDestroy {
  constructor(
    public service: MaxAcresService,
    public ownerOriginsService: PurposeDropdownsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() waterRightTypeCode = null;
  @Input() waterRightStatusCode = null;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = false;
  @Input() set idArray(id: string[]) {
    this._idArray = id.filter(Boolean);
    if (this._idArray.length) {
      this._get();
    }
  }
  get idArray(): string[] {
    return super.idArray;
  }
  public title = 'Total Acreage';
  @Input() reloadData: Observable<any> = null;
  private reloadData$: Subscription;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'acres',
      title: 'Max Acres',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
    },
    {
      columnId: 'acresOriginCode',
      title: 'Acreage Origin',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'acresOriginDescription',
      title: 'Acreage Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 210,
    },
  ];

  protected initFunction() {
    if (this.reloadData) {
      this.reloadData$ = this.reloadData.subscribe(() => {
        this._get();
      });
    }
  }

  public ngOnDestroy(): void {
    super.ngOnDestroy();
    if (this.reloadData$) {
      this.reloadData$.unsubscribe();
    }
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url, 0) && this.canEdit,
    };
  }

  protected populateDropdowns(): void {
    // Origins
    this._getColumn('acresOriginCode').selectArr =
      this.ownerOriginsService.ownerOrigins;
  }
  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }
}
