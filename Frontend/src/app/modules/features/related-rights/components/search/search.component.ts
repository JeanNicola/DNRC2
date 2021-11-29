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
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { RelatedRightsService } from '../../services/related-rights.service';
import { CreateComponent } from '../create/create.component';
import { ReviewInformationComponent } from './components/review-information/review-information.component';
import { RelationshipTypesService } from './services/relationship-types.service';

@Component({
  selector: 'app-search',
  templateUrl:
    '../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../shared/components/templates/code-table/code-table.template.scss',
    './search.component.scss',
  ],
  providers: [RelatedRightsService, RelationshipTypesService],
})
export class SearchComponent extends BaseCodeTableComponent {
  constructor(
    public service: RelatedRightsService,
    public relationshipTypesService: RelationshipTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'relationshipType',
      title: 'Relationship Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'relatedRightId',
      title: 'Related Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'relationshipTypeVal',
      title: 'Relationship Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      searchValidators: [WRISValidators.requireOtherFieldIfNonNull('ext')],
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      searchValidators: [
        WRISValidators.updateValidityOfOtherField('waterRightNumber'),
      ],
    },
    {
      columnId: 'more',
      title: 'Water Rights',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'waterRightsCount',
      displayInEdit: false,
      displayInInsert: false,
      displayInSearch: false,
      noSort: true,
      width: 50,
    },
  ];

  public title = 'Related Rights';
  public primarySortColumn = 'relatedRightId';
  public sortDirection = 'asc';
  public hideActions = true;
  public hideEdit = true;
  public hideDelete = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public dblClickableRow = true;

  public initFunction(): void {
    this.dataMessage = 'Search for or Create a New Related Right';
  }

  private redirectToRelatedRightsEdit(relatedRightId: number) {
    void this.router.navigate([relatedRightId], {
      relativeTo: this.route,
    });
  }

  protected _getHelperFunction(data?: any): any {
    if (data.get?.results?.length === 1 && data.get.currentPage === 1) {
      this.redirectToRelatedRightsEdit(data.get.results[0].relatedRightId);
    }
    return {
      ...data.get,
      results: data.get.results,
    };
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToRelatedRightsEdit(data.relatedRightId);
  }

  protected _displayMoreInfoDialog(row: number): void {
    // Open the dialog
    const dialogRef = this.dialog.open(ReviewInformationComponent, {
      width: '900px',
      data: {
        values: {
          relatedRightId: this.rows[row].relatedRightId,
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

  public populateDropdowns(): void {
    this.observables.relationshipType = new ReplaySubject(1);

    this.relationshipTypesService
      .get(this.queryParameters)
      .subscribe((relationshipTypes) => {
        relationshipTypes.results.unshift({
          value: null,
          description: null,
        });
        this._getColumn('relationshipType').selectArr =
          relationshipTypes.results.map(
            (type: { value: string; description: string }) => ({
              name: type.description,
              value: type.value,
            })
          );
        this.observables.relationshipType.next(relationshipTypes);
        this.observables.relationshipType.complete();
      });
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(CreateComponent, {
      width: '700px',
      data: {
        columns: this.columns,
        values: data,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this.service.insert(result).subscribe((savedResult) => {
          this.snackBar.open('Related Right saved successfully!');
          void this.router.navigate([savedResult.relatedRightId], {
            relativeTo: this.route,
          });
        });
      } else {
        this.firstSearch.focus();
      }
    });
  }

  // Handle the onCellClick event
  public cellClick(data: any): void {
    if (data?.columnId === 'more') {
      this._displayMoreInfoDialog(data.row);
    }
  }
}
