import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { OwnerOriginsService } from '../../../../services/owner-origins.service';
import { OwnersService } from '../../../../services/owners.service';
import { RepresentativesDialogComponent } from './components/representatives-dialog/representatives-dialog.component';

@Component({
  selector: 'app-owners',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './owners.component.scss',
  ],
  providers: [OwnersService, OwnerOriginsService],
})
export class OwnersComponent extends BaseCodeTableComponent {
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = false;

  private _waterRightType = null;
  private _compactedOriginArray: any;
  @Input() set waterRightType(t: string) {
    this._waterRightType = t;
    this._updateOriginOptions();
  }

  constructor(
    public service: OwnersService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public router: Router,
    public originService: OwnerOriginsService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'ownerId',
      title: 'Owner Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
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
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
    },
    {
      columnId: 'originalOwner',
      title: 'Original Owner',
      type: FormFieldTypeEnum.Checkbox,
      displayInEdit: false,
    },
    {
      columnId: 'contractForDeed',
      title: 'Contract For Deed',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'receivedMail',
      title: 'Received Mail?',
      type: FormFieldTypeEnum.Checkbox,
      displayInEdit: false,
      displayInTable: false,
    },
    {
      columnId: 'originValue',
      title: 'Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'originDescription',
      title: 'Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'more',
      title: 'Representatives',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'repCount',
      displayInEdit: false,
      noSort: true,
    },
  ];
  public title = 'Owners';
  protected dialogWidth = '350px';
  public hideDelete = true;
  public searchable = false;
  public hideInsert = true;
  public isInMain = false;
  public primarySortColumn = 'endDate';
  public sortDirection = 'desc';
  public dblClickableRow = true;

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  public initFunction(): void {
    this._get();
  }

  public cellClick(data: any): void {
    if (data?.columnId === 'more') {
      const dialogRef = this.dialog.open(RepresentativesDialogComponent, {
        data: {
          idArray: [
            ...this.idArray,
            this.rows[data.row]?.ownerId,
            this.rows[data.row]?.contactId,
          ],
          contactId: this.rows[data.row]?.contactId,
          name: this.rows[data.row]?.name,
          beginDate: this.rows[data.row]?.beginDate,
          endDate: this.rows[data.row]?.endDate,
          isDecreed: this.isDecreed,
          isEditableIfDecreed: this.isEditableIfDecreed,
        },
      });

      dialogRef.afterClosed().subscribe((count: number) => {
        this.rows[data.row].repCount = count;
      });
    }
  }

  public populateDropdowns(): void {
    this.observables.types = new ReplaySubject(1);
    this.originService
      .get(this.queryParameters)
      .subscribe((types: { results: any[] }) => {
        this._getColumn('originValue').selectArr = types.results.map(
          (type) => ({
            value: type.value,
            name: type.description,
          })
        );

        this._getColumn('originValue').selectArr.unshift({
          name: null,
          value: null,
        });

        // During initial load, save the initial download from backend as the 'CMPT' reference
        // will be added/deleted based on waterTypeCode
        this._compactedOriginArray = [
          ...this._getColumn('originValue').selectArr,
        ];
        this._updateOriginOptions();

        this.observables.types.next(types);
        this.observables.types.complete();
      });
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT:
        this.endpointService.canPUT(this.service.url) &&
        this.isEditableIfDecreed,
    };
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.ownerId, originalData.contactId];
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'contacts', data.contactId]);
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkDecree(
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }

  private _updateOriginOptions() {
    // If the water right type is not Compact, then do not allow a Compact origin
    if (this._compactedOriginArray) {
      if (this._waterRightType !== 'CMPT') {
        this._getColumn('originValue').selectArr =
          this._compactedOriginArray.filter((v) => v.value !== 'CMPT');
      } else {
        this._getColumn('originValue').selectArr = this._compactedOriginArray;
      }
    }
  }
}
