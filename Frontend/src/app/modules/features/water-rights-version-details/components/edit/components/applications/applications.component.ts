import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  Output,
  SecurityContext,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { forkJoin, Observable } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { ReportUrlService } from 'src/app/modules/shared/components/reports/reports-dialog/services/report-url.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { VersionApplicationService } from './services/version-applications.service';

@Component({
  selector: 'app-applications',
  templateUrl: './applications.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './applications.components.scss',
  ],
  providers: [
    VersionApplicationService,
    ReportUrlService,
    SessionStorageService,
  ],
})
export class ApplicationsComponent extends BaseCodeTableComponent {
  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() showScannedUrl = false;
  @Input() waterRightBasin = null;
  @Output() applicationLoaded = new EventEmitter<void>();
  @Output() applicationChanged = new EventEmitter<void>();
  constructor(
    public service: VersionApplicationService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private reportUrlService: ReportUrlService,
    private sessionStorage: SessionStorageService,
    private sanitizer: DomSanitizer,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationType',
      title: 'Application Type',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'dateTimeReceived',
      title: 'Date/Time Received',
      type: FormFieldTypeEnum.DateTime,
      displayInInsert: false,
    },
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
    {
      columnId: 'applicant',
      title: 'Applicant',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
  ];
  public title = '';
  public primarySortColumn = 'applicationId';
  public sortDirection = 'asc';
  public searchable = false;
  public hideEdit = true;
  public dblClickableRow = true;
  private scannedReportURL;

  protected initFunction(): void {
    this.getUrl().subscribe((response) => {
      response.results.map((ref) => {
        if (ref.value.includes('FILENET_PREFIX')) {
          this.scannedReportURL = ref.description;
        }
      });
      this._get();
    });
  }

  protected _getHelperFunction(data: any): any {
    return {
      ...data.get,
      results: data.get.results.map((app) => ({
        ...app,
        scannedUrl: this.buildUrl(app.applicationId),
      })),
    };
  }

  protected _get(): void {
    this.dataMessage = 'Loading...';

    const service = this._getService();

    // Data is subscribed here so page does not "flicker" to "Loading" each time new page is requested
    forkJoin({
      get: service.get(this.queryParameters, ...this.idArray),
      ...this.observables,
    }).subscribe(
      (data) => {
        this.data = this._getHelperFunction(data);
        this.rows = this.data.results;

        if (data?.get?.results?.length) {
          this.dataMessage = null;
        } else {
          this.dataMessage = 'No data found';
        }

        // Focus the first row in the table
        this.setTableFocus();

        this.applicationLoaded.next();
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        const message = errorBody.userMessage || ErrorMessageEnum.GET;
        this.snackBar.open(message);
      },
      () => {
        this.dataMessage = 'No data found';
      }
    );
  }

  private getUrl(): Observable<any> {
    const queryParameters: any = {
      filters: {
        env: this.sessionStorage.dbEnvironment,
      },
    };
    return this.reportUrlService.get(queryParameters);
  }

  public buildUrl(applicationId: string): string {
    if (this.scannedReportURL === null) {
      return '';
    }

    let params: HttpParams = new HttpParams();

    params = params.set('Basin', this.waterRightBasin ?? '');
    params = params.set('WR_Number', applicationId);
    params = params.set('Extension', '');
    params = params.set('WR_Type', 'CHANGE AUTHORIZATION');
    return this.sanitizer.sanitize(
      SecurityContext.URL,
      `${this.scannedReportURL}${params.toString()}`
    );
  }

  // don't use a request body for inserts, use a path parameter instead
  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, and canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST:
        this.endpointService.canPOST(this.service.url, 1) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url) && this.canEdit,
    };
  }
  protected _buildInsertIdArray(dto: any): string[] {
    return [...this.idArray, dto.applicationId];
  }
  protected _buildInsertDto(data: any): any {
    return null;
  }
  protected _buildDeleteIdArray(rowNumber: number) {
    return [...this.idArray, this.rows[rowNumber].applicationId];
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.service
      .insert(this._buildInsertDto(newRow), ...this._buildInsertIdArray(newRow))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.applicationChanged.emit(null);
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

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  /*
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this._getDeleteService()
      .delete(...this._buildDeleteIdArray(row))
      .subscribe(
        () => {
          this._get();
          this.applicationChanged.emit(null);
          this.snackBar.open('Record successfully deleted.');
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'applications', data.applicationId]);
  }

  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
