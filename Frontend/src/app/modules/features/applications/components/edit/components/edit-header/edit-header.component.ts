import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ApplicationsService } from '../../../../services/applications.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { ApplicationTypesService } from 'src/app/modules/features/code-tables/components/event-types/services/application-types.service';
import { Observable, ReplaySubject, Subscription, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ApplicationEditDialogComponent } from './components/application-edit-dialog.component';
import { ActivatedRoute } from '@angular/router';
import { Validators } from '@angular/forms';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { EditApplicationInterface } from '../../edit.component';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { HttpErrorResponse } from '@angular/common/http';
import { BasinsService } from 'src/app/modules/shared/services/basins.service';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import * as moment from 'moment';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './edit-header.component.scss',
  ],
  providers: [ApplicationsService, ApplicationTypesService, BasinsService],
})
export class EditHeaderComponent extends DataRowComponent implements OnDestroy {
  @Input() title;
  @Input() removeFieldsPadding = false;
  @Input() reloadHeaderData: Observable<any> = null;

  private unsubscribe = new Subject();

  public paging = false;
  public dialogWidth = '600px';
  public error = false;

  initialTypeArr: SelectionInterface[] = [];

  @Output()
  dataEvent: EventEmitter<EditApplicationInterface> = new EventEmitter<EditApplicationInterface>();
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Autocomplete,
      validators: [Validators.required],
      fontWeight: 700,
      width: 80,
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
      fontWeight: 700,
      editable: false,
      width: 125,
    },
    {
      columnId: 'applicationTypeCode',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'applicationTypeDescription',
      title: 'Application Type Description',
      type: FormFieldTypeEnum.TextArea,
      displayInEdit: false,
      width: 700,
    },
    {
      columnId: 'dateTimeReceived',
      title: 'Date/Time Received',
      type: FormFieldTypeEnum.DateTime,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
      editable: false,
      width: 175,
    },
  ];

  public reportTitle = 'Application Reports';

  public reports: ReportDefinition[] = [
    {
      title: 'Application Party List - NA',
      reportId: 'WRD3020R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        ['105', '600', '602', '605', '606', '610', '638', '650'].includes(
          data.applicationTypeCode
        ) && data.caseReport,
    },
    {
      title: 'Certificate of Water Right',
      reportId: 'WRD2090R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        data.issued &&
        !['600', '605', '651'].includes(data.applicationTypeCode),
    },
    {
      title: 'Change Authorization',
      reportId: 'WRD2080R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        data.issued &&
        ['606', '634', '635', '644', '650'].includes(data.applicationTypeCode),
    },
    {
      title: 'Change Authorization General Abstract',
      reportId: 'WRD2080AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        ['606', '634', '635', '644', '650'].includes(data.applicationTypeCode),
    },
    {
      title: 'Exempt Water Right Acknowledgment',
      reportId: 'WRD2050R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean => data.applicationTypeCode === '627',
    },
    {
      title: 'General Abstract',
      reportId: 'WRD2090AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
    },
    {
      title: '',
      reportId: 'WRD2071R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.title = `Pending Application ${data.applicationTypeCode} Notice of Receipt`;
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        !data.issued &&
        ['600', '638'].includes(data.applicationTypeCode) &&
        moment(data.dateTimeReceived).isAfter('2009-07-01'),
    },
    {
      title: '',
      reportId: 'WRD2091DR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.title = `Pending Application ${data.applicationTypeCode} Notice of Receipt`;
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        !data.issued &&
        ['606', '626', '644'].includes(data.applicationTypeCode) &&
        moment(data.dateTimeReceived).isAfter('2009-07-01'),
    },
    {
      title: 'Preliminary Application Party List - NA',
      reportId: 'WRD3021R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        ['105', '600', '602', '605', '606', '610', '638', '650'].includes(
          data.applicationTypeCode
        ) && data.caseReport,
    },
    {
      title: 'Provisional Permit',
      reportId: 'WRD2090BR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        data.issued && ['600', '605', '645'].includes(data.applicationTypeCode),
    },
    {
      title: 'Public Notice 606 Application',
      reportId: 'WRD2090DR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean => data.applicationTypeCode === '606',
    },
    {
      title: 'Public Notice 626 Application',
      reportId: 'WRD2075R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean => data.applicationTypeCode === '626',
    },
    {
      title: 'Public Notice 644 Application',
      reportId: 'WRD2076R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean => data.applicationTypeCode === '644',
    },
    {
      title: 'Public Notice 650 Application',
      reportId: 'WRD2077R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean => data.applicationTypeCode === '650',
    },
    {
      reportId: 'WRD2070R',
      title: '',
      setParams: (report: ReportDefinition, data: any): void => {
        report.title = `Public Notice ${data.applicationTypeCode} Application`;
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        ['600', '638'].includes(data.applicationTypeCode),
    },
    {
      reportId: 'WRD2070R_NP',
      title: '',
      setParams: (report: ReportDefinition, data: any): void => {
        report.title = `Public Notice ${data.applicationTypeCode} Application Newspaper`;
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        ['600', '638'].includes(data.applicationTypeCode),
    },

    {
      title: 'Public Notice Status',
      reportId: 'WRD3025R',
      setParams: (report: ReportDefinition, data: any): void => {},
    },
    {
      title: 'Water Reservation',
      reportId: 'WRD2090WR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean => data.applicationTypeCode === '638',
    },
    {
      title: 'Verification / Certification Abstract',
      reportId: 'WRD2060R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_APPL_ID_SEQ = data.applicationId;
      },
      isAvailable: (data: any): boolean =>
        ['600', '605', '606', '645'].includes(data.applicationTypeCode),
    },
  ];

  initFunction(): void {
    this.idArray = [this.route.snapshot.params.id];
    this._get();
    this.reloadHeaderData
      ?.pipe(takeUntil(this.unsubscribe))
      .subscribe(() => void this._get());
  }

  _getHelperFunction(data: any): any {
    this.error = false;
    const results: EditApplicationInterface =
      data.get as EditApplicationInterface;

    this.dataEvent.emit(results);

    // This code sets up the selectArr for Application Type Code
    // based on the allowed values.
    const typeCode = results.applicationTypeCode;
    if (typeCode === '600P') {
      this._getColumn('applicationTypeCode').selectArr =
        this.initialTypeArr.filter((item) =>
          ['600', '600P'].includes(item.value as string)
        );
    } else if (typeCode === '606P') {
      this._getColumn('applicationTypeCode').selectArr =
        this.initialTypeArr.filter((item) =>
          ['606', '606P'].includes(item.value as string)
        );
    } else {
      // Non-P types cannot be converted into P-types. Additionally, eight (8)
      // other types are no long allowed to be used when creating or editing
      // Applications. Some existing applications still exist with these types,
      // and this code will ensure that these types are all filtered out, unless
      // the Application is currently a retired type, allowing only that retired
      // type to be shown in the list.
      let filterArr = [
        '600P',
        '606P',
        '607',
        '608',
        '617',
        '618',
        '626',
        '627',
        '650',
        '651',
      ];

      // Exception if the Application is currently a retired type
      filterArr = filterArr.filter((item) => item !== typeCode);

      this._getColumn('applicationTypeCode').selectArr =
        this.initialTypeArr.filter(
          (item) => !filterArr.includes(item.value as string)
        );
    }
    return { ...results };
  }

  public _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Application not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }

  protected _getDisplayData(data: any): { [key: string]: any } {
    // Concatentate the application type code and the application type description for display
    if (
      data.applicationTypeCode !== undefined &&
      data.applicationTypeDescription !== undefined
    ) {
      data.applicationTypeDescription = `${data.applicationTypeCode} - ${data.applicationTypeDescription}`;
    }

    return { ...data };
  }

  public populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the getPrograms http request and the selectArr
    // population only happens once
    this.observables.appTypes = new ReplaySubject(1);
    this.applicationTypesService
      .getAll()
      .subscribe((appTypes: { results: ApplicationType[] }) => {
        this.initialTypeArr = appTypes.results.map(
          (appType: ApplicationType) => ({
            name: `${appType.code} - ${appType.description}`,
            value: appType.code,
          })
        );
        // this.onSearch();
        this.observables.appTypes.next(appTypes);
        this.observables.appTypes.complete();
      });

    this.observables.basins = new ReplaySubject(1);
    this.basinsService.getAll().subscribe((basins: { results: Basin[] }) => {
      this._getColumn('basin').selectArr = basins.results.map((basin) => ({
        value: basin.code,
        name: `${basin.code} - ${basin.description}`,
      }));
      this._getColumn('basin').validators.push(
        WRISValidators.matchToSelectArray(this._getColumn('basin').selectArr)
      );
      this.observables.basins.next(basins);
      this.observables.basins.complete();
    });
  }

  constructor(
    public service: ApplicationsService,
    public endpointService: EndpointsService,
    private sessionStorage: SessionStorageService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private applicationTypesService: ApplicationTypesService,
    private basinsService: BasinsService,
    private route: ActivatedRoute,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
    this.titleService.setTitle(
      `WRIS - Applications: ${this.route.snapshot.params.id}`
    );
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(ApplicationEditDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Application Record',
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(result);
      }
    });
  }

  public ngOnDestroy() {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}

export interface ApplicationType {
  code: string;
  description: string;
}

export interface Basin {
  code: string;
  description: string;
}
