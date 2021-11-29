import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { VersionTypesService } from '../../../water-rights/services/version-types.service';
import { WaterRightTypesService } from '../../../water-rights/services/water-right-types.service';
import { PurposesService } from './services/purposes.service';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [PurposesService, WaterRightTypesService, VersionTypesService],
})
export class SearchComponentForPurposes extends BaseCodeTableComponent {
  constructor(
    public service: PurposesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute,
    private waterRightTypesService: WaterRightTypesService,
    public versionTypeService: VersionTypesService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'waterRightTypeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'completeWaterRightVersion',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'purposeDescription',
      title: 'Purpose',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      searchValidators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      searchValidators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'waterRightType',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Select,
      searchValidators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'version',
      title: 'Version #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'versionType',
      title: 'Version Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
  ];

  public title = 'Purpose/Place of Use (POU) Details';
  public primarySortColumn = 'completeWaterRightNumber';
  public sortDirection = 'asc';
  public hideActions = true;
  public hideEdit = true;
  public hideDelete = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;
  public hideInsert = true;

  public initFunction(): void {
    this.dataMessage = 'Search for Purpose/Place of Use Details';
  }

  private redirectToPurposeEditScreen(
    waterRightId: number,
    versionId: number,
    purposeId: number
  ) {
    void this.router.navigate([
      'wris',
      'water-rights',
      waterRightId,
      'versions',
      versionId,
      'purposes',
      purposeId,
    ]);
  }

  protected _getHelperFunction(data?: any): any {
    if (data.get?.results?.length === 1 && data.get.currentPage === 1) {
      this.redirectToPurposeEditScreen(
        data.get.results[0].waterRightId,
        data.get.results[0].versionId,
        data.get.results[0].purposeId
      );
    }
    return data.get;
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToPurposeEditScreen(
      data.waterRightId,
      data.versionId,
      data.purposeId
    );
  }

  public populateDropdowns(): void {
    // Water Right Types
    this.observables.waterRightTypes = new ReplaySubject(1);
    this.waterRightTypesService.get(this.queryParameters).subscribe((types) => {
      this._getColumn('waterRightType').selectArr = types.results.map(
        (type: { value: string; description: string }) => ({
          name: type.description,
          value: type.value,
        })
      );

      this.observables.waterRightTypes.next(types);
      this.observables.waterRightTypes.complete();
    });
    // Version Types
    this.observables.versionTypes = new ReplaySubject(1);
    this.versionTypeService.get(this.queryParameters).subscribe((types) => {
      this._getColumn('versionType').selectArr = types.results.map((type) => ({
        value: type.value,
        name: type.description,
      }));
      this.observables.versionTypes.next(types);
      this.observables.versionTypes.complete();
    });
  }
}
