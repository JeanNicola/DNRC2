import { AfterViewInit } from '@angular/core';
import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { VersionTypesService } from 'src/app/modules/features/water-rights/services/version-types.service';
import { WaterRightTypesService } from 'src/app/modules/features/water-rights/services/water-right-types.service';
import { SearchDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/search-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ExaminationsSearchService } from './services/examinations-search.service';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    WaterRightTypesService,
    VersionTypesService,
    ExaminationsSearchService,
  ],
})
export class SearchComponent
  extends BaseCodeTableComponent
  implements AfterViewInit
{
  constructor(
    public service: ExaminationsSearchService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private waterRightTypesService: WaterRightTypesService,
    public versionTypeService: VersionTypesService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Examination Details';
  public primarySortColumn = 'completeWaterRightNumber';
  public sortDirection = 'asc';
  public dialogWidth = '400px';
  public hideActions = true;
  public hideEdit = true;
  public hideDelete = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;
  public hideInsert = true;

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
      columnId: 'waterRightStatusDescription',
      title: 'Water Right Status',
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

  public initFunction(): void {
    this.dataMessage = 'Search for a Examination';
  }

  private redirectToExaminationsEditScreen(examinationId: number) {
    void this.router.navigate([
      'wris',
      'water-court',
      'examinations',
      examinationId,
    ]);
  }

  protected _getHelperFunction(data?: any): any {
    if (data.get?.results?.length === 1 && data.get.currentPage === 1) {
      this.redirectToExaminationsEditScreen(data.get.results[0].examinationId);
    }
    return data.get;
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToExaminationsEditScreen(data.examinationId);
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

  /*
   * Display the Search dialog and, if data is returned, call the get function
   */
  protected displaySearchDialog(): void {
    // Open the dialog
    const dialogRef = this.dialog.open(SearchDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Search ${this.title}`,
        columns: this.columns,
        values: {
          waterRightType: 'STOC',
        },
      },
    });

    // Add the filters to the queryParameters, call the get function
    dialogRef.afterClosed().subscribe((result: { [key: string]: string }) => {
      if (result !== null && result !== undefined) {
        this.queryParameters.filters = { ...result };
        this.queryParameters.pageNumber = 1;
        this._get();
      } else {
        this.firstSearch.focus();
      }
    });
  }
}
