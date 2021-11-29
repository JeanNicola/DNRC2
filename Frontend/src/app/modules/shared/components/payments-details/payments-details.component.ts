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
import { ActivatedRoute } from '@angular/router';
import * as moment from 'moment';
import { ReplaySubject, Subject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { PermissionsInterface } from '../../interfaces/permissions.interface';
import { BaseDataService } from '../../services/base-data.service';
import { PaymentsDialogComponent } from './components/payments-dialog/payments-dialog.component';
import { PaymentOriginsService } from './services/payment-origins.service';

@Component({
  selector: 'app-payments-details',
  templateUrl: '../templates/code-table/code-table.template.html',
  styleUrls: ['../templates/code-table/code-table.template.scss'],
})
export class PaymentsDetailsComponent
  extends BaseCodeTableComponent
  implements OnChanges
{
  constructor(
    public originsService: PaymentOriginsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(originsService, endpointService, dialog, snackBar);
  }

  @Input() hideDelete = false;
  @Input() paymentsData: DataPageInterface<any>;
  @Input() amountPaid: any;
  @Input() datePaid: any;
  @Input() service: BaseDataService;
  @Input() permissions: PermissionsInterface = {
    canGET: false,
    canPOST: false,
    canDELETE: false,
    canPUT: false,
  };

  @Output() paymentChanged = new EventEmitter<DataQueryParametersInterface>();

  public originValue$ = new Subject();
  public title = 'Payment Details';
  public sortDirection = 'asc';
  public primarySortColumn = 'trackingNumber';
  protected searchable = false;
  public summary;
  public currentMode = DataManagementDialogModes.Insert;
  public containerStyles = {
    width: '100%',
    marginTop: '20px',
    background: 'rgb(250, 250, 250)',
  };
  public titleStyles = {
    fontSize: '16px',
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'paymentId',
      title: 'Payment ID',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
      displayInSearch: false,
      displayInEdit: false,
    },
    {
      columnId: 'trackingNumber',
      title: 'Payment Tracking #',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
    },
    {
      columnId: 'originDescription',
      title: 'Payment Origin',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      valueObservable$: this.originValue$,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'origin',
      title: 'Payment Origin',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      valueObservable$: this.originValue$,
      displayInTable: false,
    },
    {
      columnId: 'datePaid',
      title: 'Date Paid',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
    },
    {
      columnId: 'amountPaid',
      title: 'Amount Paid',
      type: FormFieldTypeEnum.Currency,
      validators: [
        Validators.required,
        WRISValidators.currency(),
        Validators.max(9999999.99),
      ],
    },
  ];

  public ngOnChanges(changes: SimpleChanges): void {
    if (changes.paymentsData?.currentValue?.results.details?.length) {
      // render rows
      this.data = {
        ...changes.paymentsData.currentValue,
        results: changes.paymentsData.currentValue.results.details,
      };
      this.rows = changes.paymentsData.currentValue.results.details || [];
    } else if (changes.paymentsData) {
      // no data found
      this.data = null;
      this.rows = null;
      this.dataMessage = 'No data found';
    }
  }

  protected initFunction(): void {
    this.idArray = [this.route.snapshot.params.id];
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  // Get payment origin values
  protected populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the http request and the selectArr
    // population only happens once
    this.observables.paymentOrigins = new ReplaySubject(1);
    // Get the list of payment origins
    this.originsService
      .get(this.queryParameters)
      .subscribe((data: { results: any[] }) => {
        this._getColumn('origin').selectArr = data.results.map(
          (origin: any) => ({
            name: origin.description,
            value: origin.value,
          })
        );
        this.observables.paymentOrigins.next(data);
        this.observables.paymentOrigins.complete();
      });
  }

  protected _displayInsertDialog(data: any): void {
    // Set the initial value based on the totalDue
    let amountPaid: number;
    let datePaid: moment.Moment;
    if (!data) {
      // Default the amount paid and the date paid

      amountPaid = Math.abs(this.amountPaid || 0);
      datePaid = moment(this.datePaid || null);
    }

    // data = data ? { ...data, amountPaid } : { amountPaid };
    data = { ...data, amountPaid, datePaid };

    // Add a validator to the paid date
    this.displayDialog(data, 'Payment');
  }

  protected _displayEditDialog(data: any): void {
    this.displayDialog(data, 'Payment');
  }

  private displayDialog(data: any, title: string): void {
    const dialogRef = this.dialog.open(PaymentsDialogComponent, {
      width: this.dialogWidth,
      data: {
        mode: this.currentMode,
        title,
        columns: this.columns,
        values: {
          ...data,
          type: this.service.url.includes('ownership-updates')
            ? 'ownershipUpdate'
            : 'application',
        },
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result && this.currentMode === DataManagementDialogModes.Insert) {
        this._insert(result);
      }
      if (result && this.currentMode === DataManagementDialogModes.Update) {
        this._update({ ...result, paymentId: data.paymentId });
      }
    });
  }

  // Override the basic _get and callthe parent to refresh the data
  protected _get(): void {
    this.paymentChanged.emit(this.queryParameters);
  }

  public onInsert(): void {
    this.currentMode = DataManagementDialogModes.Insert;
    this._displayInsertDialog(null);
  }

  public onEdit(data: any): void {
    this.currentMode = DataManagementDialogModes.Update;
    this._displayEditDialog(data);
  }

  protected _delete(rowIndex: number): void {
    if (this.rows && this.rows[rowIndex]?.paymentId) {
      this.service
        .delete(...this.idArray, this.rows[rowIndex].paymentId)
        .subscribe(
          () => {
            this.paymentChanged.emit(this.queryParameters);
            this.snackBar.open('Record successfully deleted.');
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error;
            let message = 'Cannot delete record. ';
            message += errorBody.userMessage || ErrorMessageEnum.DELETE;
            this.snackBar.open(message);
          }
        );
    }
  }

  protected _update(updatedRow): void {
    this.service
      .update(updatedRow, ...this.idArray, updatedRow.paymentId)
      .subscribe(
        () => {
          this.paymentChanged.emit(this.queryParameters);
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

  protected setPermissions(): void {}
}
