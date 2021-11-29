import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { EnforcementsService } from 'src/app/modules/shared/services/enforcements.service';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [EnforcementsService],
})
export class EditHeaderComponent extends DataRowComponent {
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();
  public error;

  constructor(
    public service: EnforcementsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
    this.titleService.setTitle(
      `WRIS - Enforcements: ${this.route.snapshot.params.areaId}`
    );
  }

  public _getHelperFunction(data: any) {
    this.dataEvent.emit(data);

    return {
      ...data.get,
    };
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'areaId',
      title: 'Enf Area',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'name',
      title: 'Enf Name',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public reportTitle = 'Enforcement Reports';

  public reports: ReportDefinition[] = [
    {
      title: 'Enforcement Index by Ditches',
      reportId: 'WRD3075ER',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_ENFA = data.areaId;
        report.params.P_PRINT_COMM = 'Y';
        report.params.P_PRINT_PRIORITY = 'N';
        report.params.P_PRINT_OWNER = 'N';
        report.params.PARAMFORM = 'NO';
      },
    },
    {
      title: 'Enforcement Index by Priority',
      reportId: 'WRD3070ER',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_ENFA = data.areaId;
        report.params.P_PRINT_PRIORITY = 'Y';
        report.params.P_PRINT_COMM = 'N';
        report.params.P_PRINT_OWNER = 'N';
        report.params.PARAMFORM = 'NO';
      },
    },

    {
      title: 'Enforcement Index by Owners',
      reportId: 'WRD3080ER',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_ENFA = data.areaId;
        report.params.P_PRINT_OWNER = 'Y';
        report.params.P_PRINT_COMM = 'N';
        report.params.P_PRINT_PRIORITY = 'N';
        report.params.PARAMFORM = 'NO';
      },
    },
  ];

  public initFunction() {
    this.idArray = [this.route.snapshot.params.areaId];
    this._get();
  }

  public _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Enforcement not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }
}
