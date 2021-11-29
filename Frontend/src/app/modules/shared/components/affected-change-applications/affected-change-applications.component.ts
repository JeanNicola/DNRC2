import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { PermissionsInterface } from '../../interfaces/permissions.interface';
import { AffectedWaterRightsService } from '../affected-water-rights/services/affected-water-rights.service';
import { DeleteDialogComponent } from '../dialogs/delete-dialog/delete-dialog.component';
import { AddApplicationDialogComponent } from './components/add-application-dialog/add-application-dialog.component';
import { AffectedChangeApplicationsService } from './services/affected-change-applications.service';

@Component({
  selector: 'app-affected-change-applications',
  templateUrl: '../templates/code-table/code-table.template.html',
  styleUrls: [
    '../templates/code-table/code-table.template.scss',
    './affected-change-applications.component.scss',
  ],
  providers: [AffectedWaterRightsService],
})
export class AffectedChangeApplicationsComponent
  extends BaseCodeTableComponent
  implements OnChanges, OnDestroy
{
  constructor(
    public service: AffectedChangeApplicationsService,
    public waterRightsService: AffectedWaterRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Events
  @Output() dataLoaded = new EventEmitter();

  // Inputs
  public onWaterRightDeleteSub$: Subscription;
  @Input() onWaterRightDeleteObservable: Observable<any>;

  @Input() focusFirstElementOnInit = true;
  @Input() title = 'Affected Change Applications';
  @Input() containerStyles = {
    width: '100%',
    height: 'fit-content',
    margin: '20px 0',
  };
  @Input() titleStyles = {
    fontSize: '16px',
  };
  @Input() ownerUpdateId;
  @Input() data;
  @Input() rows;
  @Input() ouWasProcessed = false;
  @Input() hideInsert = false;
  @Input() permissions: PermissionsInterface = {
    canGET: false,
    canPOST: false,
    canDELETE: false,
    canPUT: false,
  };
  @Input() hideDelete = false;
  @Input() hideActions = false;

  public hideEdit = true;
  public searchable = false;
  protected clickableRow = false;
  protected dblClickableRow = true;
  public isInMain = false;

  public primarySortColumn = 'id';
  public sortDirection = 'asc';

  public ngOnChanges(changes: SimpleChanges): void {
    // First time through the value is undefined so we're still loading
    // Next time through either we have a value or no value was returned (null)
    if (changes.ouWasProcessed?.currentValue) {
      this.hideInsert = true;
      this.hideActions = true;
      this.hideDelete = true;
    } else if (changes.ouWasProcessed?.currentValue === false) {
      this.hideInsert = false;
      this.hideActions = false;
      this.hideDelete = false;
    }
    if (!changes.ownerUpdateId && changes.onWaterRightDeleteObservable) {
      return;
    }
    if (!changes.ownerUpdateId && changes.ouWasProcessed) {
      return;
    }

    if (changes.ownerUpdateId?.currentValue === undefined) {
      this.dataMessage = 'Loading...';
    } else if (changes.ownerUpdateId?.currentValue === null) {
      this.dataMessage = 'No data found';
    } else if (changes.ownerUpdateId) {
      this.idArray = [changes.ownerUpdateId.currentValue];
      if (this.onWaterRightDeleteObservable) {
        this.onWaterRightDeleteSub$ =
          this.onWaterRightDeleteObservable.subscribe(() => {
            this.service.deleteAll(...this.idArray).subscribe(() => {
              this._get();
            });
          });
      }
      this._get();
    }
  }

  public ngOnDestroy(): void {
    if (this.onWaterRightDeleteSub$) {
      this.onWaterRightDeleteSub$.unsubscribe();
    }
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}

  /*
   * A function to modify GET data before use.
   */
  protected _getHelperFunction(data: any): any {
    // Emits event when the data is loaded
    this.dataLoaded.emit(data);
    return data.get;
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'id',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected _displayInsertDialog(): void {
    const dialogRef = this.dialog.open(AddApplicationDialogComponent, {
      data: {
        title: 'Add New Change Application',
        values: {
          ownershipUpdateId: this.ownerUpdateId,
        },
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const applicationIds = this.rows.map((app) => app.id);

        const resultsToInsert = result
          .filter((appl) => applicationIds.indexOf(appl.applicationId) === -1)
          .map((appl) => appl.applicationId);

        if (resultsToInsert.length) {
          this._insert({ applicationIds: resultsToInsert });
        }
      }
      if (this.rows?.length) {
        this.focusRowByIndex(0);
      }
    });
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [this.ownerUpdateId, this.rows[rowNumber].id];
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._delete(row);
      }
      if (this.rows?.length) {
        this.focusRowByIndex(0);
      }
    });
  }

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    this.router.navigate(['wris', 'applications', data.id]);
  }
}
