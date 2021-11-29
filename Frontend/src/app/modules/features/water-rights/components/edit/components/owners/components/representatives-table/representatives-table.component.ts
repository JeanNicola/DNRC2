import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { RoleTypesService } from 'src/app/modules/features/applications/components/edit/components/applicant/services/role-types.service';
import { OwnerRepresentativesService } from 'src/app/modules/features/water-rights/services/owner-representatives.service';
import { InsertRepresentativeComponent } from 'src/app/modules/shared/components/insert-representatives/insert-representative.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

@Component({
  selector: 'app-representatives-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [OwnerRepresentativesService, RoleTypesService],
})
export class RepresentativesTableComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  @Input() ownerData: any;

  @Output() repCountEvent: EventEmitter<any> = new EventEmitter<any>();
  constructor(
    public service: OwnerRepresentativesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public roleTypeService: RoleTypesService,
    public router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'representativeId',
      title: 'Representative Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'name',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'roleCode',
      title: 'Role',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'roleDescription',
      title: 'Role',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
      validators: [
        Validators.required,
        WRISValidators.dateBeforeToday,
        WRISValidators.requireOtherFieldIfNonNull('endDate'),
      ],
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      validators: [
        WRISValidators.dateBeforeToday,
        WRISValidators.afterOtherField('beginDate', 'Begin Date'),
      ],
    },
  ];

  public resultsColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      noSort: true,
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
    {
      columnId: 'name',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];

  public title = '';
  public searchable = false;
  public isInMain = false;
  public hideDelete = true;
  public primarySortColumn = 'endDate';
  public sortDirection = 'desc';
  public dblClickableRow = true;
  public isDecreed = false;
  public isEditableIfDecreed = false;

  initFunction(): void {
    if (this.ownerData?.beginDate != null) {
      this._getColumn('beginDate').validators.push(
        WRISValidators.afterDate(this.ownerData.beginDate)
      );
    }
    if (this.ownerData?.endDate != null) {
      this._getColumn('beginDate').validators.push(
        WRISValidators.beforeDate(this.ownerData.endDate)
      );
    }

    this.isDecreed = this.ownerData.isDecreed;
    this.isEditableIfDecreed = this.ownerData.isEditableIfDecreed;

    this._get();
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPOST:
        this.endpointService.canPUT(this.service.url) &&
        this.isEditableIfDecreed,
      canPUT:
        this.endpointService.canPUT(this.service.url) &&
        this.isEditableIfDecreed,
    };
  }

  public populateDropdowns(): void {
    this.observables.types = new ReplaySubject(1);
    this.roleTypeService
      .get(this.queryParameters)
      .subscribe((types: { results: any[] }) => {
        this._getColumn('roleCode').selectArr = types.results.map((type) => ({
          value: type.code,
          name: type.description,
        }));
        this.observables.types.next(types);
        this.observables.types.complete();
      });
  }

  protected _getHelperFunction(data: any): any {
    this.repCountEvent.emit(data?.get?.totalElements);
    return data.get;
  }

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  // Handle the onEdit event
  public onEdit(updatedData: any): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  protected _displayInsertDialog(data: any): void {
    const dialogRef = this.dialog.open(InsertRepresentativeComponent, {
      data: {
        columns: this.resultsColumns,
        formColumns: [
          this._getColumn('roleCode'),
          this._getColumn('beginDate'),
          this._getColumn('endDate'),
        ],
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.contactId != null) {
        this._insert({
          contactId: result?.contactId,
          beginDate: result?.beginDate,
          endDate: result?.endDate,
          roleCode: result?.roleCode,
        });
      }
    });
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.representativeId];
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'contacts', data.contactId]);
  }

  public ngOnDestroy(): void {}
}
