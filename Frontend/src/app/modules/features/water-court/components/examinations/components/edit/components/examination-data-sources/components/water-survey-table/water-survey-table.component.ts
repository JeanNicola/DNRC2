import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WaterSurveyCountiesService } from '../../services/water-survey-counties.service';
import { WaterSurveyService } from '../../services/water-survey.service';

@Component({
  selector: 'app-water-survey-table',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [WaterSurveyService, WaterSurveyCountiesService],
})
export class WaterSurveyTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: WaterSurveyService,
    public waterSurveyCountiesService: WaterSurveyCountiesService,
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

  public hideEdit = true;
  public clickableRow = false;
  public isInMain = false;
  public title = 'Water Resource Surveys';
  public primarySortColumn = 'countyName';
  public searchable = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'countyName',
      title: 'County',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'year',
      title: 'Year',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'surveyId',
      title: 'County',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
  ];

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    this.hideActions = data.get.results?.length === 1;

    return data.get;
  }

  protected populateDropdowns(): void {
    // Water Survey Counties
    this.observables.counties = new ReplaySubject(1);
    this.waterSurveyCountiesService
      .get(this.queryParameters)
      .subscribe((counties) => {
        const selectArray = counties.results.map(
          (county: { name: string; surveyId: string; yr: string }) => ({
            name: `${county.name} - ${county.yr}`,
            value: county.surveyId,
          })
        );

        this._getColumn('surveyId').selectArr = selectArray;

        this.observables.counties.next(counties);
        this.observables.counties.complete();
      });
  }

  protected getInsertDialogTitle() {
    return 'Add New Water Resource Survey Record';
  }

  protected getEditDialogTitle() {
    return `Update Water Resource Survey Record`;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.surveyId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].surveyId];
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

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
