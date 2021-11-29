import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { BaseCodeTableComponent } from '../../../../../shared/components/templates/code-table/code-table.template';

@Component({
  template: '',
})
export class EventSubtypeComponent extends BaseCodeTableComponent {
  paging = false;
  searchable = false;
  hideEdit = true;
  hideHeader = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'eventCode',
      title: 'Event Code',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
    },
    {
      columnId: 'code',
      title: 'Select a Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      selectArr: [],
      validators: [Validators.required],
    },
    {
      columnId: 'description',
      title: 'Description',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      noSort: true,
    },
  ];

  constructor(
    public service: BaseDataService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private lookupService: BaseDataService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  protected initFunction(): void {
    this._get();
  }

  protected populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the http request and the selectArr
    // population only happens once
    this.observables.codes = new ReplaySubject(1);
    // Get the list of application types
    this.lookupService.getAll().subscribe((data: EventSubType) => {
      this._getColumn('code').selectArr = data.results.map((item) => ({
        name: item.description,
        value: item.code,
      }));

      this.observables.codes.next(data);
      this.observables.codes.complete();
    });
  }

  protected _getHelperFunction(data: any): any {
    // Filter out dropdown values already present in the data
    this._getColumn('code').selectArr = this.trimArr(
      data.get.results.map((item) => ({
        name: item.description,
        value: item.code,
      }))
    );
    return data.get;
  }

  // This function filters out items from the selectArr
  // that are already present in the data.
  trimArr(data: SelectionInterface[]): SelectionInterface[] {
    return this._getColumn('code').selectArr.filter(
      (x: SelectionInterface) =>
        !data.some((y: SelectionInterface) => x.value === y.value)
    );
  }
}

export interface EventSubType {
  results: {
    code: string;
    description: string;
    program?: string;
    programDescription?: string;
  }[];
}
