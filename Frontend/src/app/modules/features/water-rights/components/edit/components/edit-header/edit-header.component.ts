import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { ReplaySubject, Subject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { Basin } from 'src/app/modules/features/applications/components/edit/components/edit-header/edit-header.component';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import {
  ReportDefinition,
  ReportTypes,
} from 'src/app/modules/shared/interfaces/report-definition.interface';
import { BasinsService } from 'src/app/modules/shared/services/basins.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { SubBasinsService } from '../../../../services/sub-basins.service';
import { WaterRightTypesService } from '../../../../services/water-right-types.service';
import { WaterRightService } from '../../../../services/water-right.service';
import { ChildRightsDialogComponent } from './components/child-rights-dialog/child-rights-dialog.component';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './edit-header.component.scss',
  ],
  providers: [
    BasinsService,
    WaterRightService,
    WaterRightTypesService,
    SubBasinsService,
  ],
})
export class EditHeaderComponent extends DataRowComponent implements OnDestroy {
  constructor(
    public service: WaterRightService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private basinsService: BasinsService,
    private typesService: WaterRightTypesService,
    private subBasinsService: SubBasinsService,
    private router: Router,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();
  private _reloadHeader: Subject<void>;
  @Input() set reloadHeader(value: Subject<void>) {
    this._reloadHeader = value;
    this.reloadHeaderSubscription = this._reloadHeader.subscribe(() => {
      this._get();
    });
  }

  public error = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightId',
      title: 'Water Right Id',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Select,
      width: 100,
      fontWeight: 700,
      validators: [Validators.required],
    },
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
      displayInEdit: false,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      width: 80,
      displayInEdit: false,
    },
    {
      columnId: 'typeCode',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'typeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      width: 320,
      displayInEdit: false,
    },
    {
      columnId: 'statusCode',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      displayInEdit: false,
    },
    {
      columnId: 'statusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
      width: 300,
      displayInEdit: false,
    },
    {
      columnId: 'subBasin',
      title: 'Sub Basin',
      type: FormFieldTypeEnum.Select,
      width: 100,
      validators: [],
    },
    {
      columnId: 'dividedOwnership',
      title: 'Divided Ownership',
      type: FormFieldTypeEnum.Checkbox,
      width: 100,
    },
    {
      columnId: 'severed',
      title: 'Exempt (Reserved) or Severed from Land',
      type: FormFieldTypeEnum.Checkbox,
      width: 150,
    },
    {
      columnId: 'more',
      title: 'Child Rights',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'childRightCount',
      displayInEdit: false,
    },
  ];

  private reloadHeaderSubscription: Subscription;

  public reportTitle = 'Water Rights Reports';

  public reports: ReportDefinition[] = [
    {
      title: 'General Abstract',
      reportId: 'WRD2090AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
      },
    },
    {
      title: 'Scanned Documents',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.Basin = data.basin;
        report.params.WR_Number = data.waterRightNumber;
        report.params.Extension = data.ext ?? '';
        report.params.WR_Type = data.typeDescription;
      },
      type: ReportTypes.SCANNED,
    },
  ];

  public initFunction() {
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  protected _getHelperFunction(data: any): { [key: string]: any } {
    this.dataEvent.emit(data.get);
    this.titleService.setTitle(
      `WRIS - Water Right: ${data.get.basin} ${data.get.waterRightNumber} ${
        data.get.ext ?? ''
      }`
    );

    // Disable items if not an ADJ/PR watwer right
    if (
      !['HDRT', 'IRRD', 'ITSC', 'NNAD', 'PRDL', 'RSCL', 'STOC'].includes(
        data.get.typeCode
      ) ||
      (data.get.isDecreed && !data.get.isEditableIfDecreed)
    ) {
      this._getColumn('subBasin').editable = false;
    } else {
      this._getColumn('subBasin').editable = true;
    }

    // Disable items if the water Right is decreed
    this._getColumn('basin').editable = !(
      data.get.isDecreed && !data.get.isEditableIfDecreed
    );
    this._getColumn('typeCode').editable = !(
      data.get.isDecreed && !data.get.isEditableIfDecreed
    );

    return { ...data.get };
  }

  public populateDropdowns(): void {
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

    this.observables.subBasins = new ReplaySubject(1);
    this.subBasinsService
      .get(this.queryParameters)
      .subscribe((subBasins: { results: any[] }) => {
        this._getColumn('subBasin').selectArr = subBasins.results.map(
          (basin) => ({
            value: basin.code,
            name: `${basin.code}${
              basin.parent ? ' (Basin: ' + basin.parent + ')' : ''
            } - ${basin.description}`,
          })
        );
        this._getColumn('subBasin').selectArr.unshift({ value: '', name: '' });
        this._getColumn('subBasin').validators.push(
          WRISValidators.matchToSelectArray(
            this._getColumn('subBasin').selectArr
          )
        );
        this.observables.subBasins.next(subBasins);
        this.observables.subBasins.complete();
      });

    this.observables.types = new ReplaySubject(1);
    this.typesService
      .get(this.queryParameters)
      .subscribe((types: { results: any[] }) => {
        this._getColumn('typeCode').selectArr = types.results.map((type) => ({
          value: type.value,
          name: type.description,
        }));
        this._getColumn('typeCode').validators.push(
          WRISValidators.matchToSelectArray(
            this._getColumn('typeCode').selectArr
          )
        );
        this.observables.types.next(types);
        this.observables.types.complete();
      });
  }

  protected clickCell(column: ColumnDefinitionInterface): void {
    if (column.columnId === 'more') {
      const childRightsDialog = this.dialog.open(ChildRightsDialogComponent, {
        data: {
          waterRightId: this.data.waterRightId,
        },
      });
      childRightsDialog.afterClosed().subscribe((waterRightId) => {
        if (waterRightId != null) {
          void this.router.navigate(['wris', 'water-rights', waterRightId]);
        }
      });
    }
  }

  public _onGetErrorHandler(error: HttpErrorResponse): void {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Water Right not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkDecree(
      this.data.isDecreed,
      this.data.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Water Right',
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

  public ngOnDestroy(): void {
    this.reloadHeaderSubscription.unsubscribe();
  }
}
