import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { SelectionCodeTableComponent } from 'src/app/modules/shared/components/templates/selection-code-table/selection-code-table.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { PopulateByGeocodesService } from '../../services/populate-by-geocodes.service';
import { GeocodesInfoDialogComponent } from './components/geocodes-info-dialog/geocodes-info-dialog.component';

@Component({
  selector: 'app-insert-multiple-water-rights',
  templateUrl:
    '../../../../../../../../shared/components/templates/selection-code-table/selection-code-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/selection-code-table/selection-code-table.component.scss',
  ],
  providers: [PopulateByGeocodesService],
})
export class InsertMultipleWaterRightsComponent
  extends SelectionCodeTableComponent
  implements OnDestroy
{
  constructor(
    public service: PopulateByGeocodesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public router: Router,
    public route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() onRowDoubleClickEvent = new EventEmitter();

  @Input() public ownershipUpdateId;
  @Input() public serviceToUse: BaseDataService;

  protected clickableRow = false;
  protected dblClickableRow = false;
  protected searchable = false;
  public hideEdit = true;
  public hideDelete = true;
  public hideActions = false;
  public enableMoreInfo = true;
  public hideInsert = true;
  public isInMain = false;
  public title = '';
  public primarySortColumn = 'completeWaterRightNumber';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'completeWaterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'version',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type Description',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected initFunction(): void {
    this.idArray = [this.ownershipUpdateId];
    this._get();
  }

  protected _getService(): BaseDataService {
    return this.serviceToUse;
  }

  protected _getHelperFunction(data: any): any {
    return {
      ...data.get,
      results: data.get.results,
    };
  }

  public ngOnDestroy(): void {}

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    this.onRowDoubleClickEvent.emit(data);
  }

  private _displayMoreInfoDialog(row: number): void {
    // Open the dialog
    const dialogRef = this.dialog.open(GeocodesInfoDialogComponent, {
      width: '950px',
      data: {
        values: {
          ...this.rows[row],
        },
      },
    });

    dialogRef.afterClosed().subscribe(() => {
      const moreInfoButtons: any = document.querySelectorAll(
        'button[ng-reflect-message="More Info"]'
      );

      if (moreInfoButtons[row]?.focus) moreInfoButtons[row]?.focus();
    });
  }

  // Handle the moreInfo event
  public moreInfoHandler(row: number): void {
    this._displayMoreInfoDialog(row);
  }
}
