/* eslint-disable max-len */
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, Observable, ReplaySubject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { AffectedChangeApplicationsService } from 'src/app/modules/shared/components/affected-change-applications/services/affected-change-applications.service';
import { AffectedWaterRightsService } from 'src/app/modules/shared/components/affected-water-rights/services/affected-water-rights.service';
import { ConfirmationDialogComponent } from 'src/app/modules/shared/components/dialogs/confirmation-dialog/confirmation-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { OwnershipUpdatePUTInterface } from '../../../../interfaces/ownership-update-put';
import { OwnershipUpdateTypeService } from '../../../../services/ownership-update-type.service';
import { OwnershipUpdateService } from '../../../../services/ownership-update.service';
import { TransferService } from '../../../../services/transfer.service';
import { filterOwnershipUpdateTypes } from '../../../../shared/filter-ownership-update-types';
import { FeeSummaryService } from '../dor-payments/components/fee-summary/services/fee-summary.service';
import { RecalculateFeeDueComponent } from '../dor-payments/components/recalculate-fee-due/recalculate-fee-due.component';
import { CalculateFeeDueService } from '../dor-payments/services/calculate-fee-due.service';
import { EditOwnershipUpdateDialogComponent } from './components/edit-ownership-update-dialog/edit-ownership-update-dialog.component';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './edit-header.component.scss',
  ],
  providers: [
    OwnershipUpdateTypeService,
    OwnershipUpdateService,
    TransferService,
    CalculateFeeDueService,
    FeeSummaryService,
    AffectedWaterRightsService,
    SessionStorageService,
    CurrencyPipe,
  ],
})
export class EditHeaderComponent extends DataRowComponent implements OnDestroy {
  @Input() title;
  @Input() reloadHeader: Observable<any> = null;
  public reloadHeaderSub$: Subscription;

  @Output() reloadPayments = new EventEmitter();
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();

  public error;
  public dialogWidth = '600px';
  public data;

  public permissions: PermissionsInterface = {
    canGET: false,
    canPOST: false,
    canDELETE: false,
    canPUT: true,
  };

