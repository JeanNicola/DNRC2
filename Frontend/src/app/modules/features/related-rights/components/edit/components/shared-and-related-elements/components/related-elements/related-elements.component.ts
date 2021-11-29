import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Observable, ReplaySubject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { RelatedElementsService } from 'src/app/modules/shared/services/related-elements.service';
import { RelatedElementTypesService } from './services/related-element-types.service';

@Component({
  selector: 'app-related-elements',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './related-elements.component.scss',
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [RelatedElementsService, RelatedElementTypesService],
})
export class RelatedElementsComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  constructor(
    public service: RelatedElementsService,
    public relatedElementTypesService: RelatedElementTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() relatedRightId;
  @Input() reloadRelatedElements: Observable<any> = null;
  @Output() relatedElementsChanged: EventEmitter<void> =
    new EventEmitter<void>();

  private sendRelatedElementsChanged = true;
  public clickableRow = false;
  public dblClickableRow = false;
  public hideActions = false;
  public hideInsert = false;
  public isInMain = false;
  public searchable = false;
  public hideEdit = true;
  public title = 'Related Elements';
  public dataMessage = null;
  private reloadRelatedElements$: Subscription;
  public primarySortColumn = 'elementTypeValue';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'elementTypeValue',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'relatedRightId',
      title: 'Related Element Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInInsert: false,
    },
    {
      columnId: 'elementTypeValue',
      title: 'Element',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'elementType',
      title: 'Element',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
  ];

  protected initFunction(): void {
    this.idArray = [this.relatedRightId];
    this._get();
    if (this.reloadRelatedElements) {
      this.reloadRelatedElements$ = this.reloadRelatedElements.subscribe(
        (data) => {
          this.sendRelatedElementsChanged = false;
          if (data.relationshipType === 'MULT') {
            this.hideInsert = true;
            this.hideActions = true;
          } else {
            this.hideInsert = false;
            this.hideActions = false;
          }
        }
      );
    }
  }

  protected _getHelperFunction(data: any): any {
    if (this.sendRelatedElementsChanged) {
      this.relatedElementsChanged.next(null);
    }
    this.sendRelatedElementsChanged = true;
    return data.get;
  }

  public ngOnDestroy(): void {
    super.ngOnDestroy();
    if (this.reloadRelatedElements$) {
      this.reloadRelatedElements$.unsubscribe();
    }
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Add New Related Element Record',
        columns: this.columns,
        values: data,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      }
      this.firstInsert.focus();
    });
  }

  public populateDropdowns(): void {
    this.observables.ownershipUpdateType = new ReplaySubject(1);

    this.relatedElementTypesService
      .get(this.queryParameters)
      .subscribe((elementTypes) => {
        this._getColumn('elementType').selectArr = elementTypes.results.map(
          (elementType: { value: string; description: string }) => ({
            name: elementType.description,
            value: elementType.value,
          })
        );
        this.observables.ownershipUpdateType.next(elementTypes);
        this.observables.ownershipUpdateType.complete();
      });
  }

  protected _insert(newRow: any): void {
    newRow = {
      ...newRow,
      relatedRightId: this.relatedRightId,
    };
    this.service.insert(newRow, ...this.idArray).subscribe(
      (dto) => {
        let messages = ['Record successfully added.'];
        if (!!dto.messages) {
          messages = [...dto.messages, ...messages];
        }
        this.snackBar.open(messages.join('\n'));
        this._get();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot insert new record. ';
        message += errorBody.userMessage || ErrorMessageEnum.POST;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this._displayInsertDialog(newRow);
      }
    );
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].elementType];
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
