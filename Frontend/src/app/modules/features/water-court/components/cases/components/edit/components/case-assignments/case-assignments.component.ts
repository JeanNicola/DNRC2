import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CaseAssignmentTypesService } from 'src/app/modules/features/code-tables/components/case-assignment-types/services/case-assignment-types.service';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { StaffService } from 'src/app/modules/shared/components/templates/file-location-processor/services/staff.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { CaseProgramTypes } from '../../../create/enums/caseProgramTypes';
import { CaseAssignmentsService } from './services/case-assignments.service';

@Component({
  selector: 'app-case-assignments',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CaseAssignmentsService, CaseAssignmentTypesService, StaffService],
})
export class CaseAssignmentsComponent extends BaseCodeTableComponent {
  constructor(
    public service: CaseAssignmentsService,
    public caseAssignmentTypesService: CaseAssignmentTypesService,
    public staffService: StaffService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() hasCaseAdminRole = false;
  private _programType = null;

  @Input() set programType(value: string) {
    this._programType = value;
    if (
      !this.hasCaseAdminRole &&
      this._programType === CaseProgramTypes.NA_PROGRAM
    ) {
      this.hideInsert = true;
      this.hideActions = true;
    }
  }

  get programType(): string {
    return this._programType;
  }

  public dialogWidth = '400px';
  public isInMain = false;
  public searchable = false;
  public title = '';
  public primarySortColumn = 'completeName';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'completeName',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  private allAssignmentTypes = [];

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeName',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'dnrcId',
      title: 'Name',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
    },
    {
      columnId: 'assignmentTypeDescription',
      title: 'Role',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'assignmentType',
      title: 'Role',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
    },
  ];

  protected initFunction() {
    this.idArray = [this.route.snapshot.params.caseId];
    this._get();
  }

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    const assignmentTypesBeingUsed = data.get.results
      .filter((assignment) => {
        return !assignment.endDate;
      })
      .map((assignment) => {
        return assignment.assignmentType;
      });
    this._getColumn('assignmentType').selectArr =
      this.allAssignmentTypes.filter(
        (assignmentType: { name: string; value: string }) => {
          return !assignmentTypesBeingUsed.includes(assignmentType.value);
        }
      );
    return data.get;
  }

  protected populateDropdowns(): void {
    this.observables.assignmentTypes = new ReplaySubject(1);

    this.caseAssignmentTypesService.get().subscribe((assignmentTypes) => {
      this.allAssignmentTypes = assignmentTypes.results.map(
        (assignmentType: { code: string; assignmentType: string }) => ({
          name: assignmentType.assignmentType,
          value: assignmentType.code,
        })
      );
      this._getColumn('assignmentType').selectArr = this.allAssignmentTypes;

      this.observables.assignmentTypes.next(assignmentTypes);
      this.observables.assignmentTypes.complete();
    });

    this.observables.staffs = new ReplaySubject(1);

    this.staffService.get({}).subscribe((staffs) => {
      this._getColumn('dnrcId').selectArr = staffs.results.map(
        (staff: { staffId: number; name: string }) => ({
          name: staff.name,
          value: staff.staffId,
        })
      );
      this.observables.staffs.next(staffs);
      this.observables.staffs.complete();
    });
  }

  protected getInsertDialogTitle() {
    return `Add New Assignment Record`;
  }

  protected getEditDialogTitle() {
    return `Update Assignment Record`;
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].assignmentId];
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.assignmentId];
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    // Add current rol to selectArr of assignmentType
    let currentAvailableRoles = this._getColumn('assignmentType').selectArr.map(
      (rol) => {
        return { ...rol };
      }
    );
    currentAvailableRoles.unshift({
      name: data.assignmentTypeDescription,
      value: data.assignmentType,
    });
    // Sort currentAvailableRoles since we added the current rol to the list
    currentAvailableRoles = currentAvailableRoles.sort((a, b) => {
      if (a.name > b.name) {
        return 1;
      }
      if (a.name < b.name) {
        return -1;
      }
      return 0;
    });
    // Create a copy of the columns and attach the new selectArr
    const columns = this.columns.map((col) => {
      return col.columnId === 'assignmentType'
        ? { ...col, selectArr: currentAvailableRoles }
        : { ...col };
    });
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.getEditDialogTitle(),
        columns: columns,
        values: data,
        validators: this.validators,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
