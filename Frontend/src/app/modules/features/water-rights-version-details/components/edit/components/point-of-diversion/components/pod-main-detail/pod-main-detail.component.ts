/* eslint-disable max-len */
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { MoreInfoDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/more-info-dialog/more-info-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { PodDropdownService } from '../../services/pod-dropdown.service';
import { PodMainUpdateService } from '../../services/pod-main-update.service';
import { PodMainUpdateDialogComponent } from '../pod-main-update-dialog/pod-main-update-dialog.component';

@Component({
  selector: 'app-pod-main-detail',
  templateUrl: './pod-main-detail.component.html',
  styleUrls: ['./pod-main-detail.component.scss'],
  providers: [PodMainUpdateService],
})
export class PodMainDetailComponent
  extends DataRowComponent
  implements OnDestroy
{
  constructor(
    public service: PodMainUpdateService,
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
  @Input() displayData: any;
  @Output() reloadDetails = new EventEmitter<void>();
  public title = 'Point of Diversion';
  public dialogWidth = '700px';

  public originColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'podOriginCode',
      title: 'POD Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'podOriginDescription',
      title: 'POD Origin',
      type: FormFieldTypeEnum.Select,
      displayInEdit: false,
      width: 215,
    },
    {
      columnId: 'meansOfDiversionDescription',
      title: 'Means of Diversion',
      type: FormFieldTypeEnum.Select,
      width: 400,
    },
  ];

  public legalLandDescriptionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'legalLandDescription',
      title: 'Legal Land Description',
      width: 530,
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'xCoordinate',
      title: 'X Coordinate',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'yCoordinate',
      title: 'Y Coordinate',
      type: FormFieldTypeEnum.Input,
    },
  ];
  public secondLegalLandDescriptionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'modified',
      title: 'Modified in this Change',
      width: 150,
      type: FormFieldTypeEnum.Checkbox,
    },
  ];

  public selectArrays: { [key: string]: SelectionInterface[] };

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

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

  public moreInfoColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'diversionTypeDescription',
      title: 'Diversion Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ditchName',
      title: 'Diversion/Ditch Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'podTypeDescription',
      title: 'POD Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'transitory',
      title: 'Transitory',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];

  public moreInfo(): void {
    this.dialog.open(MoreInfoDialogComponent, {
      width: '650px',
      data: {
        title: 'Means of Diversion',
        columns: this.moreInfoColumns,
        values: {
          ...this.data,
        },
      },
    });
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
    const dialogRef = this.dialog.open(PodMainUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update POD',
        columns: this.columns,
        values: data,
        selectArrays: this.selectArrays,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(this._buildEditDto(result));
      }
      this.editButton.focus();
    });
  }

  protected _buildEditDto(editedData: any): any {
    const row = { ...editedData };
    delete row.ditchName;
    return row;
  }

  protected _get(): void {
    this.reloadDetails.emit();
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0) && this.canEdit,
    };
  }
}
