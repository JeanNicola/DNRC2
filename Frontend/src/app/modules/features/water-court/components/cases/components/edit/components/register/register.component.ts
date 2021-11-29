import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { CaseEventTypesService } from '../../../../services/case-event-types.service';
import { RegisterEventsService } from './services/register-events.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './register.component.scss',
  ],
  providers: [RegisterEventsService, CaseEventTypesService],
})
export class RegisterComponent
  extends BaseCodeTableComponent
  implements OnInit, OnDestroy
{
  constructor(
    public service: RegisterEventsService,
    public registerEventTypeService: CaseEventTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  private _caseTypeCode = null;
  @Input() hasCaseAdminRole: boolean = false;
  @Input() set caseTypeCode(value: string) {
    this._caseTypeCode = value;
    this.populateDropdowns();
  }

  get caseTypeCode(): string {
    return this._caseTypeCode;
  }

  public isInMain = false;
  public searchable = false;
  public title = '';
  public primarySortColumn = 'eventTypeDescription';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'eventTypeDescription',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'eventType',
      title: 'Event',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'eventTypeDescription',
      title: 'Event',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'filedDate',
      title: 'Filed Date',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required],
    },
    {
      columnId: 'dueDate',
      title: 'Due Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'enteredBy',
      title: 'Entered By',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'comments',
      title: 'Comments',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(800)],
    },
  ];

  public ngOnInit(): void {
    this.setPermissions();
    this.hideActions = !this.hasCaseAdminRole;
    this.idArray = [this.route.snapshot.params.caseId];
    this._get();
    this.initFunction();

    if (Object.keys(this.observables).length > 0) {
      forkJoin({ ...this.observables }).subscribe(() => {
        this.areDropdownsPopulated = true;
      });
    } else {
      this.areDropdownsPopulated = true;
    }
  }

  protected _getHelperFunction(data: any): any {
    return {
      ...data.get,
      results: data.get.results.map((evt) => {
        return {
          ...evt,
          completeComment: evt.comments,
          comments: evt.comments
            ? evt.comments.substring(0, 300) +
              (evt.comments.length > 300 ? '...' : '')
            : '',
        };
      }),
    };
  }

  protected populateDropdowns(): void {
    this.observables.eventTypes = new ReplaySubject(1);

    this.registerEventTypeService
      .get(this.queryParameters, this.caseTypeCode)
      .subscribe((eventTypes) => {
        this._getColumn('eventType').selectArr = eventTypes.results.map(
          (eventType: { code: string; description: string }) => ({
            name: eventType.description,
            value: eventType.code,
          })
        );
        this.observables.eventTypes.next(eventTypes);
        this.observables.eventTypes.complete();
      });
  }

  protected getInsertDialogTitle() {
    return `Add New Event Record`;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.eventId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].eventId];
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: '630px',
      data: {
        title: this.getInsertDialogTitle(),
        columns: this.columns,
        values: data,
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
    });
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    let currentAvailableTypes = this._getColumn('eventType').selectArr.map(
      (type) => {
        return { ...type };
      }
    );
    const currentTypeIndex = (currentAvailableTypes as any[]).findIndex(
      (evt) => {
        return evt.value === data.eventType;
      }
    );
    if (currentTypeIndex === -1) {
      currentAvailableTypes.unshift({
        name: data.eventTypeDescription,
        value: data.eventType,
      });
      currentAvailableTypes = currentAvailableTypes.sort((a, b) => {
        if (a.name > b.name) {
          return 1;
        }
        if (a.name < b.name) {
          return -1;
        }
        return 0;
      });
    }
    // Create a copy of the columns and attach the new selectArr
    const columns = this.columns.map((col) => {
      return col.columnId === 'eventType'
        ? { ...col, selectArr: currentAvailableTypes }
        : { ...col };
    });
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: '630px',
      data: {
        title: this.getEditDialogTitle(),
        columns: columns,
        values: {
          ...data,
          comments: data.completeComment,
        },
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
