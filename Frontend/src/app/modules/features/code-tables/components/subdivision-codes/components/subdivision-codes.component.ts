import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { BaseCodeTableComponent } from '../../../../../shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { SubdivisionCodesService } from '../../../../../shared/services/subdivision-codes.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { CountiesPageInterface } from 'src/app/modules/shared/interfaces/counties-page.interface';
import { CountiesRowInterface } from 'src/app/modules/shared/interfaces/counties-row.interface';
import { SubdivisionCodesInsertDialogComponent } from './subdivision-codes-insert-dialog.component';
import { ReplaySubject } from 'rxjs';
import { Validators } from '@angular/forms';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

@Component({
  selector: 'code-table-subdivision-codes',
  templateUrl:
    '../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [SubdivisionCodesService],
})
export class SubdivisionCodesComponent extends BaseCodeTableComponent {
  protected url = '/subdivision-codes';
  public title = 'Subdivision Codes';
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'code',
      title: 'Code',
      type: FormFieldTypeEnum.Input,
      editable: false,
      validators: [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(5),
      ],
    },
    {
      columnId: 'dnrcName',
      title: 'DNRC Name',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.required, Validators.maxLength(50)],
    },
    {
      columnId: 'dorName',
      title: 'DOR Name',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(50)],
      displayInSearch: false,
    },
    {
      columnId: 'countyId',
      title: 'County',
      type: FormFieldTypeEnum.Autocomplete,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'countyName',
      title: 'County',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInSearch: false,
      displayInInsert: false,
      displayInEdit: false,
    },
  ];

  countiesArr: any;

  public populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the getPrograms http request and the selectArr
    // population only happens once
    this.observables.counties = new ReplaySubject(1);
    this.service.getCounties().subscribe((counties: CountiesPageInterface) => {
      this._getColumn('code').validators.push(
        WRISValidators.matchStateCountyIdToId(counties.results)
      );
      this.countiesArr = counties.results;
      this._getColumn('countyId').selectArr = counties.results.map(
        (county: CountiesRowInterface) => ({
          name: county.name,
          value: county.id,
        })
      );

      // Generate search dialog immediately following counties load
      this.onSearch();

      this.observables.counties.next(counties);
      this.observables.counties.complete();
    });
  }

  initFunction(): void {
    this.dataMessage = null;
  }

  constructor(
    public service: SubdivisionCodesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(SubdivisionCodesInsertDialogComponent, {
      data: {
        title: `Add New ${this.title} Record`,
        columns: this.columns,
        values: data,
        countiesArr: this.countiesArr,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      }
    });
  }
}
