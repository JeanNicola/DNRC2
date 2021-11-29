import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Validators } from '@angular/forms';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { BaseCodeTableComponent } from '../../../../../shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { CityZipCodesService } from '../services/city-zip-codes.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { StateCodesPageInterface } from 'src/app/modules/shared/interfaces/state-codes-page.interface';
import { StateCodesRowInterface } from 'src/app/modules/shared/interfaces/state-codes-row-interface';
import { HttpErrorResponse } from '@angular/common/http';
import { ReplaySubject } from 'rxjs';
import { ErrorMessageEnum } from '../../../enums/error-message.enum';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

@Component({
  selector: 'code-table-city-zip-codes',
  templateUrl:
    '../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CityZipCodesService],
})
export class CityZipCodesComponent extends BaseCodeTableComponent {
  protected url = '/zip-codes';
  public title = 'City / Zip Codes';
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'zipCode',
      title: 'Zip Code',
      type: FormFieldTypeEnum.Input,
      // editable: false,
      validators: [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(5),
        WRISValidators.integer,
      ],
    },
    {
      columnId: 'cityName',
      title: 'City Name',
      type: FormFieldTypeEnum.TextArea,
      validators: [
        Validators.required,
        Validators.maxLength(30),
        WRISValidators.preventNewLineCharacter,
      ],
    },
    {
      columnId: 'stateCode',
      title: 'State Code',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'stateName',
      title: 'State',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'id',
      title: 'Id',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInTable: false,
      displayInSearch: false,
      displayInInsert: false,
    },
  ];

  public populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the getPrograms http request and the selectArr
    // population only happens once
    this.observables.states = new ReplaySubject(1);
    this.service
      .getStateCodes()
      .subscribe((stateCodes: StateCodesPageInterface) => {
        this._getColumn('stateCode').selectArr = stateCodes.results.map(
          (stateCode: StateCodesRowInterface) => ({
            name: stateCode.name,
            value: stateCode.code,
          })
        );
        this.onSearch();
        this.observables.states.next(stateCodes);
        this.observables.states.complete();
      });
  }

  initFunction(): void {
    this.dataMessage = null;
  }

  constructor(
    public service: CityZipCodesService,
    public endpointsService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointsService, dialog, snackBar);
  }

  /*
   * When updating a row, use 'id' instead of 'code' as the primary key
   */
  _update(updatedRow: any): void {
    this.service.update(updatedRow, updatedRow.id).subscribe(
      () => {
        this._get();
        this.snackBar.open('Record successfully updated.', null);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update record. ';
        message += errorBody.userMessage || ErrorMessageEnum.PUT;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this._displayEditDialog(updatedRow);
      }
    );
  }

  /*
   * When deleting a row, use 'id' instead of 'code' as the primary key
   */
  _delete(row: number): void {
    if (this.rows[row].id != null) {
      this.service.delete(this.rows[row].id).subscribe(
        () => {
          this._get();
          this.snackBar.open('Record successfully deleted.');
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
    } else {
      this.service.deleteCity(this.data.results[row].cityId).subscribe(
        () => {
          this._get();
          this.snackBar.open('Record successfully deleted.');
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
    }
  }

  /*
   * When updating a row, if no primary key exists, POST the data.
   * If it does exist, reinsert the hidden primary key 'id' and PUT
   */
  _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      data: {
        title: `Update ${this.title} Record`,
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        if (data?.id != null) {
          this._update({ ...result, id: data.id });
        } else {
          this._insert(result);
        }
      }
    });
  }
}
