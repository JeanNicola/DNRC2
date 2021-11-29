import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ObjectionsVersionService } from './services/objections-version.service';

@Component({
  selector: 'app-objections-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './objections-table.component.scss',
  ],
  providers: [ObjectionsVersionService],
})
export class ObjectionsTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: ObjectionsVersionService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() selectObjection = new EventEmitter<any>();
  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined)) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public primarySortColumn = 'objectionStatusDescription';
  public title = '';
  public hideInsert = true;
  public hideDelete = true;
  public hideActions = true;
  public hideEdit = true;
  public searchable = false;
  public isInMain = false;
  public clickableRow = true;
  public dblClickableRow = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'id',
      title: 'Objection #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'objectionTypeDescription',
      title: 'Objection Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dateReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'late',
      title: 'Late',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'objectionStatusDescription',
      title: 'Objection Status',
      type: FormFieldTypeEnum.Input,
    },
  ];

  private redirectToObjections(id: number) {
    this.router.navigate(['/wris', '/water-court', 'objections', id]);
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToObjections(data.id);
  }

  public rowClick(data: any): void {
    this.selectObjection.emit(data.id);
  }

  protected _getHelperFunction(data: any) {
    if (data.get?.results?.length) {
      this.selectObjection.emit(data.get.results[0].id);
    } else {
      this.selectObjection.emit(null);
    }

    return data.get;
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
