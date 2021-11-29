import { CdkStepper } from '@angular/cdk/stepper';
import { AfterViewInit, Component, Inject, ViewChild } from '@angular/core';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { MatTableDataSource } from '@angular/material/table';
import { Moment } from 'moment';
import { Subject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { PurposeTypesEnum } from 'src/app/modules/features/purposes/shared/purpose-types.enum';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { InsertUpdateAcreageComponent } from '../../../../../create/components/insert-update-acreage/insert-update-acreage.component';
import { InsertUpdatePeriodComponent } from '../../../../../create/components/insert-update-period/insert-update-period.component';
import { LegalLandService } from './services/legal-land.service';

@Component({
  selector: 'app-purpose-insert-or-edit-dialog',
  templateUrl: './purpose-insert-or-edit-dialog.component.html',
  styleUrls: ['./purpose-insert-or-edit-dialog.component.scss'],
  providers: [CdkStepper, LegalLandService],
})
export class PurposeInsertOrEditDialogComponent
  extends DataManagementDialogComponent
  implements AfterViewInit
{
  // Stepper variables
  @ViewChild('stepper') stepper: MatStepper;
  @ViewChild('addPeriodButton') addPeriodButton: any;
  @ViewChild('addNewPlaceOfUse') addNewPlaceOfUse: any;
  public currentStep = 0;
  public selectedStepIndex = 0;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<PurposeInsertOrEditDialogComponent>,
    public legalLandService: LegalLandService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(dialogRef);
  }

  public title = this.data.title;
  public purposeTypes = PurposeTypesEnum;
  public reloadColumns$ = new Subject<{
    columns: ColumnDefinitionInterface[];
  }>();
  public dialogModesEnum = DataManagementDialogModes;
  // Acreages and periods data is handled locally, that's why we have fixed permissions
  public acreageAndPeriodPermissions = {
    canGET: false,
    canPOST: true,
    canDELETE: true,
    canPUT: true,
  };

  // State variables
  public formWasInitialized = false;
  public currentPurposeCodeValue = this.data.values?.purposeCode;
  public placesOfUse = this.data.values?.placesOfUse?.length
    ? this.data.values.placesOfUse.map((r) => {
        return {
          ...this.createPlaceOfUseRecord(r),
          formRecord: r,
        };
      })
    : [];
  public placesOfUseDataSource = new MatTableDataSource(this.placesOfUse);
  public periods = this.data.values?.periodsOfUse?.length
    ? this.data.values.periodsOfUse.map((r) => {
        return {
          ...this.createPeriodDataTableRecord(r),
          formRecord: r,
        };
      })
    : [];
  public periodsDataSource = new MatTableDataSource(this.periods);
  public typeChanged = false;

  // Purpose basic columns ids
  public basicPurposeColumnIds = [
    'purposeCode',
    'purposeOrigin',
    'modifiedByThisChange',
  ];

  public displayFields = this._getDisplayFields(this.data.columns);

  protected initFunction(): void {
    // Remove modifiedByThisChange if version === 1
    if (this.data.values?.versionNumber === 1) {
      this.basicPurposeColumnIds = this.basicPurposeColumnIds.filter(
        (c) => c !== 'modifiedByThisChange'
      );
    }

    // Add clarification and purposeVolume if we're on Update mode
    if (this.data.mode === DataManagementDialogModes.Update) {
      this.basicPurposeColumnIds = [
        ...this.basicPurposeColumnIds,
        ...['clarification', 'purposeVolume'],
      ];
    }
  }

  public ngAfterViewInit(): void {
    // Stepper will only be available on INSERT mode
    if (this.data.mode === DataManagementDialogModes.Insert) {
      setTimeout(() => {
        this.formWasInitialized = true;
        if (this.stepper) {
          this.stepper.steps.forEach((step, toGoIndex) => {
            step.select = () => {
              this.handleCurrentStep(toGoIndex);
            };
          });
        }
      });
    }
  }

  protected _getDisplayFields(columns: ColumnDefinitionInterface[]) {
    return columns.map((item) => ({
      ...item,
    }));
  }

  public handleCurrentStep(toGoIndex: number): void {
    if (this.currentStep === 0 && this.formGroup.valid) {
      this.currentStep = toGoIndex;
      setTimeout(() => {
        this.selectedStepIndex = toGoIndex;
      });
    } else if (this.currentStep !== 0) {
      this.currentStep = toGoIndex;
      setTimeout(() => {
        this.selectedStepIndex = toGoIndex;
      });
    }
  }

  public onDeleteAcreage(row: number): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this.placesOfUse.splice(row, 1);
        this.placesOfUseDataSource._updateChangeSubscription();
      }
      if (this.addNewPlaceOfUse) this.addNewPlaceOfUse.focus();
    });
  }

  public onDeletePeriod(row: number): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this.periods.splice(row, 1);
        this.periodsDataSource._updateChangeSubscription();
      }
      if (this.addPeriodButton) this.addPeriodButton.focus();
    });
  }

  protected afterFormWasReloaded() {
    setTimeout(() => {
      this.formWasInitialized = true;
    });
  }

  public onInputChangeHandler($event) {
    if ($event.fieldName === 'purposeCode') {
      this.typeChanged = true;
      this.currentPurposeCodeValue = $event.value;
      // Reset form
      this.formWasInitialized = false;
      this.reloadColumns$.next({
        columns: this.displayFields,
      });
      if (
        ![
          PurposeTypesEnum.IRRIGATION,
          PurposeTypesEnum.LAWN_AND_GARDEN,
        ].includes(this.currentPurposeCodeValue)
      ) {
        this.placesOfUse = [];
        this.placesOfUseDataSource._updateChangeSubscription();
      }
      if (
        ![
          PurposeTypesEnum.DOMESTIC,
          PurposeTypesEnum.MULTIPLE_DOMESTIC,
        ].includes(this.currentPurposeCodeValue)
      ) {
        this.periods = [];
        this.periodsDataSource._updateChangeSubscription();
      } else if (
        this.currentPurposeCodeValue === PurposeTypesEnum.DOMESTIC &&
        this.data.values?.waterRightTypeCode === 'GWCT'
      ) {
        setTimeout(() => {
          this.formGroup.get('household').setValue(1);
        });
      } else {
        setTimeout(() => {
          this.formGroup.get('household').setValue(null);
        });
      }
    }
  }

  private _getDialogColumns(
    columnId: string,
    columns
  ): ColumnDefinitionInterface {
    return columns.find(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );
  }

  private findDialogColumnValue(
    fieldName: string,
    value: string,
    columns: ColumnDefinitionInterface[]
  ) {
    return this._getDialogColumns(fieldName, columns).selectArr.find(
      (option) => option.value === value
    )?.name;
  }

  private createPeriodDataTableRecord(result) {
    return {
      periodBegin: (result.beginDate as Moment).format(
        this.data.values.has650Application ? 'MM/DD/YYYY' : 'MM/DD'
      ),
      periodEnd: (result.endDate as Moment).format(
        this.data.values.has650Application ? 'MM/DD/YYYY' : 'MM/DD'
      ),
      elementOriginDescription: this.findDialogColumnValue(
        'elementOrigin',
        result.elementOrigin,
        this.data.periodColumns
      ),
      leaseYear: result.leaseYear,
    };
  }

  private createPlaceOfUseRecord(result) {
    const realValues = {} as any;

    [
      'description40',
      'description80',
      'description160',
      'description320',
      'townshipDirection',
      'rangeDirection',
      'countyId',
    ].forEach((columnId) => {
      realValues[columnId] = this.findDialogColumnValue(
        columnId,
        result[columnId],
        [
          ...this.data.firstLegalLandDescriptionColumns,
          ...this.data.secondLegalLandDescriptionColumns,
        ]
      );
    });

    const completeLegalLandDescription = [
      result.governmentLot ? 'Govt Lot ' : null,
      result.governmentLot,
      realValues.description40,
      realValues.description80,
      realValues.description160,
      realValues.description320,
      result.section,
      result.township,
      realValues.townshipDirection,
      result.range,
      realValues.rangeDirection,
      realValues.countyId,
    ]
      .filter(Boolean)
      .join(' ');

    let placeOfUse: {
      acreage: number;
      elementOriginDescription: string;
      completeLegalLandDescription: string;
      modifiedByThisChangeDescription?: string;
    } = {
      acreage: result.acreage,
      elementOriginDescription: this.findDialogColumnValue(
        'elementOrigin',
        result.elementOrigin,
        this.data.placeOfUseColumns
      ),
      completeLegalLandDescription,
    };

    if (this.data.values?.versionNumber !== 1) {
      placeOfUse.modifiedByThisChangeDescription = this.findDialogColumnValue(
        'modifiedByThisChange',
        result.modifiedByThisChange,
        this.data.placeOfUseColumns
      );
    }

    return placeOfUse;
  }

  private createLegalLandValidationRequest(result) {
    const queryParameters = {
      filters: [
        'description40',
        'description80',
        'description160',
        'description320',
        'governmentLot',
        'section',
        'township',
        'townshipDirection',
        'range',
        'rangeDirection',
        'countyId',
      ]
        .filter((f: string) => result[f])
        .reduce((filters, f) => {
          return { ...filters, [f]: result[f] };
        }, {}),
    } as any;

    return this.legalLandService.get(queryParameters);
  }

  public onInsertPeriod(data) {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdatePeriodComponent, {
      data: {
        title: `Add New Period`,
        width: '500px',
        mode: DataManagementDialogModes.Insert,
        columns: this.data.periodColumns,
        values: {
          ...data,
          has650Application: this.data.values.has650Application,
        },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        const periodRecord = {
          ...this.createPeriodDataTableRecord(result),
          formRecord: result,
        };
        this.periods.push(periodRecord);
        this.periodsDataSource.data = this.periods;
      }
      if (this.addPeriodButton) this.addPeriodButton.focus();
    });
  }

  public onEditPeriod(index) {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdatePeriodComponent, {
      data: {
        title: `Update Period`,
        width: '500px',
        mode: DataManagementDialogModes.Update,
        columns: this.data.periodColumns,
        values: {
          ...this.periods[index].formRecord,
          has650Application: this.data.values.has650Application,
        },
      },
    });
    // Get the input data and peform the update
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        const periodRecord = {
          ...this.createPeriodDataTableRecord(result),
          formRecord: result,
        };
        this.periods[index] = periodRecord;
        this.periodsDataSource.data = this.periods;
      }
      if (this.addPeriodButton) this.addPeriodButton.focus();
    });
  }

  public onInsertAcreage(data) {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdateAcreageComponent, {
      data: {
        title: `Add New Place Of Use`,
        mode: DataManagementDialogModes.Insert,
        placeOfUseColumns: this.data.placeOfUseColumns,
        firstLegalLandDescriptionColumns:
          this.data.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.data.secondLegalLandDescriptionColumns,
        values: { ...data, versionNumber: this.data.values?.versionNumber },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this.createLegalLandValidationRequest(result).subscribe(
          (legalId) => {
            const pouRecord = {
              ...this.createPlaceOfUseRecord(result),
              formRecord: { ...result, legalId },
            };
            this.placesOfUse.push(pouRecord);
            this.placesOfUseDataSource.data = this.placesOfUse;
          },
          (e) => {
            if (e?.error?.userMessage) {
              this.snackBar.open(e.error.userMessage);
              this.onInsertAcreage(result);
            }
          }
        );
      }
      if (this.addNewPlaceOfUse) this.addNewPlaceOfUse.focus();
    });
  }

  public onEditAcreage(index) {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdateAcreageComponent, {
      data: {
        title: `Update Place Of Use`,
        mode: DataManagementDialogModes.Update,
        placeOfUseColumns: this.data.placeOfUseColumns,
        firstLegalLandDescriptionColumns:
          this.data.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.data.secondLegalLandDescriptionColumns,
        values: {
          ...this.placesOfUse[index].formRecord,
          versionNumber: this.data.values?.versionNumber,
        },
      },
    });
    // Get the input data and peform the update
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this.createLegalLandValidationRequest(result).subscribe(
          (legalId) => {
            const pouRecord = {
              ...this.createPlaceOfUseRecord(result),
              formRecord: { ...result, legalId },
            };
            this.placesOfUse[index] = pouRecord;
            this.placesOfUseDataSource.data = this.placesOfUse;
          },
          (e) => {
            if (e?.error?.userMessage) {
              this.snackBar.open(e.error.userMessage);
              this.onEditAcreage(index);
            }
          }
        );
      }
      if (this.addNewPlaceOfUse) this.addNewPlaceOfUse.focus();
    });
  }

  public getPurposeDto() {
    const dto = {
      animalUnits: null,
      household: null,
      irrigationCode: null,
      climaticCode: null,
      rotation: null,
      ...this.formGroup.getRawValue(),
    };
    if (this.data.mode === DataManagementDialogModes.Insert) {
      dto.placesOfUse = this.placesOfUse.map((pou) => {
        return {
          acreage: pou.formRecord.acreage,
          countyId: pou.formRecord.countyId,
          elementOrigin: pou.formRecord.elementOrigin,
          modifiedByThisChange: pou.formRecord.modifiedByThisChange,
          legalId: pou.formRecord.legalId,
        };
      });
      dto.periodsOfUse = this.periods.map((p) => p.formRecord);
    }
    this.dialogRef.close(dto);
  }
}
