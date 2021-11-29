import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { AerialPhotosService } from '../../services/aerial-photos.service';
import { PhotoTypesService } from '../../services/photo-types.service';
import { DataSourceTypes } from '../constants/DataSourceTypes';

@Component({
  selector: 'app-aerial-photos-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [AerialPhotosService, PhotoTypesService],
})
export class AerialPhotosTableComponent
  extends BaseCodeTableComponent
  implements OnChanges
{
  constructor(
    public service: AerialPhotosService,
    public photoTypesService: PhotoTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() dataChanged = new EventEmitter();
  @Input() sourceType = null;
  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined) && this.service) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public clickableRow = false;
  public isInMain = false;
  public searchable = false;

  public primarySortColumn = 'typeCode';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'typeCode',
      title: 'Photo Source',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'aerialPhotoNumber',
      title: 'Photo Number',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(35)],
    },
    {
      columnId: 'aerialPhotoDate',
      title: 'Photo Date',
      type: FormFieldTypeEnum.Input,
      validators: [
        Validators.required,
        Validators.maxLength(10),
        AerialPhotosTableComponent.aerialPhotoDateValidator(),
      ],
    },
  ];

  public ngOnChanges(changes: SimpleChanges): void {
    if (changes.sourceType) {
      if (this.sourceType === DataSourceTypes.AERIAL_PHOTO) {
        this.title = 'Aerial Photos';
      } else if (this.sourceType === DataSourceTypes.WRS_AERIAL_PHOTO) {
        this.title = 'WRS Aerial Photos';
      }
    }
  }

  static aerialPhotoDateValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const regex = RegExp('^[0-9]+$');
      if (control.value) {
        return regex.test(control.value.replaceAll('/', ''))
          ? null
          : { aerialPhotoDate: true };
      }
      return null;
    };
  }

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    this.hideDelete = data.get.results?.length === 1;

    return data.get;
  }

  protected populateDropdowns(): void {
    // Photo Types
    this.observables.photoTypes = new ReplaySubject(1);
    this.photoTypesService.get(this.queryParameters).subscribe((photoTypes) => {
      const selectArray = photoTypes.results.map(
        (type: { value: string; description: string }) => ({
          name: type.description,
          value: type.value,
        })
      );
      this._getColumn('typeCode').selectArr = selectArray;

      this.observables.photoTypes.next(photoTypes);
      this.observables.photoTypes.complete();
    });
  }

  protected getInsertDialogTitle() {
    return 'Add New Aerial Photo Record';
  }

  protected getEditDialogTitle() {
    return `Update Aerial Photo Record`;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.aerialId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].aerialId];
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
          this.dataChanged.emit(null);
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

  /*
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this._getDeleteService()
      .delete(...this._buildDeleteIdArray(row))
      .subscribe(
        () => {
          this.dataChanged.emit(null);
          this._get();
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

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any, originalData?: any): void {
    this._getUpdateService()
      .update(updatedRow, ...this._buildEditIdArray(updatedRow, originalData))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully updated.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.dataChanged.emit(null);
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot update record. ';
          message += errorBody.userMessage || ErrorMessageEnum.PUT;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayEditDialog({ ...originalData, ...updatedRow });
        }
      );
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
