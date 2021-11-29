import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ChildRightsService } from 'src/app/modules/features/water-rights/services/child-rights.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'app-child-rights-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './child-rights-table.component.scss',
  ],
  providers: [ChildRightsService],
})
export class ChildRightsTableComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  @Input() set waterRightId(value: string) {
    this.idArray = [value];
  }
  @Output() dblClickEvent: EventEmitter<number> = new EventEmitter<number>();

  constructor(
    public service: ChildRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'statusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public title = '';
  public searchable = false;
  public hideInsert = true;
  protected hideActions = true;
  public isInMain = false;
  protected dblClickableRow = true;

  public onRowDoubleClick(data: any): void {
    this.dblClickEvent.emit(data.waterRightId);
  }

  protected initFunction(): void {
    this._get();
  }

  public ngOnDestroy(): void {}
}
