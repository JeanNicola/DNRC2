import {
  AfterViewInit,
  Component,
  Inject,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';

import { ReplaySubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { SearchDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/search-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { SearchTypes } from '../../../../interfaces/ownership-update';

import { OwnershipUpdateTypeService } from '../../../../services/ownership-update-type.service';

@Component({
  selector: 'app-ownershipupdate-search-dialog',
  templateUrl: './ownershipupdate-search-dialog.component.html',
  styleUrls: ['./ownershipupdate-search-dialog.component.scss'],
  providers: [OwnershipUpdateTypeService],
})
export class OwnershipupdateSearchDialogComponent
  extends SearchDialogComponent
  implements AfterViewInit, OnDestroy
{
  private unsubscribe = new Subject();

  constructor(
    public dialogRef: MatDialogRef<OwnershipupdateSearchDialogComponent>,
    public ownershipUpdateTypeService: OwnershipUpdateTypeService,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {
    super(dialogRef, data);
  }

  @ViewChild('tabs') tabs: MatTabGroup;

  public currentSearchType = SearchTypes.OWNERSHIPUPDATE;
  public observables: { [key: string]: ReplaySubject<unknown> } = {};
  public mainColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'ownershipUpdateId',
      title: 'Ownership Update ID',
      type: FormFieldTypeEnum.Input,
    },

    {
      columnId: 'dateReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'ownershipUpdateType',
      title: 'Ownership Update Type',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'dateProcessed',
      title: 'Processed Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dateTerminated',
      title: 'Terminated Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  public sellerColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'sellerContactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'sellerLastName',
      title: 'Last Name/Corporation',
      type: FormFieldTypeEnum.Input,
      searchValidators: [
        WRISValidators.requireOtherFieldIfNonNull('sellerFirstName'),
      ],
    },
    {
      columnId: 'sellerFirstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      searchValidators: [
        WRISValidators.updateValidityOfOtherField('sellerLastName'),
      ],
    },
  ];

  public buyerColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'buyerContactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'buyerLastName',
      title: 'Last Name/Corporation',
      type: FormFieldTypeEnum.Input,
      searchValidators: [
        WRISValidators.requireOtherFieldIfNonNull('buyerFirstName'),
      ],
    },
    {
      columnId: 'buyerFirstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      searchValidators: [
        WRISValidators.updateValidityOfOtherField('buyerLastName'),
      ],
    },
  ];

  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public initFunction(): void {
    this.populateDropdowns();
  }

  public ngAfterViewInit(): void {
    this.tabs.selectedTabChange
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        if (event.index === 0) {
          this.currentSearchType = SearchTypes.OWNERSHIPUPDATE;
        }
        if (event.index === 1) {
          this.currentSearchType = SearchTypes.SELLER;
        }
        if (event.index === 2) {
          this.currentSearchType = SearchTypes.BUYER;
        }
        if (event.index === 1 || event.index === 2) {
          this.formGroup.get('ownershipUpdateId').patchValue(null);
          this.formGroup.get('waterRightNumber').patchValue(null);
          this.formGroup.get('ownershipUpdateType').patchValue(null);

          this.formGroup.get('dateReceived').patchValue(null);
          this.formGroup.get('dateProcessed').patchValue(null);
          this.formGroup.get('dateTerminated').patchValue(null);
        }

        if (event.index === 0 || event.index === 2) {
          this.formGroup.get('sellerContactId').patchValue(null);
          this.formGroup.get('sellerLastName').patchValue(null);
          this.formGroup.get('sellerFirstName').patchValue(null);
        }

        if (event.index === 0 || event.index === 1) {
          this.formGroup.get('buyerContactId').patchValue(null);
          this.formGroup.get('buyerLastName').patchValue(null);
          this.formGroup.get('buyerFirstName').patchValue(null);
        }
      });
  }

  private getColumn(
    columnId: string,
    columns: ColumnDefinitionInterface[] = this.mainColumns
  ): ColumnDefinitionInterface {
    let index: number;
    for (let i = 0; i < columns.length; i++) {
      if (columns[i].columnId === columnId) {
        index = i;
      }
    }
    return columns[index];
  }

  public populateDropdowns(): void {
    this.observables.ownershipUpdateType = new ReplaySubject(1);

    this.ownershipUpdateTypeService
      .get(this.queryParameters)
      .subscribe((ownershipUpdateTypes) => {
        ownershipUpdateTypes.results.unshift({
          value: null,
          description: null,
        });
        this.getColumn('ownershipUpdateType').selectArr =
          ownershipUpdateTypes.results.map(
            (ownershipUpdateType: { value: string; description: string }) => ({
              name: ownershipUpdateType.description,
              value: ownershipUpdateType.value,
            })
          );
        this.observables.ownershipUpdateType.next(ownershipUpdateTypes);
        this.observables.ownershipUpdateType.complete();
      });
  }

  public save(): void {
    const filters = {
      ...this.formGroup.getRawValue(),
      searchType: this.currentSearchType,
    };

    const dateReceived = this.formGroup
      .getRawValue()
      ?.dateReceived?.format('YYYY-MM-DD');
    const dateProcessed = this.formGroup
      .getRawValue()
      ?.dateProcessed?.format('YYYY-MM-DD');
    const dateTerminated = this.formGroup
      .getRawValue()
      ?.dateTerminated?.format('YYYY-MM-DD');

    if (dateReceived) {
      filters.dateReceived = dateReceived;
    }
    if (dateProcessed) {
      filters.dateProcessed = dateProcessed;
    }
    if (dateTerminated) {
      filters.dateTerminated = dateTerminated;
    }

    this.dialogRef.close(filters);
  }
}
