/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-return */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ReplaySubject, Subject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import {
  ButtonPositions,
  RowButtonDefinition,
} from 'src/app/modules/shared/interfaces/row-button-interface';
import { FeeStatusService } from '../../services/fee-status.service';
import { PaymentsService } from '../../services/payments.service';
import { FeeSummaryUpdateDialogComponent } from '../fee-summary-update-dialog/fee-summary-update-dialog.component';

export interface FeeStatusType {
  value: string;
  description: string;
}

@Component({
  selector: 'app-fee-summary',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
})
export class FeeSummaryComponent extends DataRowComponent implements OnChanges {
  @Input() paymentsData;

  @Output() paymentsSummaryUpdate = new EventEmitter();
  @Output() reloadEvents = new EventEmitter();

  public showLoading = false;
  public title = 'Filing Fee Summary';
  public containerStyles = {
    padding: '16px',
    marginTop: '20px',
    background: 'rgb(250, 250, 250)',
    boxShadow: '0px 1px 3px 0px rgb(0 0 0 / 10%)',
    borderRadius: '4px',
    justifyContent: 'center',
  };
  public titleStyles = {
    marginBottom: '20px',
    fontSize: '16px',
  };

  public rowButtons: RowButtonDefinition[] = [
    {
      title: 'Auto Complete',
      tooltip: 'Auto Complete',
      position: ButtonPositions.TOP_RIGHT_CORNER,
      onClick: () => {
        const applicationId = this.route.snapshot.params.id;
        this.service.autoComplete(applicationId).subscribe({
          next: (data) => {
            if (data?.waterRightId) {
              void this.router.navigate([
                'wris',
                'water-rights',
                data.waterRightId,
              ]);
            }
          },
          error: (err: HttpErrorResponse) => {
            const errorBody = err.error;
            let message = 'Cannot auto complete. ';
            message += errorBody.userMessage || ErrorMessageEnum.PUT;
            this.snackBar.open(message);
          },
        });
      },
    },
  ];

  public feeStatusValue$ = new Subject<string>();
  public feeWaivedValue$ = new Subject<string>();
  public feeDiscountValue$ = new Subject<string>();
  public feeOtherValue$ = new Subject<string>();
  public feeCGWAValue$ = new Subject<string>();

