import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { Reference } from 'src/app/modules/shared/interfaces/reference.interface';
import { FlowRateUnitsService } from 'src/app/modules/shared/services/flow-rate-units.service';
import { OriginsService } from 'src/app/modules/shared/services/origins.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { FlowRateSummaryService } from '../../services/flow-rate-summary.service';
import { FlowRateSummaryUpdateDialogComponent } from '../flow-rate-summary-update-dialog/flow-rate-summary-update-dialog.component';

@Component({
  selector: 'app-flow-rate-summary',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row-top-button.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './flow-rate-summary.component.scss',
  ],
  providers: [FlowRateSummaryService, FlowRateUnitsService, OriginsService],
})
export class FlowRateSummaryComponent extends DataRowComponent {
  constructor(
    public service: FlowRateSummaryService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public unitsService: FlowRateUnitsService,
    public originsService: OriginsService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() waterRightTypeCode: string = null;
  @Output() reloadPeriods = new EventEmitter<void>();

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'flowRateSummary',
      title: 'Flow Rate Max & Unit',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'flowRate',
      title: 'Flow Rate',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [
        WRISValidators.isNumber(8, 2),
        WRISValidators.notAllowedIfAnyOtherFieldsNonNull({
          columnId: 'flowRateDescription',
          title: 'Flow Rate Description',
        }),
      ],
    },
    {
      columnId: 'flowRateUnit',
      title: 'Flow Rate Unit',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'originCode',
      title: 'Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'originDescription',
      title: 'Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 250,
    },
    {
      columnId: 'flowRateDescription',
      title: 'Flow Rate Description',
      type: FormFieldTypeEnum.TextArea,
      width: 800,
      validators: [
        Validators.maxLength(350),
        WRISValidators.notAllowedIfAnyOtherFieldsNonNull(
          { columnId: 'flowRateUnit', title: 'Flow Rate Unit' },
          { columnId: 'flowRate', title: 'Flow Rate' }
        ),
      ],
    },
  ];
  public dialogWidth = '400px';
  public title = 'Flow Rate Summary';
  public origins: SelectionInterface[];
  public units: SelectionInterface[];

  public initFunction(): void {
    this._get();
  }

  protected _getHelperFunction(data: any): { [key: string]: any } {
    return { ...data.get.results };
  }

  protected populateDropdowns(): void {
    this.observables.units = new ReplaySubject(1);
    this.unitsService.get({}).subscribe((units: { results: Reference[] }) => {
      this.units = units.results.map((unit: Reference) => ({
        name: unit.description,
        value: unit.value,
      }));
      this.units.unshift({ name: '', value: null });
      this.observables.units.next(units);
      this.observables.units.complete();
    });
    this.observables.origins = new ReplaySubject(1);
    this.originsService
      .get({})
      .subscribe((origins: { results: Reference[] }) => {
        this.origins = origins.results.map((origin: Reference) => ({
          name: origin.description,
          value: origin.value,
        }));
        this.observables.origins.next(origins);
        this.observables.origins.complete();
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
    const dialogRef = this.dialog.open(FlowRateSummaryUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title}`,
        columns: this.columns,
        values: data,
        originSelectArray: this.origins,
        unitSelectArray: this.units,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(result);
      }
      this.editButton.focus();
    });
  }

  protected _update(updatedRow: any): void {
    this.service.update(updatedRow, ...this.idArray).subscribe(
      () => {
        this._get();
        this.snackBar.open('Record successfully updated.', null);
        this.reloadPeriods.next();
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
      canPUT:
        this.endpointService.canPUT(this.service.url, 0) &&
        this.canEdit &&
        ['63GW', 'ITSC', 'NNAD', 'RSCL', 'STOC', 'IRRD'].includes(
          this.waterRightTypeCode
        ),
    };
  }
}
