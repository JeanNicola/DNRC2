import { Component, Input, OnDestroy } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Subject, BehaviorSubject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { HistoricalData, Historical } from '../../historical.component';
import { ChangesService } from './services/changes.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { ConfirmationDialogComponent } from 'src/app/modules/shared/components/dialogs/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-changes',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row-top-button.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './changes.component.scss',
  ],
  providers: [ChangesService],
})
export class ChangesComponent extends DataRowComponent implements OnDestroy {
  @Input() historical: BehaviorSubject<HistoricalData | null>;
  @Input() waterRightTypeCode?: string;
  private _versionTypeCode: string = undefined;
  @Input() set versionTypeCode(v: string) {
    this.disableEdit = !['CHAU', 'CHSP', 'REDX'].includes(v) || !this.canEdit;
    this._versionTypeCode = v;
  }
  get versionTypeCode(): string {
    return this._versionTypeCode;
  }

  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = true;

  private unsubscribe = new Subject();

  public showLoading = false;
  public title = 'Historical Data for Changes';
  public paging = false;
  public searchable = false;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'flowRate',
      title: 'Flow Rate',
      type: FormFieldTypeEnum.Input,
      validators: [
        WRISValidators.isNumber(8, 2),
        WRISValidators.updateValidityOfOtherField('flowRateUnit'),
      ],
      width: 130,
    },
    {
      columnId: 'flowRateUnit',
      title: 'Flow Rate Unit',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [WRISValidators.requireOtherFieldIfNonNull('flowRate')],
    },
    {
      columnId: 'flowRateUnitMeaning',
      title: 'Flow Rate Unit',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'divertedVolume',
      title: 'Diverted Volume',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      width: 130,
    },
    {
      columnId: 'consumptiveVolume',
      title: 'Consumptive Volume',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      width: 130,
    },
  ];

  constructor(
    public service: ChangesService,
    public endpointsService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointsService, dialog, snackBar);
  }

  protected initFunction() {
    const { waterRightId, versionId } = this.route.snapshot.params;
    this.idArray = [waterRightId, versionId];
    this.historical
      .pipe(takeUntil(this.unsubscribe), filter(Boolean))
      .subscribe(this._onGetSuccessHandler.bind(this));
  }

  protected _onGetSuccessHandler(data: HistoricalData): void {
    this.data = data.record;
    this.displayData = this._getDisplayData(data.record);

    this._getColumn('flowRateUnit').selectArr = [
      { name: '', value: '' },
      ...data.flowRateUnits
        .filter(({ value, description }) => value !== 'POF')
        .map(({ value, description }) => ({
          name: value,
          value,
        })),
    ];
  }

  protected _onPutSuccessHandler(data: Historical): void {
    this.data = data;
    this.displayData = this._getDisplayData(data);
  }

  protected _get(): void {}

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data?: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title}`,
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // If any of the fields have been entered, but not all, display message to user reminding them all fields are necessary
        if (
          result.consumptiveVolume === null ||
          result.divertedVolume === null ||
          result.flowRate === null ||
          result.flowRateUnit === null
        ) {
          const warningMessageDialog = this.dialog.open(
            ConfirmationDialogComponent,
            {
              width: '500px',
              data: {
                title: 'Edit Historical Data',
                message:
                  'All or one of the Historic Flow, Diverted Volume, or Consumptive Volume fields have NOT been Entered. ' +
                  'These fields need to be entered for all Change Authorizations. Is this information available?',
                confirmButtonName: 'Yes',
                cancelButtonName: 'No',
              },
            }
          );

          warningMessageDialog.afterClosed().subscribe((r) => {
            if (r === 'confirmed') {
              this._displayEditDialog({ ...data, ...result });
            } else {
              this._update(this._buildEditDto(data, result));
            }
          });
        } else {
          this._update(this._buildEditDto(data, result));
        }
      }
      this.editButton.focus();
    });
  }

  protected _update(values: any): void {
    this.service.update(values, ...this.idArray).subscribe(
      (response: Historical) => {
        this._onPutSuccessHandler(response);
        this.snackBar.open('Record successfully updated.', null);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update record. ';
        message += errorBody.userMessage || ErrorMessageEnum.PUT;
        this.snackBar.open(message);
        this._displayEditDialog(this.data);
      }
    );
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0),
    };
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }

  ngOnDestroy(): void {
    this.dialog.closeAll();
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
