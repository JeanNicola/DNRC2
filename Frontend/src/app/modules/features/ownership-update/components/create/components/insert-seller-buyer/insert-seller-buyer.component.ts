import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject, ViewChild } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { ContactsService } from 'src/app/modules/features/contacts/services/contacts.service';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { OwnershipUpdateTypes } from '../../../../interfaces/ownership-update';
import { ActiveSellersService } from '../../services/active-sellers.service';
import { SellersService } from '../../services/sellers.service';

@Component({
  selector: 'app-insert-seller-buyer',
  templateUrl: './insert-seller-buyer.component.html',
  styleUrls: ['./insert-seller-buyer.component.scss'],
  providers: [ContactsService, SellersService, ActiveSellersService],
})
export class InsertSellerBuyerComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<InsertSellerBuyerComponent>,
    public service: ContactsService,
    public sellersService: SellersService,
    public activeSellersService: ActiveSellersService,
    public snackBar: SnackBarService
  ) {
    super(dialogRef);
  }

  // allow moving to second step
  @ViewChild('stepper') stepper: MatStepper;

  public searchColumns = this.data.columns.filter((item) =>
    item?.displayInSearch == null ? true : item?.displayInInsert
  );
  public mode = DataManagementDialogModes.Insert;
  public title = this.data.title;
  public displayFields = this.data.columns.filter((item) => {
    return item?.displayInInsert == null ? true : item?.displayInInsert;
  });
  public currentStep = 0;
  public checkedCustomers = {};
  public selectedCustomersCount = 0;

  // Page Options
  protected pageSizeOptions: number[] = [25, 50, 100];
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'asc',
    sortColumn: this._getColumn('name').columnId,
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public queryResult: any;
  public rows: any[];
  public dataFound = true;

  // Customer variables
  public customersForm = new FormGroup({});

  private _getColumn(columnId: string): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = this.displayFields.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }

  // Work through the stepper
  public stepping(step: StepperSelectionEvent): void {
    this.currentStep = step.selectedIndex;
    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      this.checkedCustomers = {};
      this.selectedCustomersCount = 0;
      this.stepper.steps.forEach((v, i) => {
        if (i > step.selectedIndex) {
          v.completed = false;
        }
      });
    } else {
      if (step.selectedIndex === 1) {
        this.queryResult = null;
        this.queryParameters.filters = { ...this.formGroup.value };
        this.queryParameters.pageNumber = 1;

        this.lookup();
      }
      this.stepper.selected.completed = true;
    }
  }

  public onRowDoubleClick(idx: number): void {
    if (this.currentStep === 1) {
      this.dialogRef.close([this.rows[idx]]);
    }
  }

  protected lookup(): void {
    if (this.data.values?.type === OwnershipUpdateTypes.SELLER) {
      if (this.data.values?.mode === DataManagementDialogModes.Insert) {
        const waterRightIds = this.data.values?.waterRights?.map(
          (waterRight) => {
            return waterRight.waterRightId;
          }
        );
        this.sellersService
          .getSellers(
            {
              waterRights: waterRightIds,
            },
            this.queryParameters
          )
          .subscribe((data) => {
            this.handleResponseData(data);
          }, this.handleErrorOnRequest.bind(this));
      } else {
        this.activeSellersService
          .get(this.queryParameters, this.data.values?.ownershipUpdateId)
          .subscribe((data) => {
            this.handleResponseData(data);
          }, this.handleErrorOnRequest.bind(this));
      }
    } else {
      this.service.get(this.queryParameters).subscribe((data) => {
        this.handleResponseData(data);
      }, this.handleErrorOnRequest.bind(this));
    }
  }

  public handleErrorOnRequest(err: HttpErrorResponse) {
    const errorBody = err.error as ErrorBodyInterface;
    const message = errorBody.userMessage || ErrorMessageEnum.GET;
    this.snackBar.open(message);
  }

  protected postLookup(dataIn: any): any {
    // if only one row is returned, automatically accept the row.
    if (dataIn.results.length === 1 && dataIn.currentPage === 1) {
      this.dialogRef.close([dataIn.results[0]]);
    }
    return dataIn;
  }

  public handleResponseData(data) {
    data.results = data.results.map((contact) => ({
      ...contact,
      contactId: contact.contactID || contact.contactId,
    }));
    const checkedCustomersIds = Object.keys(this.checkedCustomers).map(
      (id) => +id
    );
    const localData = this.postLookup(data);
    this.queryResult = localData;
    this.rows = localData.results.map((contact) => ({
      ...contact,
      checked: checkedCustomersIds.includes(contact.contactId),
    }));
    this.dataFound = this.rows.length > 0;
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.queryParameters.sortColumn = sort.active.toUpperCase();
      this.queryParameters.sortDirection = sort.direction.toUpperCase();
      this.lookup();
    }
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.queryParameters.pageSize = pagingOptions.pageSize;
      this.queryParameters.pageNumber = pagingOptions.pageIndex + 1;
      this.lookup();
    }
  }

  public onRowStateChangedHandler(idx) {
    const row = this.rows[idx];
    const formGroup = (this.customersForm.get('customers') as FormArray).at(
      idx
    );

    if (formGroup.get('checked').value) {
      this.checkedCustomers[row.contactId] = formGroup;
      this.selectedCustomersCount = this.selectedCustomersCount + 1;
    } else {
      delete this.checkedCustomers[row.contactId];
      this.selectedCustomersCount = this.selectedCustomersCount - 1;
    }
  }

  public save(): void {
    this.dialogRef.close(
      Object.values(this.checkedCustomers).map((formGroup: FormGroup) =>
        formGroup.getRawValue()
      )
    );
  }
}
