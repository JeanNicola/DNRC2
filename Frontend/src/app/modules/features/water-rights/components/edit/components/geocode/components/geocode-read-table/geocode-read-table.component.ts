import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { GeocodesService } from 'src/app/modules/features/water-rights/services/geocodes.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';

@Component({
  selector: 'app-geocode-read-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    '/geocode-read-table.component.scss',
  ],
  providers: [GeocodesService],
})
export class GeocodeReadTableComponent extends BaseCodeTableComponent {
  @Input() set inputData(value: DataPageInterface<any>) {
    this.rows = value.results;
    this.data = value;
    if (!this.rows?.length) {
      this.dataMessage = 'No data found';
    }
  }

  @Output() dblClickEvent: EventEmitter<any> = new EventEmitter<any>();
  constructor(
    public service: GeocodesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'xrefId',
      title: 'Geocode Xref Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'geocodeId',
      title: 'Geocode #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'formattedGeocode',
      title: 'Geocode #',
      type: FormFieldTypeEnum.Input,
      width: 200,
    },
    {
      columnId: 'comments',
      title: 'Comment',
      type: FormFieldTypeEnum.TextArea,
      width: 200,
    },
  ];

  public title = '';
  public searchable = false;
  public hideInsert = true;
  public hideActions = true;
  public primarySortColumn = 'formattedGeocode';
  public sortDirection = 'desc';
  public isInMain = false;
  protected dblClickableRow = true;

  public ngOnDestroy(): void {}

  public onRowDoubleClick(row: any): void {
    this.dblClickEvent.emit(row);
  }
}
