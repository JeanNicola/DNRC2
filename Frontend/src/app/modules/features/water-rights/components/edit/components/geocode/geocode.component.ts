import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Component, Input, SecurityContext } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { MoreInfoDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/more-info-dialog/more-info-dialog.component';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { ReportUrlService } from 'src/app/modules/shared/components/reports/reports-dialog/services/report-url.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { SeverGeocodesService } from '../../../../services/geocode-sever.service';
import { UnresolveGeocodesService } from '../../../../services/geocode-unresolve.service';
import { GeocodesService } from '../../../../services/geocodes.service';
import { InsertGeocodesDialogComponent } from './components/insert-geocodes-dialog/insert-geocodes-dialog.component';
import { UpdateGeocodeDialogComponent } from './components/update-geocode-dialog/update-geocode-dialog.component';

@Component({
  selector: 'app-geocode',
  templateUrl: './geocode.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './geocode.component.scss',
  ],
  providers: [
    GeocodesService,
    UnresolveGeocodesService,
    SeverGeocodesService,
    ReportUrlService,
    SessionStorageService,
  ],
})
export class GeocodeComponent extends BaseCodeTableComponent {
  constructor(
    public service: GeocodesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private unresolveService: UnresolveGeocodesService,
    private severService: SeverGeocodesService,
    private reportUrlService: ReportUrlService,
    private sessionStorage: SessionStorageService,
    private sanitizer: DomSanitizer
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() headerData: any = null;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'xrefId',
      title: 'Geocode Xref Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'geocodeId',
      title: 'Geocode #',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
      validators: [Validators.required, WRISValidators.isGeocode],
      placeholder: '00-0000-00-0-00-00-0000',
    },
    {
      columnId: 'formattedGeocode',
      title: 'Geocode #',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      width: 200,
      editable: false,
    },
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
      width: 100,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      validators: [
        WRISValidators.afterOtherField('beginDate', 'Begin Date'),
        WRISValidators.dateBeforeToday,
      ],
      width: 100,
    },
    {
      columnId: 'comments',
      title: 'Comment',
      type: FormFieldTypeEnum.TextArea,
      width: 200,
      validators: [Validators.maxLength(4000)],
    },
    {
      columnId: 'valid',
      title: 'Valid',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'unresolved',
      title: 'Unresolved',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'sever',
      title: 'Sever/Sell',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'createdBy',
      title: 'Created By',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
      displayInTable: false,
      noSort: true,
    },
    {
      columnId: 'createdDate',
      title: 'Created By Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
      displayInInsert: false,
      displayInTable: false,
      noSort: true,
    },
    {
      columnId: 'modifiedBy',
      title: 'Modified By',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
      displayInTable: false,
      noSort: true,
    },
    {
      columnId: 'modifiedDate',
      title: 'Modified By Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
      displayInInsert: false,
      displayInTable: false,
      noSort: true,
    },
  ];

  public data: DataPageInterface<any> & {
    allSevered: boolean;
    allUnresolved: boolean;
    allValid: boolean;
    geocodeUrl: string;
    nrisUrl: string;
    mapVersionNumber: number;
  } = null;
  public title = '';
  public searchable = false;
  public isInMain = true;
  public primarySortColumn = 'formattedGeocode';
  public sortDirection = 'desc';
  protected dialogWidth = '600px';
  public enableMoreInfo = true;
  public dblClickableRow = true;
  private regularReportURL;

  // Override the initial focus
  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}

  public initFunction(): void {
    this.getUrl().subscribe((response) => {
      response.results.map((ref) => {
        if (ref.value.includes('SSRS_REPORT_PREFIX')) {
          this.regularReportURL = ref.description;
        }
      });
      this._get();
    });
  }

  private getUrl(): Observable<any> {
    const queryParameters: any = {
      filters: {
        env: this.sessionStorage.dbEnvironment,
      },
    };
    return this.reportUrlService.get(queryParameters);
  }

  public buildUrl(geocode: string): string {
    if (this.regularReportURL === null) {
      return '';
    }

    let params: HttpParams = new HttpParams();

    params = params.set('rs:Command', 'Render');
    params = params.set('P_GOCD_ID_SEQ', geocode);
    params = params.set('rs:Format', 'PDF');
    return this.sanitizer.sanitize(
      SecurityContext.URL,
      `${this.regularReportURL}${'WRD2090AR'}&${params.toString()}`
    );
  }

  protected _getHelperFunction(data: any): any {
    return {
      ...data.get,
      results: data.get.results.map((geocode) => ({
        ...geocode,
        geocodeReportUrl: this.buildUrl(geocode.geocodeId),
      })),
    };
  }

  // Handle the OnInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkDecree(
      this.headerData.isDecreed,
      this.headerData.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertGeocodesDialogComponent, {
      width: null,
      data: {
        title: 'Add New Geocodes',
        columns: this.columns,
        tableValues: this.data,
        values: data,
        idArray: this.idArray,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result: any[]) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });
  }

  protected _buildInsertDto(data: any[]): any {
    return {
      newGeocodes: data.map((geocode: any) => {
        const g = { ...geocode };
        delete g.formattedGeocode;
        g.geocodeId = g.geocodeId.replace(/-/g, '');
        return g;
      }),
    };
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.xrefId];
  }

  // Handle the onEdit event
  public onEdit(data: any): void {
    WaterRightsPrivileges.checkDecree(
      this.headerData.isDecreed,
      this.headerData.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, data)
    );
  }
  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateGeocodeDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Geocode',
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }

  // Handle the OnDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkDecree(
      this.headerData.isDecreed,
      this.headerData.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }

  protected _displayDeleteDialog(row: number): void {
    const message = this.rows[row]?.valid
      ? 'This is a <strong>Valid</strong> Geocode. Are you sure you want to delete this record?'
      : null;
    const title = this.rows[row]?.valid ? 'Delete this Valid Geocode' : null;

    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
      data: {
        message,
        title,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._delete(row);
      }
    });
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].xrefId];
  }

  // Handle the OnDeleteAll event
  public onDeleteAll(): void {
    WaterRightsPrivileges.checkDecree(
      this.headerData.isDecreed,
      this.headerData.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteAllDialog.bind(this)
    );
  }

  protected _displayDeleteAllDialog(): void {
    this.service.delete(...this.idArray).subscribe(
      () => {
        this._get();
        this.snackBar.open('All Invalid Geocodes successfully deleted.');
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot delete record. ';
        message += errorBody.userMessage || ErrorMessageEnum.DELETE;
        this.snackBar.open(message);
      }
    );
  }

  // Handle the OnSever event
  public onSever(): void {
    WaterRightsPrivileges.checkDecree(
      this.headerData.isDecreed,
      this.headerData.isEditableIfDecreed,
      this.dialog,
      this.doSever.bind(this)
    );
  }

  private doSever(): void {
    this.severService
      .sever({}, ...this.idArray) // just a basic post
      .subscribe(
        () => {
          this._get();
          this.snackBar.open('All Geocodes successfully severed.');
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
  }

  // Handle the OnSever event
  public onUnresolve(): void {
    WaterRightsPrivileges.checkDecree(
      this.headerData.isDecreed,
      this.headerData.isEditableIfDecreed,
      this.dialog,
      this.doUnresolve.bind(this)
    );
  }

  public doUnresolve(): void {
    this.unresolveService
      .unresolve({}, ...this.idArray) // just a basic post
      .subscribe(
        () => {
          this._get();
          this.snackBar.open('All Geocodes successfully unresolved.');
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
  }

  public moreInfoHandler(row: number) {
    this.dialog.open(MoreInfoDialogComponent, {
      width: '700px',
      data: {
        title: 'Geocodes',
        columns: [
          this._getColumn('createdBy'),
          this._getColumn('createdDate'),
          this._getColumn('modifiedBy'),
          this._getColumn('modifiedDate'),
        ],
        values: this.rows[row],
      },
    });
  }

  public onRowDoubleClick(data: any): void {
    window.open(this.data.geocodeUrl + data.geocodeId);
  }

  public onNRISMap(): void {
    window.open(
      `${this.data.nrisUrl}${this.headerData.waterRightId}-${
        this.data.mapVersionNumber
      }&wrn=${this.headerData.basin} ${this.headerData.waterRightNumber}${
        this.headerData.extension ? ' ' + this.headerData.extension : ''
      }`
    );
  }
}
