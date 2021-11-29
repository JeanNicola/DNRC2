import { Component, Input, OnDestroy } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Subject, BehaviorSubject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { HistoricalData, Historical } from '../../historical.component';
import { ClaimFilingService } from './services/claim-filing.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

@Component({
  selector: 'app-claim-filing',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row-top-button.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './claim-filing.component.scss',
  ],
  providers: [ClaimFilingService],
})
export class ClaimFilingComponent
  extends DataRowComponent
  implements OnDestroy
{
  @Input() historical: BehaviorSubject<HistoricalData | null>;
  @Input() waterRightTypeCode?: string;
  @Input() versionTypeCode?: string;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = false;

  private unsubscribe = new Subject();

  public showLoading = false;
  public title = 'Claim Filing Information';
  public paging = false;
  public searchable = false;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'dateReceived',
      title: 'Date Received',
      type: FormFieldTypeEnum.Date,
      width: 140,
    },
    {
      columnId: 'lateDesignation',
      title: 'Late Designation',
      type: FormFieldTypeEnum.Select,
      selectArr: [{ value: '' }, { value: 'A' }, { value: 'B' }],
      width: 125,
    },
    {
      columnId: 'exemptClaim',
      title: 'Exempt Claim',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'impliedClaim',
      title: 'Implied Claim',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'feeReceived',
      title: 'Fee Owed',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];

  constructor(
    public service: ClaimFilingService,
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

    this.disableEdit =
      !this.canEdit ||
      ['CHAU', 'CHSP', 'REDU', 'REDX', 'ERSV'].includes(this.versionTypeCode);

    const disabled =
      ['POST', 'SPPD'].includes(this.versionTypeCode) &&
      [
        '62GW',
        'EXEX',
        'GWCT',
        'PRPM',
        'TPRP',
        'STWP',
        'WRWR',
        'CDWR',
        'NAPP',
        'NFWP',
        'DMAL',
      ].includes(this.waterRightTypeCode);

    this._getColumn('impliedClaim').editable = !disabled;
    this._getColumn('exemptClaim').editable = !disabled;
  }

  protected _onGetSuccessHandler(data: HistoricalData) {
    this.data = data.record;
    this.displayData = this._getDisplayData(data.record);
  }

  protected _onPutSuccessHandler(data: Historical) {
    this.data = data;
    this.displayData = this._getDisplayData(data);
  }

  protected _get(): void {}

  protected _update(values: any): void {
    this.service.update(values, ...this.idArray).subscribe(
      (response: Historical) => {
        this._onPutSuccessHandler(response);
        this.snackBar.open('Record successfully updated.', null);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update record. ';
        message += errorBody.userMessage || ErrorMessageEnum.PUT;
        this.snackBar.open(message);
        this._displayEditDialog(this.data);
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

  ngOnDestroy(): void {
    this.dialog.closeAll();
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
