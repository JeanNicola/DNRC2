import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, ReplaySubject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { RelatedRightsService } from '../../../../services/related-rights.service';
import { RelationshipTypesService } from '../../../search/services/relationship-types.service';
import { ResetRelatedElementsComponent } from './components/reset-related-elements/reset-related-elements.component';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    './edit-header.component.scss',
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [RelatedRightsService, RelationshipTypesService],
})
export class EditHeaderComponent extends DataRowComponent implements OnDestroy {
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();
  @Input() reloadHeaderData: Observable<any> = null;
  private reloadHeaderData$: Subscription;
  private sendReloadDataEvent = true;
  public error;
  public dialogWidth = '400px';
  public data;

  constructor(
    public service: RelatedRightsService,
    public relationshipTypesService: RelationshipTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
    this.titleService.setTitle(
      `WRIS - Related Rights: ${this.route.snapshot.params.id}`
    );
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'relatedRightId',
      title: 'Related Right #',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
      displayInEdit: false,
      width: 160,
    },
    {
      columnId: 'relationshipType',
      title: 'Relationship Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'relationshipTypeVal',
      title: 'Relationship Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInSearch: false,
      width: 300,
    },
  ];

  public reportTitle = 'Related Rights Reports';

  public reports: ReportDefinition[] = [
    {
      title: 'General Abstract',
      reportId: 'WRD2090AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_RLRT_ID_SEQ = data.relatedRightId;
      },
    },
    {
      title: 'Supplemental Rights Worksheet',
      reportId: 'WRD3011R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_RLRT_ID_SEQ = data.relatedRightId;
      },
    },
  ];

  public _getHelperFunction(data: any) {
    if (this.sendReloadDataEvent) {
      this.dataEvent.emit(data);
    }
    this.sendReloadDataEvent = true;
    return {
      ...data.get,
    };
  }

  public initFunction() {
    this.idArray = [this.route.snapshot.params.id];
    this._get();
    if (this.reloadHeaderData) {
      this.reloadHeaderData$ = this.reloadHeaderData.subscribe(() => {
        this.sendReloadDataEvent = false;
        this._get();
      });
    }
  }

  public ngOnDestroy() {
    super.ngOnDestroy();
    if (this.reloadHeaderData$) {
      this.reloadHeaderData$.unsubscribe();
    }
  }

  public _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Related Right not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Related Right Record',
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (
          result.relationshipType === 'MULT' &&
          this.data.hasRelatedElements
        ) {
          const clearRelatedElementsDialog = this.dialog.open(
            ResetRelatedElementsComponent,
            {
              width: '500px',
            }
          );
          clearRelatedElementsDialog.afterClosed().subscribe((r) => {
            if (r === 'reset') {
              this._update(result);
            }
          });
        } else {
          this._update(result);
        }
      }
    });
  }

  public populateDropdowns(): void {
    this.observables.ownershipUpdateType = new ReplaySubject(1);

    this.relationshipTypesService
      .get(this.queryParameters)
      .subscribe((relationshipTypes) => {
        this._getColumn('relationshipType').selectArr =
          relationshipTypes.results.map(
            (type: { value: string; description: string }) => ({
              name: type.description,
              value: type.value,
            })
          );
        this.observables.ownershipUpdateType.next(relationshipTypes);
        this.observables.ownershipUpdateType.complete();
      });
  }

  public deleteRelatedRight() {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
      data: {
        title: 'Delete Related Right',
        message: `WARNING - This action will delete the <strong>Related Right</strong> and all
        the currently selected <strong>Related Elements</strong> and
        <strong>Water Rights</strong>. Are you sure you want to continue?`,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this.service.delete(this.data.relatedRightId).subscribe(() => {
          this.snackBar.open('Record successfully deleted.');
          void this.router.navigate(['..'], {
            relativeTo: this.route,
          });
        });
      }
    });
  }
}