  constructor(
    public service: OwnershipUpdateService,
    public feeSummaryService: FeeSummaryService,
    public waterRightsService: AffectedWaterRightsService,
    public applicationsService: AffectedChangeApplicationsService,
    public transferService: TransferService,
    public endpointService: EndpointsService,
    public ownershipUpdateTypeService: OwnershipUpdateTypeService,
    public calculateFeeDueService: CalculateFeeDueService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private titleService: Title,
    private currencyPipe: CurrencyPipe
  ) {
    super(service, endpointService, dialog, snackBar);
    this.titleService.setTitle(
      `WRIS - Ownership Update: ${this.route.snapshot.params.id}`
    );
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'ownershipUpdateId',
      title: 'Ownership Update ID',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
      displayInEdit: false,
      width: 160,
    },
    {
      columnId: 'ownershipUpdateType',
      title: 'Ownership Transfer Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'ownershipUpdateTypeVal',
      title: 'Ownership Transfer Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 310,
    },
    {
      columnId: 'dateReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
      width: 130,
    },
    {
      columnId: 'dateProcessed',
      title: 'Processed Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
      width: 130,
    },
    {
      columnId: 'dateTerminated',
      title: 'Terminated Date',
      type: FormFieldTypeEnum.Date,
      validators: [WRISValidators.dateBeforeToday],
      width: 130,
    },
    {
      columnId: 'isPendingDor',
      title: 'Pending \n DOR Validation',
      type: FormFieldTypeEnum.Checkbox,
      displayInEdit: false,
    },
    {
      columnId: 'isReceivedAs608',
      title: 'Received \n as a 608',
      type: FormFieldTypeEnum.Checkbox,
      displayInEdit: false,
    },
  ];

  public reportTitle = 'Ownership Updates Reports';

  public reports: ReportDefinition[] = [
    {
      title: 'Acknowledgment of Owner Update',
      reportId: 'WRD2010R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_OWNR_UPDT_ID = data.ownershipUpdateId;
      },
    },
    {
      title: 'DOR Fee Letter',
      reportId: 'WRD2015R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_OWNR_UPDT_ID = data.ownershipUpdateId;
      },
      isAvailable: (data: any) =>
        ['DOR 608', '608'].includes(data.ownershipUpdateType) &&
        !data.dateTerminated &&
        !data.dateProcessed,
    },
    {
      title: 'General Abstract',
      reportId: 'WRD2090AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_OWNR_UPDT_ID = data.ownershipUpdateId;
      },
    },
    {
      title: 'Modified Abstract For Water Court',
      reportId: 'WRD2040R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_OWNR_UPDT_ID = data.ownershipUpdateId;
      },
      isAvailable: (data: any): boolean => data.canPrintDecreeReport,
    },
    {
      title: 'Water Court Abstract',
      reportId: 'WRD2041R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_OWNR_UPDT_ID = data.ownershipUpdateId;
      },
      isAvailable: (data: any): boolean => data.canPrintDecreeReport,
    },
  ];

  public _getHelperFunction(data: any) {
    this.dataEvent.emit(data.get);

    // Set Sale Date for 'DOR OWNERSHIP UPDTAE' type, otherwise set Received Date
    const showSaleDate = data.get?.ownershipUpdateType === 'DOR 608';
    const displayData = {
      ...data.get,
      isPendingDor: data.get.pendingDor === 'Y',
      isReceivedAs608: data.get.receivedAs608 === 'Y',
    };

    if (showSaleDate) {
      this._getColumn('dateReceived').title = 'Sale Date';
    } else {
      this._getColumn('dateReceived').title = 'Received Date';
    }

    // Set corresponding fields
    this.displayedColumns = this.getDisplayedColumns(this.columns);

    // Hide the Edit button if the user doesn't have any fields to edit
    if (
      (data.get?.dateProcessed || data.get?.dateTerminated) &&
      !['DOR 608', '608'].includes(data.get?.ownershipUpdateType)
    ) {
      this.permissions.canPUT = false;
    } else {
      this.permissions.canPUT = true;
    }

    return {
      ...displayData,
    };
  }

  public initFunction() {
    this.idArray = [this.route.snapshot.params.id];
    this._get();
    if (this.reloadHeader) {
      this.reloadHeaderSub$ = this.reloadHeader.subscribe(() => {
        this._get();
      });
    }
  }

  public ngOnDestroy() {
    if (this.dialog) {
      this.dialog.closeAll();
    }
    if (this.reloadHeaderSub$) {
      this.reloadHeaderSub$.unsubscribe();
    }
  }

  public update(updatedRow, calculateFeeDue) {
    this.service.update(updatedRow, ...this.idArray).subscribe(
      () => {
        this.snackBar.open('Record successfully updated.', null);
        if (calculateFeeDue) {
          this.calculateFeeDueService
            .update({}, this.idArray[0])
            .subscribe(() => {
              this._get();
              this.reloadPayments.emit(null);
            });
        } else {
          this._get();
          this.reloadPayments.emit(null);
        }
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

  protected preUpdate(updatedRow: any): void {
    if (
      ['DOR 608', '608'].includes(updatedRow.ownershipUpdateType) &&
      !updatedRow.dateTerminated &&
      !this.data.dateProcessed
    ) {
      const recalculateFeeDueDialog = this.dialog.open(
        RecalculateFeeDueComponent,
        {
          width: '500px',
        }
      );
      recalculateFeeDueDialog.afterClosed().subscribe((result) => {
        this.update(updatedRow, result === 'yes');
      });
    } else {
      this.update(updatedRow, false);
    }
  }

  public onProcessTransferHandler(): void {
    // Get the data necessary to process the ownership transfer
    forkJoin([
      this.feeSummaryService.get(this.queryParameters, this.idArray[0]),
      this.waterRightsService.get(this.queryParameters, this.idArray[0]),
      this.applicationsService.get(this.queryParameters, this.idArray[0]),
    ]).subscribe({
      next: ([feeSummary, waterRightsData, apps]) => {
        this._processTransfer({
          feeSummary,
          waterRights: waterRightsData.results,
          apps: apps.results,
        });
      },
      error: (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message =
          'System error occurred. Cannot process Ownership Update.\n';
        message += errorBody.userMessage || '';
        this.snackBar.open(message);
      },
    });
  }
  private _processTransfer(
    data,
    overpaymentChecked = false,
    otherOwnerUpdateChecked = false,
    appsIncChecked = false
  ) {
    // Ensure either a water right or an application exists on the ownership update before continuing
    // This should already be handled by the "Transfer" button enable/disable.
    if (!data.apps.length && !data.waterRights.length) {
      this.snackBar.open(
        'A Water Right or Change Application must exist before a Transfer can be processed.'
      );
      return;
    }

    // Ensure 643 only has one water right
    if (
      this.data.ownershipUpdateType === '643 608' &&
      data.waterrights.length > 1
    ) {
      this.snackBar.open(
        'This 643 update has more than one Water Right. Only ONE Water Right is allowed to be processed by the 643 OWNERSHIP UPDATE.'
      );
      return;
    }

    // Ensure all water rights on 641 or 642 are the same program
    if (
      this.data.ownershipUpdateType === '641 608' ||
      this.data.ownershipUpdateType === '642 608'
    ) {
      // Count the different type codes
      const map = data.waterRights.reduce(
        (acc, e) => acc.set(e.typeCode, (acc.get(e.typeCode) || 0) + 1),
        new Map()
      );

      if ([...map.keys()].length > 1) {
        this.snackBar.open(
          'This 641/642 update has water rights from more than one program. Only water rights from one program can be processed by the 641/642 OWNERSHIP UPDATE.'
        );
        return;
      }
    }

    if (this.data.parentCount > 1) {
      this.snackBar.open(
        'More than one water right in the list has a split version. To run this process there can be only one water right with a split version.'
      );
      return;
    }

    if (
      this.data.parentCount === 1 &&
      this.data.ownershipUpdateType !== '642 608'
    ) {
      this.snackBar.open(
        'No split version on Parent. To run this process Parent needs a split version.'
      );
      return;
    }

    if (
      this.data.childCount === 1 &&
      this.data.ownershipUpdateType === '641 608'
    ) {
      this.snackBar.open(
        'No child split rights exists. To run this process there must be at least one water right without a split version.'
      );
      return;
    }

    if (this.data.allAppsInc === false && !appsIncChecked) {
      const confirmationDialog = this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: 'Warning',
          message:
            'Not all applications are included in this Update, do you want to continue?',
          confirmButtonName: 'Continue',
        },
      });

      confirmationDialog.afterClosed().subscribe((confirmation) => {
        if (confirmation === 'confirmed') {
          this._processTransfer(
            data,
            overpaymentChecked,
            otherOwnerUpdateChecked,
            true
          );
        }
      });

      return;
    } else if (!appsIncChecked) {
      this._processTransfer(
        data,
        overpaymentChecked,
        otherOwnerUpdateChecked,
        true
      );
      return;
    }

    // Check if a fee is currently due
    if (data.feeSummary.totalDue > 0) {
      this.snackBar.open('A FEE IS DUE! Cannot process this DOR 608 or 608.');
      return;
    }

    // If an overpayment exists, alert the user
    if (data.feeSummary.totalDue < 0 && !overpaymentChecked) {
      const confirmationDialog = this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: 'Overpayment Exists',
          message: `Overpayment of ${this.currencyPipe.transform(
            data.feeSummary.totalDue * -1
          )} exists. A REFUND IS DUE! Do you wish to continue to process this Ownership Update?`,
          confirmButtonName: 'Continue',
        },
      });

      confirmationDialog.afterClosed().subscribe((confirmation) => {
        if (confirmation === 'confirmed') {
          this._processTransfer(
            data,
            true,
            otherOwnerUpdateChecked,
            appsIncChecked
          );
        }
      });
      return;
    } else if (!overpaymentChecked) {
      this._processTransfer(
        data,
        true,
        otherOwnerUpdateChecked,
        appsIncChecked
      );
      return;
    }

    if (this.data.otherOwnerUpdateId && !otherOwnerUpdateChecked) {
      const confirmationDialog = this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: 'Warning',
          message:
            'WRs in this Update are in other Updates Processed after this ownership received date.',
          confirmButtonName: 'Continue',
        },
      });

      confirmationDialog.afterClosed().subscribe((confirmation) => {
        if (confirmation === 'confirmed') {
          this._processTransfer(data, overpaymentChecked, true, appsIncChecked);
        }
      });
      return;
    } else if (!otherOwnerUpdateChecked) {
      this._processTransfer(data, overpaymentChecked, true, appsIncChecked);
      return;
    }

    // Send the process request ot the backend
    // this.dataEvent.emit({ ...this.data, dateProcessed: Date.now() });
    this.transferService.insert({}, this.idArray[0]).subscribe(
      () => {
        this._get();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        const message = errorBody.userMessage || ErrorMessageEnum.GET;
        this.snackBar.open(message);
      }
    );
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  public _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(EditOwnershipUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const dto: OwnershipUpdatePUTInterface = {
          ownershipUpdateId: this.route.snapshot.params.id,
          ownershipUpdateType: result.ownershipUpdateType,
          dateReceived: result?.dateReceived,
          dateTerminated: result?.dateTerminated,
          pendingDor: result?.isPendingDor ? 'Y' : 'N',
          receivedAs608: result?.isReceivedAs608 ? 'Y' : 'N',
        };

        this.preUpdate(dto);
      }
    });
  }

  public getDisplayedColumns(columns: ColumnDefinitionInterface[]) {
    return columns
      .filter((item) =>
        item?.displayInTable == null ? true : item?.displayInTable
      )
      .map((item) => item);
  }

  public populateDropdowns(): void {
    this.observables.ownershipUpdateType = new ReplaySubject(1);

    this.ownershipUpdateTypeService
      .get(this.queryParameters)
      .subscribe((ownershipUpdateTypes) => {
        this._getColumn('ownershipUpdateType').selectArr =
          ownershipUpdateTypes.results
            .filter(
              (ownershipUpdateType: { value: string; description: string }) =>
                filterOwnershipUpdateTypes(
                  ownershipUpdateType.value,
                  DataManagementDialogModes.Update
                )
            )
            .map(
              (ownershipUpdateType: {
                value: string;
                description: string;
              }) => ({
                name: ownershipUpdateType.description,
                value: ownershipUpdateType.value,
              })
            );

        this.observables.ownershipUpdateType.next(ownershipUpdateTypes);
        this.observables.ownershipUpdateType.complete();
      });
  }

  public _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Ownership Update not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }

  protected setPermissions() {}
}
