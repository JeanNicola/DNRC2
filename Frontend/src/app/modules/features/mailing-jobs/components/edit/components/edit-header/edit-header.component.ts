import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  SecurityContext,
  ViewChild,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { DomSanitizer, Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { ReportUrlService } from 'src/app/modules/shared/components/reports/reports-dialog/services/report-url.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { MailingJobsService } from '../../../../services/mailing-jobs.service';
import { ApplicationSelectDialogComponent } from '../application-select-dialog/application-select-dialog.component';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './edit-header.component.scss',
  ],
  providers: [MailingJobsService, ReportUrlService],
})
export class EditHeaderComponent extends DataRowComponent implements OnDestroy {
  constructor(
    public service: MailingJobsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public router: Router,
    private sessionStorage: SessionStorageService,
    private reportUrlService: ReportUrlService,
    private sanitizer: DomSanitizer,
    private route: ActivatedRoute,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
    this.titleService.setTitle(
      `WRIS - Mailing Labels: ${this.route.snapshot.params.id}`
    );
  }

  @Input() reloadHeader: Observable<any> = null;

  @Output() errorEvent: EventEmitter<HttpErrorResponse> =
    new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();

  public error = false;
  private unsubscribe = new Subject();

  public firstColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'mailingJobNumber',
      title: 'Mail Job #',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
      displayInEdit: false,
    },
    {
      columnId: 'mailingJobHeader',
      title: 'Mail Label Header',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'generatedDate',
      title: 'Generated Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
    },
  ];
  public secondColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'applicationTypeDescription',
      title: 'Application Type Description',
      type: FormFieldTypeEnum.Input,
      width: 800,
      displayInEdit: false,
      displayInSearch: false,
    },
  ];
  public columns = [...this.firstColumns, ...this.secondColumns];
  private baseReportUrl: string;
  public disableGenerate = false;
  @ViewChild('editApplicationButton', { static: false })
  editApplicationButton: MatButton;

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public initFunction() {
    this._get();
    this.reloadHeader?.pipe(takeUntil(this.unsubscribe)).subscribe(() => {
      void this._get();
    });
  }

  public _getHelperFunction(data: any) {
    this.dataEvent.emit(data);
    this.report.params.P_MAIL_LABEL_ID = data.get.mailingJobNumber;
    this.disableGenerate = !data.get.canGenerateLabels;
    this.disableEdit = data.get.generatedDate !== undefined;
    return data.get;
  }

  protected _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Mailing Job not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }

  protected _buildEditDto(originalData: any, editedData: any): any {
    return { ...editedData, applicationId: originalData.applicationId };
  }

  protected _displayEditDialog(data?: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Mail Label Header',
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(this._buildEditDto(data, result));
      }
      this.editButton.focus();
    });
  }

  public onApplicationEdit(): void {
    this._displayApplicationEditDialog(this.data);
  }

  protected _buildApplicationEditDto(originalData: any, editedData: any): any {
    return {
      applicationId: editedData.applicationId,
      mailingJobHeader: `PN-${editedData.basin}-${editedData.applicationId}`,
    };
  }

  protected _displayApplicationEditDialog(data?: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(ApplicationSelectDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update the Application #',
        columns: this.secondColumns,
        values: data,
        mode: DataManagementDialogModes.Update,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(this._buildApplicationEditDto(data, result));
      }
      this.editApplicationButton.focus();
    });
  }

  protected clickCell(column: ColumnDefinitionInterface) {
    if (column.columnId === 'applicationId' && this.data?.applicationId) {
      void this.router.navigate([
        'wris',
        'applications',
        this.data.applicationId,
      ]);
    }
  }

  public onDelete(): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
      data: {
        message:
          'This will delete the <strong>entire</strong> Mail Job including all references to Water Rights and Contacts',
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._delete();
      }
    });
  }

  public _delete(): void {
    this.service.delete(...this.idArray).subscribe(
      () => {
        this._get();
        this.snackBar.open('Record successfully deleted.');
        void this.router.navigate(['wris', 'mailing-jobs']);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot delete record. ';
        message += errorBody.userMessage || ErrorMessageEnum.DELETE;
        this.snackBar.open(message);
      }
    );
  }

  // Generate Labels Logic
  public report: ReportDefinition = {
    title: 'Mailing Labels',
    reportId: 'MAILING_JOBS',
    params: {},
  };

  public onGenerateLabels(): void {
    this.service.insert({}, ...this.idArray).subscribe(
      () => {
        this._get();
        const url = this.buildLabelUrl();
        window.open(url, '_blank');
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update Generate Labels. ';
        message += errorBody.userMessage;
        this.snackBar.open(message);
      }
    );
  }

  protected populateDropdowns(): void {
    this.reportUrlService
      .get({
        filters: {
          env: this.sessionStorage.dbEnvironment,
        },
      })
      .subscribe((response) => {
        this.baseReportUrl = response.results.find((ref) =>
          ref.value.includes('SSRS_REPORT_PREFIX')
        ).description;
      });
  }

  private buildLabelUrl(): string {
    let params: HttpParams = new HttpParams();
    Object.entries(this.report.params).forEach((k) => {
      params = params.set(k[0], k[1]);
    });
    params = params.set('rs:Command', 'Render');
    params = params.set('rs:Format', 'EXCEL');
    return this.sanitizer.sanitize(
      SecurityContext.URL,
      `${this.baseReportUrl}${this.report.reportId}&${params.toString()}`
    );
  }
}
