import { HttpErrorResponse } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { CalculateFeeDueService } from '../../services/calculate-fee-due.service';
import { RecalculateFeeDueComponent } from '../recalculate-fee-due/recalculate-fee-due.component';
import { FeeSummaryService } from './services/fee-summary.service';

@Component({
  selector: 'app-fee-summary',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    './fee-summary.component.scss',
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [FeeSummaryService, CalculateFeeDueService],
})
export class FeeSummaryComponent
  extends DataRowComponent
  implements AfterViewInit, OnDestroy
{
  @Output() totalDue = new EventEmitter();
  @Output() feeDueUpdated = new EventEmitter();

  constructor(
    public service: FeeSummaryService,
    public calculateFeeDueService: CalculateFeeDueService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() reloadPaymentsData;
  @Input() showRecalculateDialog = false;

  @Input() set refreshFeeSummaryOnChange(value) {
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  public reloadPaymentsDataSub$: Subscription;

  public ngAfterViewInit() {
    setTimeout(() => {
      if (this.reloadPaymentsData) {
        this.reloadPaymentsDataSub$ = this.reloadPaymentsData.subscribe(() => {
          this._get();
        });
      }
    });
  }

  public ngOnDestroy() {
    if (this.reloadPaymentsDataSub$) {
      this.reloadPaymentsDataSub$.unsubscribe();
    }
  }

  protected _getHelperFunction(data: any): { [key: string]: any } {
    this.totalDue.emit(data.get.totalDue);
    return { ...data.get };
  }

  public showLoading = false;
  public title = 'Fee Summary';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'feeStatus',
      title: 'Fee Status',
      type: FormFieldTypeEnum.Select,
      displayInEdit: false,
      editable: false,
    },
    {
      columnId: 'feeDue',
      title: 'Fee Due',
      type: FormFieldTypeEnum.Currency,
      validators: [WRISValidators.currency(), Validators.max(9999.99)],
    },
    {
      columnId: 'amountPaid',
      title: 'Amount Paid',
      type: FormFieldTypeEnum.Currency,
      displayInEdit: false,
      editable: false,
    },
    {
      columnId: 'totalDue',
      title: 'Total Due',
      type: FormFieldTypeEnum.Currency,
      displayInEdit: false,
      editable: false,
    },
  ];

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0),
    };
  }

  public handleUpdateSuccess() {
    this.snackBar.open('Record successfully updated.', null);
    this.feeDueUpdated.emit(null);
    this._get();
  }

  public handleErrorOnUpdate(err: HttpErrorResponse) {
    const errorBody = err.error;
    let message = 'Cannot update record. ';
    message += errorBody.userMessage || ErrorMessageEnum.PUT;
    this.snackBar.open(message);

    // Redisplay the dialog with the input data
    this._displayEditDialog(this.data);
  }

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    if (this.showRecalculateDialog) {
      const recalculateFeeDueDialog = this.dialog.open(
        RecalculateFeeDueComponent,
        {
          width: '500px',
        }
      );
      recalculateFeeDueDialog.afterClosed().subscribe((result) => {
        if (result === 'yes') {
          this.calculateFeeDueService.update({}, this.idArray[0]).subscribe({
            next: this.handleUpdateSuccess.bind(this),
            error: this.handleErrorOnUpdate.bind(this),
          });
        } else {
          this.service.update(updatedRow, ...this.idArray).subscribe({
            next: this.handleUpdateSuccess.bind(this),
            error: this.handleErrorOnUpdate.bind(this),
          });
        }
      });
    } else {
      this.service.update(updatedRow, ...this.idArray).subscribe({
        next: this.handleUpdateSuccess.bind(this),
        error: this.handleErrorOnUpdate.bind(this),
      });
    }
  }
}
