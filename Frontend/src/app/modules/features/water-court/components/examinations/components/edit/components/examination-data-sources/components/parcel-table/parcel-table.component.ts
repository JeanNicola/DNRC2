import { HttpErrorResponse } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { ParcelsService } from '../../services/parcels.service';
import { PopulateParcelsService } from '../../services/populate-parcels.service';
import { DataSourceTypes } from '../constants/DataSourceTypes';
import { ExamInfoService } from './services/exam-info.service';

@Component({
  selector: 'app-parcel-table',
  templateUrl: './parcel-table.component.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [ParcelsService, PopulateParcelsService, ExamInfoService],
})
export class ParcelTableComponent
  extends BaseCodeTableComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    public service: ParcelsService,
    public examInfoService: ExamInfoService,
    public populateParcelsService: PopulateParcelsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() dataChanged = new EventEmitter();
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

  @Input() sourceType = null;

  public hideInsert = true;
  public clickableRow = false;
  public isInMain = false;
  public searchable = false;
  public title = 'Parcel Details';
  public primarySortColumn = 'placeId';
  private reloadData$: Subscription;
  @Input() reloadDataObservable: Observable<any> = null;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'placeId',
      title: 'Parcel #',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'examinedAcreage',
      title: 'Examined Acres',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
    },
    {
      columnId: 'acreage',
      title: 'Claimed Acres',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'examInfo',
      title: 'Exam Info',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      noSort: true,
    },
    {
      columnId: 'completeLegalLandDescription',
      title: 'Legal Land Description',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'aerialId',
      title: 'Exam Information',
      type: FormFieldTypeEnum.Select,
      displayInEdit: false,
      displayInTable: false,
    },
    {
      columnId: 'surveyId',
      title: 'Exam Information',
      type: FormFieldTypeEnum.Select,
      displayInEdit: false,
      displayInTable: false,
    },
  ];

  public ngAfterViewInit(): void {
    if (this.reloadDataObservable) {
      this.reloadData$ = this.reloadDataObservable.subscribe(() => {
        this._get();
      });
    }
  }

  public ngOnDestroy() {
    if (this.reloadData$) {
      this.reloadData$.unsubscribe();
    }
  }

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    this._getColumn('aerialId').displayInEdit = false;
    this._getColumn('surveyId').displayInEdit = false;
    if (this.sourceType === DataSourceTypes.FIELD_INVESTIGATION) {
      this._getColumn('examInfo').title = 'Field Investigation';
    } else if (this.sourceType === DataSourceTypes.AERIAL_PHOTO) {
      this._getColumn('examInfo').title = 'Aerial Photo';
      this._getColumn('aerialId').displayInEdit = true;
    } else if (this.sourceType === DataSourceTypes.WRS_AERIAL_PHOTO) {
      this._getColumn('examInfo').title = 'WRS Aerial Photo';
      this._getColumn('aerialId').displayInEdit = true;
    } else if (this.sourceType === DataSourceTypes.WATER_RESOURCE_SURVEY) {
      this._getColumn('examInfo').title = 'Water Resource Survey';
      this._getColumn('surveyId').displayInEdit = true;
    } else {
      this._getColumn('examInfo').title = 'Exam Info';
    }

    if (
      [
        DataSourceTypes.AERIAL_PHOTO,
        DataSourceTypes.WRS_AERIAL_PHOTO,
        DataSourceTypes.WATER_RESOURCE_SURVEY,
      ].includes(this.sourceType)
    ) {
      this.populateExamInfoDropdown();
    }
    return data.get;
  }

  private populateExamInfoDropdown() {
    // Exam Info Values
    this.examInfoService
      .get(this.queryParameters, ...this.idArray)
      .subscribe((options) => {
        const selectArray = options.results.map(
          (option: { value: string; description: string }) => ({
            name: option.description,
            value: +option.value,
          })
        );
        selectArray.unshift({
          name: null,
          value: null,
        });
        if (
          [
            DataSourceTypes.WRS_AERIAL_PHOTO,
            DataSourceTypes.AERIAL_PHOTO,
          ].includes(this.sourceType)
        ) {
          this._getColumn('aerialId').selectArr = selectArray;
        } else if (this.sourceType === DataSourceTypes.WATER_RESOURCE_SURVEY) {
          this._getColumn('surveyId').selectArr = selectArray;
        }
      });
  }

  protected getEditDialogTitle() {
    return `Update Parcel Record`;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.placeId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].placeId];
  }

  public populateParcels() {
    this.populateParcelsService.insert(null, ...this.idArray).subscribe(
      (response) => {
        this._get();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        const message = errorBody.userMessage;
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

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
