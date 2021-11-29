import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import {
  Enforcement,
  EnforcementsService,
} from 'src/app/modules/shared/services/enforcements.service';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [EnforcementsService],
})
export class SearchComponent
  extends BaseCodeTableComponent
  implements AfterViewInit
{
  constructor(
    public service: EnforcementsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Enforcement Projects';
  public dialogWidth = '400px';
  public primarySortColumn = 'enforcementArea';
  public sortDirection = 'asc';
  public hideActions = true;
  public hideEdit = true;
  public hideDelete = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'enforcementArea',
      title: 'Enf Area',
      type: FormFieldTypeEnum.Input,
      searchValidators: [Validators.maxLength(10)],
    },
    {
      columnId: 'enforcementName',
      title: 'Enf Name',
      type: FormFieldTypeEnum.Input,
      searchValidators: [Validators.maxLength(60)],
    },
    {
      columnId: 'enforcementNumber',
      title: 'Enf #',
      type: FormFieldTypeEnum.Input,
      searchValidators: [Validators.maxLength(20)],
    },
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      searchValidators: [
        WRISValidators.requireOtherFieldIfNonNull('waterNumber'),
        WRISValidators.updateValidityOfOtherField('waterNumber'),
        Validators.maxLength(4),
      ],
    },
    {
      columnId: 'waterNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      searchValidators: [
        WRISValidators.updateValidityOfOtherField('basin'),
        WRISValidators.requireOtherFieldIfNonNull('basin'),
        Validators.maxLength(10),
      ],
    },
  ];

  public createColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'areaId',
      title: 'Enf Area #',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(10)],
    },
    {
      columnId: 'name',
      title: 'Enf Name',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.required, Validators.maxLength(60)],
    },
  ];

  public initFunction(): void {
    this.dataMessage = 'Search for or Create a New Enforcement Project';
  }

  private redirectToEnforcementsEditScreen(areaId: string) {
    void this.router.navigate([areaId], { relativeTo: this.activatedRoute });
  }

  protected _getHelperFunction(data?: any): any {
    if (data.get?.results?.length === 1 && data.get.currentPage === 1) {
      this.redirectToEnforcementsEditScreen(
        data.get.results[0].enforcementArea
      );
    }
    return data.get;
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToEnforcementsEditScreen(data?.enforcementArea);
  }

  public onInsert(data: any = {}): void {
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: '400px',
      data: {
        title: 'Create New Enforcement Area',
        columns: this.createColumns,
        values: data,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this.service.insert(result).subscribe(
          (savedEnforcement: Enforcement) => {
            this.redirectToEnforcementsEditScreen(savedEnforcement.areaId);
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error as ErrorBodyInterface;
            let message = 'Cannot insert new record. ';
            message += errorBody.userMessage || ErrorMessageEnum.POST;
            this.snackBar.open(message);
            this.onInsert(result);
          }
        );
      }
    });
  }
}
