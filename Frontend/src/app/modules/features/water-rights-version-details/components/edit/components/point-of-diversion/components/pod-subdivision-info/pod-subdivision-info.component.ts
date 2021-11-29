import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { PodDropdownService } from '../../services/pod-dropdown.service';
import { PodSubdivisionInfoService } from '../../services/pod-subdivision-update.service';
import { PodSubdivisionUpdateDialogComponent } from '../pod-subdivision-update-dialog/pod-subdivision-update-dialog.component';

@Component({
  selector: 'app-pod-subdivision-info',
  templateUrl: './pod-subdivision-info.component.html',
  styleUrls: ['./pod-subdivision-info.component.scss'],
  providers: [PodSubdivisionInfoService],
})
export class PodSubdivisionInfoComponent
  extends DataRowComponent
  implements OnDestroy
{
  constructor(
    public service: PodSubdivisionInfoService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public dropdownService: PodDropdownService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  private unsubscribe = new Subject();

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() set values(value: any) {
    if (value === null) {
      this.dataMessage = 'No data found';
    }
    this.data = value;
  }
  get values(): any {
    return this.data;
  }
  @Output() reloadDetails = new EventEmitter<void>();

  public firstColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'subdivisionCode',
      title: 'Subdivision Code',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'dnrcName',
      title: 'DNRC Name',
      type: FormFieldTypeEnum.TextArea,
      width: 340,
      formWidth: 350,
    },
    {
      columnId: 'dorName',
      title: 'DOR Name',
      type: FormFieldTypeEnum.TextArea,
      width: 340,
      formWidth: 350,
    },
  ];
  public secondColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'lot',
      title: 'Lot',
      type: FormFieldTypeEnum.Input,
      width: 120,
      validators: [Validators.maxLength(8)],
    },
    {
      columnId: 'block',
      title: 'Block',
      type: FormFieldTypeEnum.Input,
      width: 120,
      validators: [Validators.maxLength(8)],
    },
    {
      columnId: 'tract',
      title: 'Tract',
      type: FormFieldTypeEnum.Input,
      width: 190,
      validators: [Validators.maxLength(20)],
    },
  ];

  public title = 'Subdivision Info';

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public selectArrays: { [key: string]: SelectionInterface[] };
  protected populateDropdowns(): void {
    this.selectArrays = {
      description40: this.dropdownService.aliquots,
      description80: this.dropdownService.aliquots,
      description160: this.dropdownService.aliquots,
      description320: this.dropdownService.aliquots,
      countyId: this.dropdownService.counties,
      ditchId: this.dropdownService.ditchTypeCode,
      majorTypeCode: this.dropdownService.majorTypeCode,
      meansOfDiversionCode: this.dropdownService.meansOfDiversionCode,
      minorTypeCode: this.dropdownService.minorTypeCode,
      podOriginCode: this.dropdownService.podOriginCode,
      podTypeCode: this.dropdownService.podTypeCode,
      rangeDirection: this.dropdownService.rangeDirections,
      sourceOriginCode: this.dropdownService.sourceOriginCode,
      townshipDirection: this.dropdownService.townshipDirections,
    };
  }

  protected _get(): void {
    this.reloadDetails.emit();
  }

  protected _buildEditDto(data: any) {
    const row = { ...data };
    return row;
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

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(PodSubdivisionUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Subdivision Info',
        columns: this.firstColumns,
        secondColumns: this.secondColumns,
        values: this.data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(this._buildEditDto(result));
      }
      this.editButton.focus();
    });
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0) && this.canEdit,
    };
  }
}
