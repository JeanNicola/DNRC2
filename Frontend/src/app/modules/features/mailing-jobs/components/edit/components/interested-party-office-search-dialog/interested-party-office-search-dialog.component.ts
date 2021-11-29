import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { OfficeContactService } from '../../../../services/office-contact.service';

@Component({
  selector: 'app-interested-party-office-search-dialog',
  templateUrl: './interested-party-office-search-dialog.component.html',
  styleUrls: ['./interested-party-office-search-dialog.component.scss'],
  providers: [OfficeContactService],
})
export class InterestedPartyOfficeSearchDialogComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      officeSelectArr: SelectionInterface[];
      responsibleOfficeId: number;
      idArray: string[];
    },
    public dialogRef: MatDialogRef<InterestedPartyOfficeSearchDialogComponent>,
    public service: OfficeContactService,
    public snackBar: SnackBarService
  ) {
    super(dialogRef);
  }

  public includeAll = false;
  public exceptions = [];
  public rows;
  public partiesForm = new FormGroup({});
  public searchForm = new FormGroup({});
  public idArray: string[];
  public title = 'Add Interested Parties By Office';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'firstLastName',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'contactTypeDescription',
      title: 'Type',
      type: FormFieldTypeEnum.Input,
    },
  ];
  public displayFields = this.columns;
  public searchMode = DataManagementDialogModes.Search;
  public officeField: ColumnDefinitionInterface = {
    columnId: 'officeId',
    title: 'Office',
    type: FormFieldTypeEnum.Select,
    selectArr: this.data.officeSelectArr,
    formWidth: 300,
  };

  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'asc',
    sortColumn: 'firstLastName',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public dataMessage = null;
  public queryResult: any = null;

  protected initFunction(): void {
    const regionalOfficeIds = this.data.officeSelectArr.map(
      (office) => office.value
    );
    const id = this.data.responsibleOfficeId;
    if (id && regionalOfficeIds.includes(id)) {
      this.idArray = [...this.data.idArray, String(id)];
      this.lookup();
    }
  }

  protected lookup(): void {
    this.service.get(this.queryParameters, ...this.idArray).subscribe(
      (data: DataPageInterface<any>) => {
        this.queryResult = data;
        this.rows = data.results.map((row: any) => {
          const hasException = this.exceptions.includes(row.contactId);
          row.checked = hasException != this.includeAll;
          return row;
        });

        if (data.results.length) {
          this.dataMessage = null;
        } else {
          this.dataMessage = 'No data found';
        }
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        const message = errorBody.userMessage || ErrorMessageEnum.GET;
        this.snackBar.open(message);
      },
      () => {
        this.dataMessage = 'No data found';
      }
    );
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

  public onRowDoubleClick(idx: number): void {
    this.dialogRef.close({
      officeId: this.searchForm.get('officeId').value,
      contactIds: [this.rows[idx].contactId],
      includeAll: this.includeAll,
    });
  }

  public onRowStateChangedHandler(idx): void {
    const row = this.rows[idx];
    const index = this.exceptions.indexOf(row.contactId);
    if (index >= 0) {
      this.exceptions.splice(index);
    } else {
      this.exceptions.push(row.contactId);
    }
  }

  public include(): void {
    this.exceptions = [];
    this.includeAll = true;
    this.checkOrUncheckAll(true);
  }

  public exclude(): void {
    this.exceptions = [];
    this.includeAll = false;
    this.checkOrUncheckAll(false);
  }

  protected checkOrUncheckAll(check: boolean) {
    if (this.partiesForm.get('rows')) {
      (this.partiesForm.get('rows') as FormArray).controls.forEach(
        (control: FormGroup) => {
          control.get('checked').setValue(check);
        }
      );
    }
  }

  public search(): void {
    this.idArray = [
      ...this.data.idArray,
      this.searchForm.get('officeId').value,
    ];
    // clear everything
    this.includeAll = false;
    this.checkOrUncheckAll(false);

    this.lookup();
  }

  public save(): void {
    this.dialogRef.close({
      officeId: this.searchForm.get('officeId').value,
      contactIds: this.exceptions,
      includeAll: this.includeAll,
    });
  }
}
