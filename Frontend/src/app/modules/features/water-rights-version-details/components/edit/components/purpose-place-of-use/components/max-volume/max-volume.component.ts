/* eslint-disable max-len */
import { Component, Input, OnDestroy } from '@angular/core';
import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { merge, Observable, Subject, Subscription } from 'rxjs';
import { auditTime, map } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { PurposeDropdownsService } from 'src/app/modules/features/purposes/components/edit/components/edit-header/services/purpose-dropdowns.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { MaxVolumeUpdateDialogComponent } from './components/max-volume-update-dialog/max-volume-update-dialog.component';
import { MaxVolumeService } from './services/max-volume.service';

@Component({
  selector: 'app-max-volume',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row-top-button.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './max-volume.component.scss',
  ],
  providers: [MaxVolumeService],
})
export class MaxVolumeComponent extends DataRowComponent implements OnDestroy {
  constructor(
    public service: MaxVolumeService,
    public purposeDropdownService: PurposeDropdownsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  static volumeValidator(
    has645Application,
    versionNumber,
    waterRightTypeCode
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (
        has645Application &&
        +versionNumber === 1 &&
        ['GWCT', 'PRPM'].includes(waterRightTypeCode) &&
        control.value > 10
      ) {
        return {
          errorMessage:
            'Max Volume cannot exceed 10.0  for application type 645',
        };
      }

      return null;
    };
  }

  @Input() versionNumber = null;
  @Input() has645Application = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = false;
  @Input() waterRightTypeCode = null;
  @Input() waterRightStatusCode = null;
  @Input() reloadData: Observable<any> = null;
  private reloadData$: Subscription;

  @Input() set idArray(id: string[]) {
    this._idArray = id.filter(Boolean);
    if (this._idArray.length) {
      this._get();
    }
  }
  get idArray(): string[] {
    return super.idArray;
  }
  public title = 'Total Volume';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'volume',
      title: 'Max Volume',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      width: 110,
    },
    {
      columnId: 'volumeOriginCode',
      title: 'Volume Origin',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'volumeOriginDescription',
      title: 'Volume Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 210,
    },
    {
      columnId: 'volumeDescription',
      title: 'Description',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(350)],
      width: 375,
      formWidth: 500,
    },
  ];

  private allowDescription = true;

  protected initFunction() {
    this.allowDescription = [
      '63GW',
      'ITSC',
      'NNAD',
      'RSCL',
      'STOC',
      'IRRD',
    ].includes(this.waterRightTypeCode);

    this._getColumn('volumeDescription').displayInEdit = this.allowDescription;
    this._getColumn('volumeDescription').displayInTable = this.allowDescription;

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
    this._getColumn('volumeOriginCode').selectArr =
      this.purposeDropdownService.ownerOrigins;
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this)
    );
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(): void {
    this._getColumn('volume').validators = [
      WRISValidators.isNumber(8, 2),
      MaxVolumeComponent.volumeValidator(
        this.has645Application,
        this.versionNumber,
        this.waterRightTypeCode
      ),
    ];
    const reloadColumns = new Subject<{
      columns: ColumnDefinitionInterface[];
      markAsDirty?;
      markAllAsTouched?;
    }>();
    // Open the dialog
    const dialogRef = this.dialog.open(MaxVolumeUpdateDialogComponent, {
      width: '600px',
      data: {
        title: `Update ${this.title}`,
        columns: this.columns,
        values: this.data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(result);
      }
      this.editButton.focus();
    });

    if (this.allowDescription) {
      setTimeout(() => {
        if (this.data.volumeDescription) {
          dialogRef.componentInstance.formGroup.get('volume').disable();
        }
        if (this.data.volume) {
          dialogRef.componentInstance.formGroup
            .get('volumeDescription')
            .disable();
        }

        if (this.data.volumeDescription && this.data.volume) {
          dialogRef.componentInstance.formGroup.get('volume').enable();
        }
      });

      // Initialize .reloadColumns$ observable, this observable is used for refreshing the columns
      dialogRef.componentInstance.reloadColumns$ = reloadColumns.asObservable();
      merge(
        // changeEvent fires whenever the user clears the input
        dialogRef.componentInstance.changeEvent.pipe(
          map(($event) => ({
            fieldName: $event.fieldName,
            value: $event.target?.value,
          }))
        ),
        // blurEvent fires whenever the user clicks outside the input
        dialogRef.componentInstance.blurEvent.pipe(
          map(($event: any) => ({
            fieldName: $event.fieldName,
            value: $event.event.target?.value,
          }))
        )
      )
        // auditTime will take the last value emitted in a window of time, this is necessary because
        // Whenever changeEvent fires, the re-render of the form is going to fire blurEvent, making an extra unnecessary  call
        // auditTime prevents this unnecessary  call
        .pipe(auditTime(100))
        .subscribe(($event) => {
          // Enable fields
          dialogRef.componentInstance.formGroup.get('volume').enable();
          dialogRef.componentInstance.formGroup
            .get('volumeDescription')
            .enable();

          // Disable corresponding fields
          if ($event.fieldName === 'volume' && $event.value) {
            dialogRef.componentInstance.formGroup
              .get('volumeDescription')
              .disable();
          }
          if ($event.fieldName === 'volumeDescription' && $event.value) {
            dialogRef.componentInstance.formGroup.get('volume').disable();
          }
        });
    }
  }
}
