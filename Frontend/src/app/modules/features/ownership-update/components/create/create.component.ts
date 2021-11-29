import { StepperSelectionEvent } from '@angular/cdk/stepper';
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { MatTableDataSource } from '@angular/material/table';
import { MatTabGroup } from '@angular/material/tabs';
import { Subject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { InsertWaterRightComponent } from 'src/app/modules/shared/components/affected-water-rights/components/insert-water-right/insert-water-right.component';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { OwnershipUpdateTypes } from '../../interfaces/ownership-update';
import { OwnershipUpdateInsertInterface } from '../../interfaces/ownership-update-insert';
import { OwnershipUpdateTypeService } from '../../services/ownership-update-type.service';
import { filterOwnershipUpdateTypes } from '../../shared/filter-ownership-update-types';
import { InsertSellerBuyerComponent } from './components/insert-seller-buyer/insert-seller-buyer.component';
import { ResetSellersDialogComponent } from './components/reset-sellers-dialog/reset-sellers-dialog.component';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss'],
  providers: [OwnershipUpdateTypeService],
})
export class CreateComponent
  extends DataManagementDialogComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  // allow moving to second step
  @ViewChild('stepper') stepper: MatStepper;
  @ViewChild('tabs') tabs: MatTabGroup;
  @ViewChild('waterRightAddFocus', { static: false })
  waterRightAddButton: MatButton;
  @ViewChild('sellerAddFocus', { static: false }) sellerAddButton: MatButton;
  @ViewChild('buyerAddFocus', { static: false }) buyerAddButton: MatButton;

  constructor(
    public ownershipUpdateTypeService: OwnershipUpdateTypeService,
    public dialogRef: MatDialogRef<CreateComponent>,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(dialogRef);
  }

  public loaded = true;
  public title = 'Create Ownership Update';
  public currentStep = 0;
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public permissions = {
    canGET: false,
    canPOST: true,
    canDELETE: true,
    canPUT: false,
  };

  public selectedStepIndex = 0;
  public ownershipUpateTypeEnum = OwnershipUpdateTypes;
  public displayFields = [];

  // Subject in charge of reloading the columns

  public reloadColumnsSubject = new Subject<{
    columns: ColumnDefinitionInterface[];
    markAsDirty?;
    markAllAsTouched?;
  }>();
  public checkboxesState: any = {
    isPendingDor: false,
    isReceivedAs608: false,
  };

  // Customers Info
  public sellers = [];
  public sellersDataSource = new MatTableDataSource(this.sellers);
  public buyers = [];
  public buyersDataSource = new MatTableDataSource(this.sellers);
  get contactColumns(): ColumnDefinitionInterface[] {
    const columns = [
      {
        columnId: 'contactId',
        title: 'Contact ID',
        type: FormFieldTypeEnum.Input,
      },
      {
        columnId: 'name',
        title: 'Name',
        type: FormFieldTypeEnum.Input,
        displayInSearch: false,
      },
      {
        columnId: 'lastName',
        title: 'Last Name',
        type: FormFieldTypeEnum.Input,
        displayInTable: false,
      },
      {
        columnId: 'firstName',
        title: 'First Name',
        type: FormFieldTypeEnum.Input,
        displayInTable: false,
      },
    ];

    return [...columns.map((obj) => Object.assign({}, obj))];
  }

  // Info for the checkboxes below the title
  public ownershipUpdateCheckboxes: ColumnDefinitionInterface[] = [
    {
      columnId: 'isPendingDor',
      title: 'Pending DOR Validation',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'isReceivedAs608',
      title: 'Received as a 608',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];

  // Basic information Info
  public basicInformationFormGroup: FormGroup = new FormGroup({});

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'ownershipUpdateType',
      title: 'Ownership Update Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'ownershipUpdateTypeVal',
      title: 'Ownership Update Type',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      width: 320,
      validators: [Validators.required],
    },
    {
      columnId: 'dateReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
    },
  ];

  public displayFieldsForDataRow: ColumnDefinitionInterface[];

  // Water Right Info
  public waterRights = [];
  public waterRightsQueryResult: any;
  public waterRightsDataSource = new MatTableDataSource(this.waterRights);

  get waterRightColumns(): ColumnDefinitionInterface[] {
    const columns: ColumnDefinitionInterface[] = [
      {
        columnId: 'basin',
        title: 'Basin',
        type: FormFieldTypeEnum.Input,
      },
      {
        columnId: 'waterRightNumber',
        title: 'Water Right #',
        type: FormFieldTypeEnum.Input,
      },
      {
        columnId: 'ext',
        title: 'Ext',
        type: FormFieldTypeEnum.Input,
      },
      {
        columnId: 'typeDescription',
        title: 'Water Right Type Description',
        type: FormFieldTypeEnum.Input,
        displayInSearch: false,
      },
      {
        columnId: 'statusCode',
        title: 'WR Status',
        type: FormFieldTypeEnum.Input,
        displayInSearch: false,
        displayInTable: false,
      },
      {
        columnId: 'statusDescription',
        title: 'Water Right Status',
        type: FormFieldTypeEnum.Input,
        displayInSearch: false,
      },
    ];

    return [...columns.map((obj) => Object.assign({}, obj))];
  }

  protected initFunction(): void {
    this.displayFields = this._getDisplayFields(this.columns);
    this.displayFieldsForDataRow = this._getDisplayFieldsForDataRow(
      this.columns
    );
    this.reloadColumns$ = this.reloadColumnsSubject.asObservable();
    this.populateDropdowns();
  }

  public ngAfterViewInit(): void {
    setTimeout(() => {
      this.stepper.steps.forEach((step, toGoIndex) => {
        step.select = () => {
          this.handleCurrentStep(toGoIndex);
        };
      });
      this.basicInformationFormGroup.get('isPendingDor')?.disable();
      this.basicInformationFormGroup.get('isReceivedAs608')?.disable();
    });
  }

  public handleCurrentStep(toGoIndex: number): void {
    if (
      this.currentStep === 0 &&
      this.basicInformationFormGroup.valid &&
      this.waterRights.length
    ) {
      this.currentStep = toGoIndex;
      setTimeout(() => {
        this.selectedStepIndex = toGoIndex;
      });
    } else if (this.currentStep !== 0) {
      this.currentStep = toGoIndex;
      setTimeout(() => {
        this.selectedStepIndex = toGoIndex;
      });
    }
  }

  public ngOnDestroy(): void {
    if (this.reloadColumnsSub) {
      this.reloadColumnsSub.unsubscribe();
    }
  }

  private getRealValueFor(fieldName, value: string, columns) {
    const options = this._getColumn(fieldName, columns).selectArr.filter(
      (option) => option.value === value
    );
    if (options[0]) {
      return options[0].name;
    }
  }

  public stepping(step: StepperSelectionEvent): void {
    if (step.selectedIndex === 3) {
      // Set control and data for ownershipUpdateTypeVal
      const ownershipUpdateTypeVal = this.getRealValueFor(
        'ownershipUpdateType',
        this.basicInformationFormGroup.get('ownershipUpdateType').value,
        this.columns
      );

      this.basicInformationFormGroup.addControl(
        'ownershipUpdateTypeVal',
        new FormControl('')
      );

      // Set Ownership Update Type
      this.basicInformationFormGroup
        .get('ownershipUpdateTypeVal')
        .setValue(ownershipUpdateTypeVal);
      // Start at the Water Rights TAB
      this.tabs.selectedIndex = 0;
    }

    this.handleCurrentStep(step.selectedIndex);
  }

  private ownershipUpdateTypeHandler(ownershipUpdateType: string) {
    if (ownershipUpdateType === 'DOR 608') {
      this.basicInformationFormGroup.get('isPendingDor')?.enable();
      this.basicInformationFormGroup.get('isReceivedAs608')?.enable();
      if (!this.basicInformationFormGroup.get('isReceivedAs608').value) {
        this.basicInformationFormGroup.get('isPendingDor').setValue(true);
      }
    } else {
      this.basicInformationFormGroup.get('isPendingDor')?.disable();
      this.basicInformationFormGroup.get('isReceivedAs608')?.disable();
      this.basicInformationFormGroup.get('isPendingDor').setValue(false);
      this.basicInformationFormGroup.get('isReceivedAs608').setValue(false);
    }
  }

  protected _getColumn(
    columnId: string,
    columns: ColumnDefinitionInterface[]
  ): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = columns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }

  private _displayInsertSellerBuyerDialog(
    data: any,
    title: string,
    type: OwnershipUpdateTypes
  ): void {
    const dialogRef = this.dialog.open(InsertSellerBuyerComponent, {
      data: {
        title,
        columns: this.contactColumns,
        values: {
          ...data,
          type,
          waterRights: this.waterRights,
          mode: DataManagementDialogModes.Insert,
        },
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (type === OwnershipUpdateTypes.SELLER) {
          // Handle New Seller
          // Get Array of all the current Seller ids
          const contactIds = this.sellers.map((seller) => seller.contactId);
          const resultsToInsert = result.filter(
            (contact) => contactIds.indexOf(contact.contactId) === -1
          );
          const duplicates = result.length - resultsToInsert.length;
          if (duplicates) {
            this.snackBar.open(
              duplicates === 1
                ? `${duplicates} Duplicate seller was ignored.`
                : `${duplicates} Duplicate sellers were ignored.`,
              null,
              2000
            );
          }
          const dataToPush = resultsToInsert.map((contact) => ({
            contactId: contact.contactId,
            name: contact.name,
          }));

          this.sellers.push(...dataToPush);
          this.sellers = this.sortContacts(this.sellers);
          this.sellersDataSource.data = this.sellers;
        } else if (type === OwnershipUpdateTypes.BUYER) {
          // Handle New Buyer
          // Get Array of all the current Buyer ids
          const contactIds = this.buyers.map((buyer) => buyer.contactId);
          const resultsToInsert = result.filter(
            (contact) => contactIds.indexOf(contact.contactId) === -1
          );
          const duplicates = result.length - resultsToInsert.length;
          if (duplicates) {
            this.snackBar.open(
              duplicates === 1
                ? `${duplicates} Duplicate buyer was ignored.`
                : `${duplicates} Duplicate buyers were ignored.`,
              null,
              2000
            );
          }
          const dataToPush = resultsToInsert.map((contact) => ({
            contactId: contact.contactId,
            name: contact.name,
          }));
          this.buyers.push(...dataToPush);
          this.buyers = this.sortContacts(this.buyers);
          this.buyersDataSource.data = this.buyers;
        }
      } else {
        if (type === OwnershipUpdateTypes.SELLER) {
          this.sellerAddButton.focus();
        } else {
          this.buyerAddButton.focus();
        }
      }
    });
  }

  private _displayInsertWaterRightDialog(data: any, title: string): void {
    const dialogRef = this.dialog.open(InsertWaterRightComponent, {
      data: {
        title,
        values: data,
        columns: this.waterRightColumns,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // Handle New Water Right
        // Get Array of all the current Water Right numbers
        const waterRightIds = this.waterRights.map(
          (waterRight) => waterRight.waterRightId
        );

        const resultsToInsert = result.filter(
          (wr) => waterRightIds.indexOf(wr.waterRightId) === -1
        );

        // Check if the Water Right was already selected
        const duplicates = result.length - resultsToInsert.length;
        if (duplicates) {
          this.snackBar.open(
            duplicates === 1
              ? `${duplicates} Duplicate water right was ignored.`
              : `${duplicates} Duplicate water rights were ignored.`,
            null,
            2000
          );
        }

        this.waterRights.push(...resultsToInsert);
        this.waterRights = this.sortWaterRights(this.waterRights);
        this.waterRightsDataSource.data = this.waterRights;
      }
      this.waterRightAddButton.focus();
    });
  }

  public sortWaterRights(waterRights: any[]): any[] {
    return waterRights.sort((a, b) => {
      if (a.waterRightNumber > b.waterRightNumber) {
        return 1;
      }
      if (a.waterRightNumber < b.waterRightNumber) {
        return -1;
      }
      if (a.waterRightNumber === b.waterRightNumber) {
        if (a.basin > b.basin) {
          return 1;
        }
        if (a.basin < b.basin) {
          return -1;
        }
        return 0;
      }
    });
  }

  public sortContacts(contacts: any[]): any[] {
    return contacts.sort((a, b) => {
      if (a.name > b.name) {
        return 1;
      }
      if (a.name < b.name) {
        return -1;
      }
      return 0;
    });
  }

  public onDelete(row: number, type: OwnershipUpdateTypes): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        if (type === OwnershipUpdateTypes.SELLER) {
          this.sellers.splice(row, 1);
          this.sellersDataSource._updateChangeSubscription();
        } else if (type === OwnershipUpdateTypes.BUYER) {
          this.buyers.splice(row, 1);
          this.buyersDataSource._updateChangeSubscription();
        }
      }
    });
  }

  public onDeleteWaterRight(row: number): void {
    if (this.sellers.length) {
      const clearSellerDialog = this.dialog.open(ResetSellersDialogComponent, {
        width: '500px',
      });
      clearSellerDialog.afterClosed().subscribe((clearSellersResult) => {
        if (clearSellersResult === 'reset') {
          this.sellers = [];
          this.sellersDataSource = new MatTableDataSource(this.sellers);
          this.waterRights.splice(row, 1);
          this.waterRightsDataSource._updateChangeSubscription();
        }
      });
    } else {
      const dialogRef = this.dialog.open(DeleteDialogComponent, {
        width: '500px',
      });
      dialogRef.afterClosed().subscribe((result) => {
        if (result === 'delete') {
          this.waterRights.splice(row, 1);
          this.waterRightsDataSource._updateChangeSubscription();
        }
      });
    }
  }

  public onInsertSeller(): void {
    this._displayInsertSellerBuyerDialog(
      null,
      'Add New Seller',
      OwnershipUpdateTypes.SELLER
    );
  }

  public onInsertBuyer(): void {
    this._displayInsertSellerBuyerDialog(
      null,
      'Add New Buyer',
      OwnershipUpdateTypes.BUYER
    );
  }

  public onInsertWaterRight(): void {
    this._displayInsertWaterRightDialog(null, 'Add New Water Right');
  }

  public save(): any {
    const data = { ...this.basicInformationFormGroup.getRawValue() };
    // Format data
    data.sellerIds = this.sellers.map((seller) => seller.contactId);
    data.buyerIds = this.buyers.map((buyer) => buyer.contactId);
    data.waterRights = this.waterRights.map(
      (waterRight) => waterRight.waterRightId
    );

    // Build dto
    const dto: OwnershipUpdateInsertInterface = {
      sellers: data.sellerIds,
      buyers: data.buyerIds,
      waterRights: data.waterRights,
      ownershipUpdateType: data.ownershipUpdateType,
      receivedDate: data.dateReceived,
      pendingDORValidation: data.isPendingDor,
      receivedAs608: data.isReceivedAs608,
    };
    if (
      !data.sellerIds?.length &&
      !data.buyerIds?.length &&
      !data.waterRights?.length
    ) {
      this.snackBar.open(
        'At least one Seller, Buyer or Water Right is required.'
      );
      return;
    }
    this.dialogRef.close(dto);
  }

  public onChange(event): void {
    // Set the corresponding info for the columns whenever the type changes
    if (event?.fieldName === 'ownershipUpdateType') {
      this.setColumnsForOwnershipUpdateType(event.value);
    }
  }

  private setColumnsForOwnershipUpdateType(ownershipUpdateType: string): void {
    const columns: any = [...this.columns.map((column) => ({ ...column }))];
    if (ownershipUpdateType === 'DOR 608') {
      this._getColumn('dateReceived', columns).title = 'Sale Date';
    } else {
      this._getColumn('dateReceived', columns).title = 'Received Date';
    }
    // Synchronize checkboxes state
    Object.keys(this.checkboxesState).forEach((key) => {
      this.checkboxesState[key] = this.basicInformationFormGroup.get(key).value;
    });
    // Send event in order for the new columns to be displayed

    // columns = this._getDisplayFields(columns);
    this.displayFields = this._getDisplayFields(columns);
    this.displayFieldsForDataRow = this._getDisplayFieldsForDataRow(columns);
    this.reloadColumnsSubject.next({
      columns,
      markAllAsTouched: false,
      markAsDirty: false,
    });

    setTimeout(() => {
      this.ownershipUpdateTypeHandler(ownershipUpdateType);
    });
  }

  // We're overriding this method because we need to take care of the checkboxes too
  protected setReloadColumnsFunctionality(): void {
    if (this.reloadColumns$) {
      this.reloadColumnsSub = this.reloadColumns$.subscribe((data) => {
        // Keep track of the old form
        const oldFormControls = { ...this.basicInformationFormGroup.controls };
        // Reset fields
        this.displayFields = [];
        this.basicInformationFormGroup = new FormGroup({});
        // Re-render form
        this.displayFields = this._getDisplayFields(data.columns);
        this.ownershipUpdateCheckboxes = this.ownershipUpdateCheckboxes.map(
          (checkbox) => ({ ...checkbox })
        );
        // Restore values from old form
        setTimeout(() => {
          data.columns.forEach((column: ColumnDefinitionInterface) => {
            if (
              oldFormControls[column.columnId] &&
              this.basicInformationFormGroup.get(column.columnId)
            ) {
              this.basicInformationFormGroup
                .get(column.columnId)
                .setValue(oldFormControls[column.columnId].value);
            }
          });
          if (data.markAsDirty) {
            this.basicInformationFormGroup.markAsDirty();
          }
          if (data.markAllAsTouched) {
            this.basicInformationFormGroup.markAllAsTouched();
          }
        });
      });
    }
  }

  public populateDropdowns(): void {
    this.ownershipUpdateTypeService
      .get(this.queryParameters)
      .subscribe((ownershipUpdateTypes) => {
        this._getColumn('ownershipUpdateType', this.columns).selectArr =
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
        this._getColumn('ownershipUpdateType', this.displayFields).selectArr =
          this._getColumn('ownershipUpdateType', this.columns).selectArr;
      });
  }

  _getDisplayFields(
    columns: ColumnDefinitionInterface[]
  ): ColumnDefinitionInterface[] {
    return columns
      .filter((item) =>
        item?.displayInInsert == null ? true : item?.displayInInsert
      )
      .map((item) => ({
        ...item,
      }));
  }

  _getDisplayFieldsForDataRow(
    columns: ColumnDefinitionInterface[]
  ): ColumnDefinitionInterface[] {
    return columns
      .filter((item) =>
        item?.displayInTable == null ? true : item?.displayInTable
      )
      .map((item) => ({
        ...item,
      }));
  }

  protected setPermissions(): void {}
}
