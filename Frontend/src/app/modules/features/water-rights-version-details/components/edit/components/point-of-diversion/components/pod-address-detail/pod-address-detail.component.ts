import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CityZipCodesService } from 'src/app/modules/features/code-tables/components/city-zipcode/services/city-zip-codes.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { PodAddressUpdateService } from '../../services/pod-address-update.service';
import { PodAddressUpdateDialogComponent } from '../pod-address-update-dialog/pod-address-update-dialog.component';

@Component({
  selector: 'app-pod-address-detail',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row-top-button.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './pod-address-detail.component.scss',
  ],
  providers: [PodAddressUpdateService, CityZipCodesService],
})
export class PodAddressDetailComponent extends DataRowComponent {
  constructor(
    public service: PodAddressUpdateService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public zipCodeService: CityZipCodesService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

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

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'fullAddress',
      title: 'Address',
      width: 505,
      type: FormFieldTypeEnum.TextArea,
      displayInEdit: false,
    },
    {
      columnId: 'addressLine',
      title: 'Address Line',
      type: FormFieldTypeEnum.TextArea,
      displayInTable: false,
      validators: [
        Validators.maxLength(50),
        WRISValidators.requireOtherFieldIfNonNull('zipCode'),
      ],
      formWidth: 400,
    },
    {
      columnId: 'zipCode',
      title: 'Zip Code',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [
        Validators.maxLength(5),
        Validators.minLength(5),
        WRISValidators.requireOtherFieldIfNonNull('addressLine'),
      ],
    },
    {
      columnId: 'zipCodeId',
      title: 'City',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
  ];
  public title = 'Property Address';

  protected _get(): void {
    this.reloadDetails.emit();
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
    const dialogRef = this.dialog.open(PodAddressUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title}`,
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.findZipCodeAndUpdate(result);
      }
      this.editButton.focus();
    });
  }

  protected _buildEditDto(data: any): any {
    const dto = { ...data };
    delete dto.zipCode;
    return dto;
  }

  protected findZipCodeAndUpdate(updatedRow: any): void {
    if (!updatedRow.zipCodeId && Boolean(updatedRow.zipCode)) {
      this.zipCodeService
        .get({ filters: { zipCode: updatedRow.zipCode } })
        .subscribe((data) => {
          if (data.totalElements < 1) {
            this.snackBar.open('Please provide a valid Zip Code.');
            this._displayEditDialog(updatedRow);
          } else if (data.totalElements > 1) {
            this.snackBar.open('Please select a City');
            this._displayEditDialog(updatedRow);
          } else {
            updatedRow.zipCodeId = data.results[0].id;
            this._update(updatedRow);
          }
        });
    } else {
      this._update(updatedRow);
    }
  }

  protected _update(updatedRow: any): void {
    this.service
      .update(this._buildEditDto(updatedRow), ...this.idArray)
      .subscribe(
        () => {
          this._get();
          this.snackBar.open('Record successfully updated.', null);
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error;
          let message = 'Cannot update record. ';
          message += errorBody.userMessage || ErrorMessageEnum.PUT;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayEditDialog(updatedRow);
        }
      );
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
