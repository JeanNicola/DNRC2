import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataSourceDetailsService } from '../../services/data-source-details.service';

@Component({
  selector: 'app-field-investigation-row',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './field-investigation-row.component.scss',
  ],
  providers: [DataSourceDetailsService],
})
export class FieldInvestigationRowComponent extends DataRowComponent {
  constructor(
    public service: DataSourceDetailsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() dataChanged = new EventEmitter();
  @Input() set idArray(id: string[]) {
    if (id) {
      if (!id.includes(undefined) && this.service) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): { [key: string]: any } {
    if (this.data) this.dataChanged.emit(null);
    return { ...data.get };
  }

  public showLoading = false;
  public title = 'Field Investigation';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'investigationDate',
      title: 'Field Investigation Date',
      type: FormFieldTypeEnum.Date,
    },
  ];
}
