import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Injector,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { RetiredPouSubdivisionsService } from '../../../retired-place-of-use/services/retired-pou-subdivisions.service';
import { SubdivisionInformationService } from '../../services/subdivision-information.service';
import { InsertSubdivisionComponent } from './components/insert-subdivision/insert-subdivision.component';

@Component({
  selector: 'app-subdivision-information',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    { provide: BaseDataService, useClass: SubdivisionInformationService },
  ],
})
export class SubdivisionInformationComponent
  extends BaseCodeTableComponent
  implements OnInit
{
  constructor(
    public service: BaseDataService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private injector: Injector
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() delete = new EventEmitter();
  @Output() insert = new EventEmitter();

  // Set the dataservice based on isRetired
  @Input() set isRetired(ret: boolean) {
    if (ret) {
      this.service = this.injector.get(SubdivisionInformationService);
    } else {
      this.service = this.injector.get(RetiredPouSubdivisionsService);
    }
  }

  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
        this.hideInsert = true;
      } else if (!id.includes(undefined) && this.service) {
        this._idArray = id;
        this.hideInsert = false;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = false;
  @Input() countyId = null;
  @Input() versionNumber = null;

  public hideInsert = true;
  public primarySortColumn = 'dnrcName';
  public title = 'Subdivision Info';
  public searchable = false;
  public isInMain = false;
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'asc',
    sortColumn: 'dnrcName',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'dnrcName',
      title: 'DNRC Name',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(50)],
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'dorName',
      title: 'DOR Name',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(50)],
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'lot',
      title: 'Lot',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(20)],
      displayInSearch: false,
    },
    {
      columnId: 'blk',
      title: 'Block',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(8)],
      displayInSearch: false,
    },
  ];

  protected getInsertDialogTitle() {
    return `Add New Subdivision Record`;
  }

  protected getEditDialogTitle() {
    return `Update Subdivision Record`;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.code];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].code];
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.service
      .insert(this._buildInsertDto(newRow), ...this._buildInsertIdArray(newRow))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this._get();
          this.insert.emit(null);
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot insert new record. ';
          message += errorBody.userMessage || ErrorMessageEnum.POST;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayInsertDialog(newRow);
        }
      );
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertSubdivisionComponent, {
      width: 'auto',
      data: {
        title: this.getInsertDialogTitle(),
        columns: this.columns,
        values: {
          ...data,
          countyId: this.countyId,
        },
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

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  protected _delete(row: number): void {
    this._getDeleteService()
      .delete(...this._buildDeleteIdArray(row))
      .subscribe(
        () => {
          this._get();
          this.delete.emit(null);
          this.snackBar.open('Record successfully deleted.');
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPOST, canDELETE, and canPUT values

    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url) && this.canEdit,
    };
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
