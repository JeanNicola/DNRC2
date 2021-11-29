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
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { PodDropdownService } from '../../services/pod-dropdown.service';
import { PodSourceUpdateService } from '../../services/pod-source-update.service';
import { PodSourceUpdateDialogComponent } from '../pod-source-update-dialog/pod-source-update-dialog.component';

@Component({
  selector: 'app-pod-source-detail',
  templateUrl: './pod-source-detail.component.html',
  styleUrls: ['./pod-source-detail.component.scss'],
  providers: [PodSourceUpdateService],
})
export class PodSourceDetailComponent
  extends DataRowComponent
  implements OnDestroy
{
  constructor(
    public service: PodSourceUpdateService,
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
  public title = 'Source';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'sourceOriginDescription',
      title: 'Source Origin',
      type: FormFieldTypeEnum.Select,
      width: 250,
    },
    {
      columnId: 'unnamedTributary',
      title: 'Unnamed Tributary',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'sourceName',
      title: 'Source/Fork',
      type: FormFieldTypeEnum.Select,
      width: 380,
    },
  ];

  public majorColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'majorTypeDescription',
      title: 'Major Type',
      type: FormFieldTypeEnum.Input,
      width: 280,
    },
    {
      columnId: 'minorTypeDescription',
      title: 'Minor Type',
      type: FormFieldTypeEnum.Input,
      width: 160,
    },
  ];

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
    delete row.source;
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
    const dialogRef = this.dialog.open(PodSourceUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Source',
        columns: [],
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

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0) && this.canEdit,
    };
  }
}
