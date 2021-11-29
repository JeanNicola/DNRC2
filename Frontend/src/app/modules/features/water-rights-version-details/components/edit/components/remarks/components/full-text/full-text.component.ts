import { Component, EventEmitter, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { FullTextService } from './services/full-text.service';

@Component({
  selector: 'app-full-text',
  templateUrl: './full-text.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './full-text.component.scss',
  ],
  providers: [FullTextService],
})
export class FullTextComponent extends DataRowComponent {
  @Input() variableUpdate: EventEmitter<void>;

  constructor(
    public service: FullTextService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Full Remark Text';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'remarkText',
      title: '',
      type: FormFieldTypeEnum.TextArea,
    },
  ];

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

  protected initFunction(): void {
    this.variableUpdate.subscribe(() => {
      this._get();
    });
  }
}
