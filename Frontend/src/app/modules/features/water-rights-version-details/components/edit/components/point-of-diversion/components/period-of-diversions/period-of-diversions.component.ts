import { Component, EventEmitter, Input, OnDestroy } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { Reference } from 'src/app/modules/shared/interfaces/reference.interface';
import { OriginsService } from 'src/app/modules/shared/services/origins.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { PeriodOfDiversionsService } from '../../services/period-of-diversions.service';

@Component({
  selector: 'app-period-of-diversions',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './period-of-diversions.component.scss',
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [PeriodOfDiversionsService, OriginsService],
})
export class PeriodOfDiversionsComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  constructor(
    public service: PeriodOfDiversionsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public originService: OriginsService
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
  @Input() periodReloadEvent: EventEmitter<void>;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.MonthDayDate,
      validators: [Validators.required],
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.MonthDayDate,
      validators: [Validators.required],
    },
    {
      columnId: 'diversionOriginCode',
      title: 'Period Of Diversion Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'diversionOriginDescription',
      title: 'Period Of Diversion Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'flowRate',
      title: 'Flow Rate',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [WRISValidators.isNumber(8, 2)],
    },
    {
      columnId: 'flowRateSummary',
      title: 'Flow Rate & Unit',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
  ];
  public primarySortColumn = 'beginDate';
  public sortDirection = 'desc';
  public title = 'Period of Diversion';
  public searchable = false;

  protected initFunction(): void {
    this.periodReloadEvent.pipe(takeUntil(this.unsubscribe)).subscribe(() => {
      this._get();
    });
  }

  protected _getHelperFunction(data: any): any {
    this._getColumn('flowRate').validators = [
      WRISValidators.isNumber(8, 2),
      Validators.max(data.get.maxFlowRate),
    ];
    return data.get;
  }

  protected populateDropdowns(): void {
    this.observables.origins = new ReplaySubject(1);
    this.originService
      .get({})
      .subscribe((origins: { results: Reference[] }) => {
        this._getColumn('diversionOriginCode').selectArr = origins.results.map(
          (origin: Reference) => ({
            name: origin.description,
            value: origin.value,
          })
        );
        this.observables.origins.next(origins);
        this.observables.origins.complete();
      });
  }

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, { diversionOriginCode: 'ISSU' })
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
    return [...this.idArray, originalData.periodId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].periodId];
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url) && this.canEdit,
    };
  }

  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}
}
