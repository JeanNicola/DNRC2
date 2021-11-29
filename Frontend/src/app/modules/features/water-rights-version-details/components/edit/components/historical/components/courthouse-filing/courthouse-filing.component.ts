/* eslint-disable max-len */
import { Component, Input, OnDestroy, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { AbstractControl, Validators, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Subject, BehaviorSubject } from 'rxjs';
import { takeUntil, filter, tap, map } from 'rxjs/operators';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { ConfirmationDialogComponent } from 'src/app/modules/shared/components/dialogs/confirmation-dialog/confirmation-dialog.component';
import { CountiesService } from 'src/app/modules/shared/services/counties.service';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { HistoricalData, Historical } from '../../historical.component';
import { CourthouseFilingService } from './services/courthouse-filing.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { MatButton } from '@angular/material/button';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';

enum EditTypes {
  COURTHOUSE,
  DECREE,
}

class PartialDate {
  static isDay(control: AbstractControl): ValidationErrors | null {
    if (control.value == null) {
      return null;
    }
    if (control.value >= 1 && control.value <= 31) {
      return null;
    }

    return /[0-9]+/.test(control.value)
      ? { errorMessage: 'Day must be between 1 and 31' }
      : { errorMessage: 'Invalid day' };
  }

  static isMonth(control: AbstractControl): ValidationErrors | null {
    if (control.value == null) {
      return null;
    }
    if (control.value >= 1 && control.value <= 12) {
      return null;
    }

    return /[0-9]+/.test(control.value)
      ? { errorMessage: 'Month must be between 1 and 12' }
      : { errorMessage: 'Invalid month' };
  }

  static isYear(control: AbstractControl): ValidationErrors | null {
    if (control.value == null) {
      return null;
    }
    if (control.value >= 1850 && control.value <= 2100) {
      return null;
    }

    return /[0-9]+/.test(control.value)
      ? { errorMessage: 'Year must be between 1850 and 2100' }
      : { errorMessage: 'Invalid year' };
  }
}

@Component({
  selector: 'app-courthouse-filing',
  templateUrl: './courthouse-filing.component.html',
  styleUrls: ['./courthouse-filing.component.scss'],
  providers: [CourthouseFilingService, CountiesService],
})
export class CourthouseFilingComponent
  extends DataRowComponent
  implements OnDestroy
{
  @ViewChild('editDecreeButton', { static: false }) editDecreeButton: MatButton;
  @Input() historical: BehaviorSubject<HistoricalData | null>;
  @Input() waterRightTypeCode?: string;
  @Input() versionTypeCode?: string;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit: false;

  private unsubscribe = new Subject();

  public paging = false;
  public searchable = false;
  public editTypes = EditTypes;
  public disableEditCourthouse = false;
  public disableEditDecreeHistory = false;

  public historicalFiling: ColumnDefinitionInterface[] = [
    {
      columnId: 'rightType',
      title: 'Right Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'rightTypeMeaning',
      title: 'Right Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'rightTypeOrigin',
      title: 'Right Type Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'rightTypeOriginMeaning',
      title: 'Right Type Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'caseNumber',
      title: 'Case/Document #',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(20)],
    },
    {
      columnId: 'filingDate',
      title: 'Filing Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'countyId',
      title: 'County',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
    },
    {
      columnId: 'county',
      title: 'County',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
  ];

  public decreeHistory: ColumnDefinitionInterface[] = [
    {
      columnId: 'decreeAppropriator',
      title: 'Decreed Appropriator',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(200)],
    },
    {
      columnId: 'decreedMonth',
      title: 'Decreed Month',
      type: FormFieldTypeEnum.Input,
      validators: [PartialDate.isMonth],
    },
    {
      columnId: 'decreedDay',
      title: 'Decreed Day',
      type: FormFieldTypeEnum.Input,
      validators: [PartialDate.isDay],
    },
    {
      columnId: 'decreedYear',
      title: 'Decreed Year',
      type: FormFieldTypeEnum.Input,
      validators: [PartialDate.isYear],
    },
    {
      columnId: 'minersInches',
      title: 'Miners Inches',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.min(0), WRISValidators.isNumber(8, 2)],
    },
    {
      columnId: 'source',
      title: 'Source',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(200)],
    },
    {
      columnId: 'flowDescription',
      title: 'Flow Description',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(200)],
    },
  ];

  public columns: ColumnDefinitionInterface[] = [
    ...this.historicalFiling,
    ...this.decreeHistory,
  ];

  constructor(
    public service: CourthouseFilingService,
    public endpointsService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private counties: CountiesService
  ) {
    super(service, endpointsService, dialog, snackBar);
  }

  protected initFunction(): void {
    const { waterRightId, versionId } = this.route.snapshot.params;
    this.idArray = [waterRightId, versionId];
    this.historical
      .pipe(
        takeUntil(this.unsubscribe),
        filter(Boolean),
        tap((data: HistoricalData) => {
          this._getColumnById(this.historicalFiling, 'rightType').selectArr = [
            { name: '', value: '' },
            ...data.rightTypes.map(({ value, description }) => ({
              name: description,
              value,
            })),
          ];

          this._getColumnById(
            this.historicalFiling,
            'rightTypeOrigin'
          ).selectArr = [
            { name: '', value: '' },
            ...data.elementOrigins.map(({ value, description }) => ({
              name: description,
              value,
            })),
          ];
        }),
        map((data: HistoricalData) => data.record)
      )
      .subscribe(this._onGetSuccessHandler.bind(this));

    this.counties.get().subscribe(({ results }) => {
      this._getColumnById(this.historicalFiling, 'countyId').selectArr = [
        { name: '', value: '' },
        ...results.map(({ name, id }) => ({
          name,
          value: id,
        })),
      ];
    });
  }

  protected _onGetSuccessHandler(data: Historical): void {
    // this.data = data.record;
    this.data = data;
    this.displayData = this._getDisplayData(data);

    // Disable courthouse columns that are editable if the version is a change type
    this.disableEditCourthouse =
      !this.canEdit ||
      ['CHAU', 'CHSP', 'REDU', 'REDX', 'ERSV'].includes(this.versionTypeCode);

    // Enable/disable columns that are editable if the historical right type is DECREE
    this.disableEditDecreeHistory = this.data.rightType !== 'DECR';
  }

  // Handle the onEdit event
  public onEdit(editType?: EditTypes): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data, editType)
    );
  }

  protected _displayEditDialog(data: any, editType?: EditTypes): void {
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: '550px',
      data: {
        title:
          editType === EditTypes.COURTHOUSE
            ? `Update ${this.title}`
            : 'Update Decree History',
        columns:
          editType === EditTypes.COURTHOUSE
            ? this.historicalFiling
            : this.decreeHistory,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (editType === EditTypes.DECREE) {
        this.editDecreeButton.focus();
      } else {
        this.editButton.focus();
      }
      if (!result) {
        return;
      }

      if (this.data.rightType === 'DECR' && result.rightType !== 'DECR') {
        const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
          data: {
            title: 'Change Right Type',
            message:
              'Warning: Changing the right type on this record to anything other than Decreed will delete all fields in Decree History.',
            confirmButtonName: 'Update and Delete',
          },
        });
        dialogRef.afterClosed().subscribe((state) => {
          if (state !== 'confirmed') {
            return void this._displayEditDialog(
              { ...this.data, ...result },
              editType
            );
          }
          this._update({ ...this.data, ...result }, editType);
        });
      } else {
        return void this._update({ ...this.data, ...result }, editType);
      }
    });
  }

  protected _get(): void {}

  protected _update(data: any, editType?: EditTypes): void {
    // Remove fields not necessary for updating
    const {
      county,
      dateReceived,
      enforceableDate,
      priorityDate,
      priorityDateOrigin,
      priorityDateOriginMeaning,
      rightTypeMeaning,
      rightTypeOriginMeaning,
      ...values
    } = data;

    this.service.update(values, ...this.idArray).subscribe(
      (response: Historical) => {
        this._onGetSuccessHandler(response);
        this.snackBar.open('Record successfully updated.', null);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update record. ';
        message += errorBody.userMessage || ErrorMessageEnum.PUT;
        this.snackBar.open(message);
        this._displayEditDialog(data, editType);
      }
    );
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0),
    };
  }

  ngOnDestroy(): void {
    this.dialog.closeAll();
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  protected _getColumnById(
    columns: ColumnDefinitionInterface[],
    columnId: string
  ): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = columns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }
}
