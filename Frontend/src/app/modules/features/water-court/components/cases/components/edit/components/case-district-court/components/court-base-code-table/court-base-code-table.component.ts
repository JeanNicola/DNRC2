import { Component, EventEmitter, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, ReplaySubject, Subject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { DistrictCourtCasesService } from './services/district-court-cases.service';
import { DistrictCourtCountiesService } from './services/district-court-counties.service';
import { DistrictCourtStaffService } from './services/district-court-staff.service';

@Component({
  selector: 'app-court-base-code-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    DistrictCourtCasesService,
    DistrictCourtStaffService,
    DistrictCourtCountiesService,
  ],
})
export class CourtBaseCodeTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: DistrictCourtCasesService,
    public districtCourtStaffService: DistrictCourtStaffService,
    public districtCourtCountiesService: DistrictCourtCountiesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() onCourtSelect = new EventEmitter<any>();

  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dialogWidth = '500px';
  public isInMain = false;
  public searchable = false;
  public title = 'Court';
  public primarySortColumn = 'causeNumber';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'causeNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'causeNumber',
      title: 'District Court Cause #',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(30)],
    },
    {
      columnId: 'districtCourtNumber',
      title: 'District Court #',
      type: FormFieldTypeEnum.Input,
      validators: [
        WRISValidators.integer,
        Validators.min(1),
        Validators.max(22),
      ],
    },
    {
      columnId: 'dnrcId',
      title: 'Judge',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      selectArr: [],
    },
    {
      columnId: 'completeName',
      title: 'Judge',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'countyName',
      title: 'County',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'countyId',
      title: 'County',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'supremeCourtCauseNumber',
      title: 'Supreme Court Cause #',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(30)],
    },
  ];
  private currentDistrictCourtNumber = null;
  private reloadColumns = new Subject<{
    columns: ColumnDefinitionInterface[];
    markAsDirty?;
    markAllAsTouched?;
  }>();

  protected initFunction() {
    this.idArray = [this.route.snapshot.params.caseId];
    this._get();
  }

  protected _getHelperFunction(data: any): any {
    if (data.get.results?.length) {
      this.onCourtSelect.emit(data.get.results[0].districtId);
    } else {
      this.onCourtSelect.emit(null);
    }

    return data.get;
  }

  // Handle the onRowClick event
  public rowClick(data: any): void {
    this.onCourtSelect.emit(data.districtId);
  }

  protected populateDropdowns(): void {
    if (this.currentDistrictCourtNumber) {
      this.observables.staffs = new ReplaySubject(1);
      this.observables.counties = new ReplaySubject(1);

      this.districtCourtStaffService
        .get({}, this.currentDistrictCourtNumber)
        .subscribe((staffs) => {
          this._getColumn('dnrcId').selectArr = staffs.results.map(
            (staff: { staffId: number; name: string }) => ({
              name: staff.name,
              value: staff.staffId,
            })
          );
          this.observables.staffs.next(staffs);
          this.observables.staffs.complete();
        });

      this.districtCourtCountiesService
        .get({}, this.currentDistrictCourtNumber)
        .subscribe((counties) => {
          const countiesArr: SelectionInterface[] = counties.results.map(
            (county: { id: number; name: string }) => ({
              name: county.name,
              value: county.id,
            })
          );
          countiesArr.unshift({
            name: null,
            value: null,
          });
          this._getColumn('countyId').selectArr = countiesArr;

          this.observables.counties.next(counties);
          this.observables.counties.complete();
        });

      forkJoin({ ...this.observables }).subscribe(() => {
        this.reloadColumns.next({
          columns: this.columns,
          markAsDirty: true,
          markAllAsTouched: true,
        });
      });
    }
  }

  protected getInsertDialogTitle() {
    return 'Add New Court Record';
  }

  protected getEditDialogTitle() {
    return 'Update Court Record';
  }

  private attachChangeCourtEventAndReloadObservable(
    dialogRef:
      | MatDialogRef<InsertDialogComponent, any>
      | MatDialogRef<UpdateDialogComponent, any>
  ) {
    dialogRef.componentInstance.reloadColumns$ =
      this.reloadColumns.asObservable();
    dialogRef.componentInstance.changeEvent.subscribe(($event) => {
      if ($event.fieldName === 'districtCourtNumber') {
        if (
          dialogRef.componentInstance.formGroup.get('districtCourtNumber')
            .valid &&
          $event.value
        ) {
          this.currentDistrictCourtNumber = $event.value;
          dialogRef.componentInstance.formGroup.get('dnrcId').reset();
          dialogRef.componentInstance.formGroup.get('countyId').reset();
          this.populateDropdowns();
        } else {
          dialogRef.componentInstance.formGroup.get('dnrcId').reset();
          dialogRef.componentInstance.formGroup.get('dnrcId').disable();
          dialogRef.componentInstance.formGroup.get('countyId').reset();
          dialogRef.componentInstance.formGroup.get('countyId').disable();
        }
      }
    });
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getInsertDialogTitle(),
        columns: this.columns,
        values: data || {},
        validators: this.validators,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
      if (dialogRef.componentInstance.changeEvent) {
        dialogRef.componentInstance.changeEvent.unsubscribe();
      }
    });
    this.attachChangeCourtEventAndReloadObservable(dialogRef);
    setTimeout(() => {
      if (!data?.districtCourtNumber) {
        dialogRef.componentInstance.formGroup.get('dnrcId').disable();
        dialogRef.componentInstance.formGroup.get('countyId').disable();
      }
      dialogRef.componentInstance.formGroup.markAsDirty();
    });
  }

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    if (
      updatedData?.districtCourtNumber &&
      updatedData.districtCourtNumber !== this.currentDistrictCourtNumber
    ) {
      this.currentDistrictCourtNumber = updatedData.districtCourtNumber;
      this.populateDropdowns();
    }
    this._displayEditDialog(updatedData);
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getEditDialogTitle(),
        columns: this.columns,
        values: data,
        validators: this.validators,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result), data);
      }
      if (dialogRef.componentInstance.changeEvent) {
        dialogRef.componentInstance.changeEvent.unsubscribe();
      }
    });
    this.attachChangeCourtEventAndReloadObservable(dialogRef);
    setTimeout(() => {
      if (!data?.districtCourtNumber) {
        dialogRef.componentInstance.formGroup.get('dnrcId').disable();
        dialogRef.componentInstance.formGroup.get('countyId').disable();
      }
    });
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].districtId];
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.districtId];
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