  constructor(
    public service: PaymentsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public feeStatusService: FeeStatusService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'feeStatus',
      title: 'Fee Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      displayInEdit: false,
      editable: false,
      valueObservable$: this.feeStatusValue$,
    },
    {
      columnId: 'feeStatusDescription',
      title: 'Fee Status',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 100,
    },
    {
      columnId: 'feeDue',
      title: 'Fee Due',
      displayInEdit: false,
      editable: false,
      type: FormFieldTypeEnum.Currency,
      width: 100,
    },
    {
      columnId: 'amountPaid',
      title: 'Amount Paid',
      editable: false,
      displayInEdit: false,
      type: FormFieldTypeEnum.Currency,
      width: 100,
    },
    {
      columnId: 'totalDue',
      title: 'Total Due',
      editable: false,
      displayInEdit: false,
      type: FormFieldTypeEnum.Currency,
      width: 100,
    },
    {
      columnId: 'feeWaived',
      title: 'Fee Waived',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      valueObservable$: this.feeWaivedValue$,
    },
    {
      columnId: 'feeWaivedDescription',
      title: 'Fee Waived',
      type: FormFieldTypeEnum.Input,
      width: 80,
      displayInEdit: false,
    },
    {
      columnId: 'feeWaivedReason',
      title: 'Fee Waived Reason',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'feeDiscount',
      title: 'Fee Discount',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      valueObservable$: this.feeDiscountValue$,
    },
    {
      columnId: 'feeDiscountDescription',
      title: 'Fee Discount',
      type: FormFieldTypeEnum.Input,
      width: 90,
      displayInEdit: false,
    },
    {
      columnId: 'feeOther',
      title: 'Fee Other',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      valueObservable$: this.feeOtherValue$,
    },
    {
      columnId: 'feeOtherDescription',
      title: 'Fee Other',
      type: FormFieldTypeEnum.Input,
      width: 80,
      displayInEdit: false,
    },
    {
      columnId: 'feeCGWA',
      title: 'Fee CGWA',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      valueObservable$: this.feeCGWAValue$,
    },
    {
      columnId: 'feeCGWADescription',
      title: 'Fee CGWA',
      type: FormFieldTypeEnum.Input,
      width: 80,
      displayInEdit: false,
    },
  ];

  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  protected initFunction(): void {
    this.idArray = [this.route.snapshot.params.id];
  }

  // The data used here is actually passed in from the parent component so no get() processing occurs
  public ngOnChanges(changes: SimpleChanges): void {
    // Save the current paging/sorting parameters and send them back so details stay current
    this.queryParameters.sortColumn =
      changes?.paymentsData?.currentValue?.sortColumn || '';
    this.queryParameters.sortDirection =
      changes?.paymentsData?.currentValue?.sortDirection || '';
    this.queryParameters.pageSize =
      changes?.paymentsData?.currentValue?.pageSize || 25;
    this.queryParameters.pageNumber =
      changes?.paymentsData?.currentValue?.pageNumber || 1;
    this.queryParameters.filters =
      changes?.paymentsData?.currentValue?.filters || {};

    if (changes?.paymentsData?.currentValue?.results?.summary) {
      this.data = changes.paymentsData.currentValue.results.summary;
      this.displayData = changes.paymentsData.currentValue.results.summary;

      // disable and enable different fee modifiers
      if (this.data?.appFeeDiscount > 0) {
        this._getColumn('feeDiscount').displayInEdit = true;
        this._getColumn('feeDiscountDescription').displayInTable = true;
      } else {
        this._getColumn('feeDiscount').displayInEdit = false;
        this._getColumn('feeDiscountDescription').displayInTable = false;
      }

      if (this.data?.appFeeOther > 0) {
        this._getColumn('feeOther').displayInEdit = true;
        this._getColumn('feeOtherDescription').displayInTable = true;
      } else {
        this._getColumn('feeOther').displayInEdit = false;
        this._getColumn('feeOtherDescription').displayInTable = false;
      }

      if (this.data?.appFeeCGWA > 0) {
        this._getColumn('feeCGWA').displayInEdit = true;
        this._getColumn('feeCGWADescription').displayInTable = true;
      } else {
        this._getColumn('feeCGWA').displayInEdit = false;
        this._getColumn('feeCGWADescription').displayInTable = false;
      }

      // Initially set the autocomplete button status
      // - this is the "negated" value of the canAutoComplete as we're setting a disabled flag
      //   e.g., TRUE canAutocomplete = FALSE disabled
      this.rowButtons[0].disabled = !this.data.canAutoComplete;
    }
  }

  public populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the getPrograms http request and the selectArr
    // population only happens once
    this.observables.feeStatus = new ReplaySubject(1);
    this.feeStatusService
      .getAll()
      .subscribe((feeStatusTypes: { results: FeeStatusType[] }) => {
        this._getColumn('feeStatus').selectArr = feeStatusTypes.results.map(
          (type: FeeStatusType) => ({
            name: type.description,
            value: type.value,
          })
        );

        this.observables.feeStatus.next(feeStatusTypes);
        this.observables.feeStatus.complete();
      });

    this.feeStatusService.getYesAndNo().subscribe((options) => {
      const selectArr = options.results.map((type: FeeStatusType) => ({
        name: type.description,
        value: type.value,
      }));

      this._getColumn('feeCGWA').selectArr = selectArr;
      this._getColumn('feeDiscount').selectArr = selectArr;
      this._getColumn('feeOther').selectArr = selectArr;
      this._getColumn('feeWaived').selectArr = selectArr;
    });
  }

  protected _displayEditDialog(data: any): void {
    const dialogRef = this.dialog.open(FeeSummaryUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Payments Summary Record',
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.submitPayment(result);
      }
    });
  }

  private submitPayment(payment: any): void {
    const updatedRow = {
      ...payment,
      feeStatus: '',
      feeDue: 0,
      amountPaid: 0,
      totalDue: 0,
    };
    this._update(updatedRow);
  }

  // Defer to @Input() paymentsData
  protected _get(): void {}

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    this.service.update(updatedRow, ...this.idArray).subscribe(
      () => {
        this.paymentsSummaryUpdate.emit(this.queryParameters);
        if (updatedRow.feeWaived === 'Y') {
          this.reloadEvents.next();
        }
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
}
