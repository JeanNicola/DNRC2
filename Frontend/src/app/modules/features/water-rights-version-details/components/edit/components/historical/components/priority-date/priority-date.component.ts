import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Subject, BehaviorSubject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { HistoricalData, Historical } from '../../historical.component';
import { PriorityDateService } from './services/priority-date.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

@Component({
  selector: 'app-priority-date',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row-top-button.component.html',

  // './priority-date.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './priority-date.component.scss',
  ],
  providers: [PriorityDateService],
})
export class PriorityDateComponent extends DataRowComponent {
  @Input() historical: BehaviorSubject<HistoricalData | null>;
  @Input() waterRightTypeCode?: string;
  @Input() versionTypeCode?: string;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = true;

  @Output() reloadHeader: EventEmitter<boolean> = new EventEmitter<boolean>();

  private unsubscribe = new Subject();

  public title = 'Priority Date and Time';
  public paging = false;
  public searchable = false;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'priorityDate',
      title: 'Priority Date & Time',
      type: FormFieldTypeEnum.DateOrDateTime,
      width: 190,
      validators: [
        WRISValidators.dateBeforeToday,
        WRISValidators.beforeOtherField('enforceableDate', 'Enforcement Date'),
      ],
    },
    {
      columnId: 'priorityDateOrigin',
      title: 'Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'priorityDateOriginMeaning',
      title: 'Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 235,
    },
    {
      columnId: 'enforceableDate',
      title: 'Enforcement Date',
      type: FormFieldTypeEnum.DateOrDateTime,
      width: 190,
      validators: [
        WRISValidators.dateBeforeToday,
        WRISValidators.afterOtherField('priorityDate', 'Priority Date'),
      ],
    },
    {
      columnId: 'adjudicationProcess',
      title: 'Adjudication Process',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      width: 190,
    },
    {
      columnId: 'adjudicationProcessMeaning',
      title: 'Adjudication Process',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 190,
    },
  ];

  constructor(
    public service: PriorityDateService,
    public endpointsService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointsService, dialog, snackBar);
  }

  protected initFunction() {
    const { waterRightId, versionId } = this.route.snapshot.params;
    this.idArray = [waterRightId, versionId];
    this.historical
      .pipe(takeUntil(this.unsubscribe), filter(Boolean))
      .subscribe(this._onGetSuccessHandler.bind(this));

    if (
      ![
        'CMPT',
        'HDRT',
        'IRRD',
        'ITSC',
        'NNAD',
        'PRDL',
        'RSCL',
        'STOC',
      ].includes(this.waterRightTypeCode) ||
      !['ORIG', 'POST', 'SPLT', 'SPPD', 'REXM', 'FINL'].includes(
        this.versionTypeCode
      )
    ) {
      this._getColumn('adjudicationProcess').editable = false;
    }

    this.disableEdit = !this.canEdit;
  }

  protected _onGetSuccessHandler(data: HistoricalData): void {
    this.data = data.record;
    this.displayData = this._getDisplayData(data.record);

    const compactFilter =
      this.waterRightTypeCode === 'CMPT'
        ? ({ value }) => value === 'CMPT'
        : ({ value }) => value !== 'CMPT';

    this._getColumn('priorityDateOrigin').selectArr = [
      { name: '', value: '' },
      ...data.elementOrigins
        .filter(compactFilter)
        .map(({ value, description }) => ({ name: description, value })),
    ];

    this._getColumn('adjudicationProcess').selectArr = [
      { name: '', value: '' },
      ...data.adjudicationProcesses.map(({ value, description }) => ({
        name: description,
        value,
      })),
    ];
  }

  protected _onPutSuccessHandler(data: Historical): void {
    this.data = data;
    this.displayData = this._getDisplayData(data);
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

  protected _displayEditDialog(data: any = this.data): void {
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title}`,
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      this.editButton.focus();
      if (!result) {
        return;
      }
      this._update(result);
    });
  }

  protected _get(): void {}

  protected _update(values: any): void {
    this.service.update(values, ...this.idArray).subscribe(
      (response: Historical) => {
        this._onPutSuccessHandler(response);
        this.snackBar.open('Record successfully updated.', null);
        this.reloadHeader.emit(true);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update record. ';
        message += errorBody.userMessage || ErrorMessageEnum.PUT;
        this.snackBar.open(message);
        this._displayEditDialog();
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

  ngOnDestroy() {
    this.dialog.closeAll();
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
