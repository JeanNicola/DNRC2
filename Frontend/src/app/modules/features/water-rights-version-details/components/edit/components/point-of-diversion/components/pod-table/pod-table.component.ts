import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { PodDropdownService } from '../../services/pod-dropdown.service';
import { PodService } from '../../services/pod.service';
import { InsertPodDialogComponent } from '../insert-pod-dialog/insert-pod-dialog.component';

@Component({
  selector: 'app-pod-table',
  templateUrl: './pod-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './pod-table.component.scss',
  ],
})
export class PodTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: PodService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public dropdownService: PodDropdownService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() inputObservables: { [key: string]: ReplaySubject<unknown> } = {};
  @Input() headerReloadEvent: EventEmitter<void>;
  @Output() podSelect = new EventEmitter<number>();
  @Output() podUpdate = new EventEmitter<void>();

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'podNumber',
      title: 'POD ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'majorTypeDescription',
      title: 'Major Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'legalLandDescription',
      title: 'Legal Land Description',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'meansOfDiversionDescription',
      title: 'Means of Diversion',
      type: FormFieldTypeEnum.Input,
    },
  ];
  public primarySortColumn = 'podNumber';
  public title = '';
  public searchable = false;
  public hideEdit = true;
  public clickableRow = true;
  public dblClickableRow = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public selectArrays: { [key: string]: SelectionInterface[] };
  public selectedPodIndex = 0;
  private _newPOD = 0;

  protected initFunction(): void {
    this._get();
    this.headerReloadEvent.pipe(takeUntil(this.unsubscribe)).subscribe(() => {
      this._get();
    });
  }

  // Handle the onInsertWithData event
  public onCopyPOD(row: any): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this.doCopyPOD.bind(this, row)
    );
  }

  public doCopyPOD(row: any): void {
    this.service
      .copyPod(this.data.results[row].podId, ...this.idArray)
      .subscribe(
        (dto) => {
          this._newPOD = dto.podId;

          let messages = ['POD successfully copied.\n'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.podUpdate.next();
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot copy POD. \n';
          message += errorBody.userMessage || ErrorMessageEnum.POST;
          this.snackBar.open(message);
        }
      );
  }

  protected _getHelperFunction(data: any): any {
    if (this.route.snapshot.queryParamMap.get('podNumber')) {
      const podIndex = data.get.results.findIndex(
        (pod) =>
          pod.podNumber === +this.route.snapshot.queryParamMap.get('podNumber')
      );
      if (podIndex !== -1) {
        this.podSelect.emit(data.get.results[podIndex].podId ?? null);
        this.selectedPodIndex = podIndex;
        return data.get;
      }
    }
    this.podSelect.emit(
      data?.get?.results[this.selectedPodIndex]?.podId ?? null
    );

    if (this._newPOD) {
      data.get.results = data.get.results.map((row) => {
        if (row.podId === this._newPOD) {
          row.podNumber = '*' + row.podNumber;
        }

        return row;
      });
    }

    return data.get;
  }

  protected populateDropdowns(): void {
    this.selectArrays = {
      description40: this.dropdownService.aliquots,
      description80: this.dropdownService.aliquots,
      description160: this.dropdownService.aliquots,
      description320: this.dropdownService.aliquots,
      countyId: this.dropdownService.counties,
      ditchId: this.dropdownService.ditchTypeCode,
      majorTypeCode: this.dropdownService.majorTypeCode,
      meansOfDiversionCode: this.dropdownService.meansOfDiversionCode,
      minorTypeCode: this.dropdownService.minorTypeCode,
      podOriginCode: this.dropdownService.podOriginCode,
      podTypeCode: this.dropdownService.podTypeCode,
      rangeDirection: this.dropdownService.rangeDirections,
      sourceOriginCode: this.dropdownService.sourceOriginCode,
      townshipDirection: this.dropdownService.townshipDirections,
    };
  }

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertPodDialogComponent, {
      width: null,
      data: {
        title: 'Create New POD',
        columns: this.columns,
        values: data ?? {},
        selectArrays: {
          ...this.selectArrays,
        },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result: any[]) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });
  }

  protected _buildInsertDto(newRow: any): void {
    const row = { ...newRow };
    delete row.sourceName;
    return row;
  }

  protected _buildDeleteIdArray(rowNumber: number) {
    return [...this.idArray, this.rows[rowNumber].podId];
  }

  public rowClick(data: any) {
    this.podSelect.emit(data.podId);
  }

  // propogate POD inserts and deletes
  protected _insert(newRow: any): void {
    this.service
      .insert(this._buildInsertDto(newRow), ...this._buildInsertIdArray(newRow))
      .subscribe(
        (dto) => {
          this._newPOD = dto.podId;
          let messages = ['Record successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.podUpdate.next();
          this._get();
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

  protected _delete(row: number): void {
    this._getDeleteService()
      .delete(...this._buildDeleteIdArray(row))
      .subscribe(
        () => {
          // Reset newCopy to remove asterisk until next copy/insert
          this._newPOD = 0;

          this._get();
          this.snackBar.open('Record successfully deleted.');
          this.podUpdate.next();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
  }

  // Override the initial focus
  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
    };
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }
}
