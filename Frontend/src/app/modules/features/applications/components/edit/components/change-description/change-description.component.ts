import { Component, Input, OnInit } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
// eslint-disable-next-line max-len
import { ChangeDescriptionConfirmDialogComponent } from './components/change-description-confirm-dialog/change-description-confirm-dialog.component';
// eslint-disable-next-line max-len
import { ChangeDescriptionUpdateDialogComponent } from './components/change-description-update-dialog/change-description-update-dialog.component';
import { ApplicationsChangeDescriptionService } from './services/applications-change-description.service';
import { CardinalDirectionsService } from './services/cardinal-directions.service';
import { DirectionsService } from './services/directions.service';

@Component({
  selector: 'app-change-description',
  templateUrl: './change-description.component.html',
  styleUrls: ['./change-description.component.scss'],
  providers: [
    ApplicationsChangeDescriptionService,
    CardinalDirectionsService,
    DirectionsService,
  ],
})
export class ChangeDescriptionComponent
  extends DataRowComponent
  implements OnInit
{
  private _appTypeCode: string;
  @Input() set appTypeCode(str: string) {
    this._appTypeCode = str;

    // Set various UI display and form conditions based on Application Type Code
    if (['606', '604', '626', '650', '105'].includes(this._appTypeCode)) {
      this.pastUseOfWater = true;
    } else if (['634', '644'].includes(this._appTypeCode)) {
      this.distanceAndDirection = true;
    }

    this.populateDropdowns2();
  }
  url = '/applications/{applicationId}/change';
  title = 'Change Description';
  dialogWidth = '600px';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'changeDescription',
      title: 'Proposed Change',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(4000)],
    },
    {
      columnId: 'distance',
      title: 'Distance from Original (feet)',
      type: FormFieldTypeEnum.Input,
      validators: [
        WRISValidators.integer,
        Validators.min(0),
        Validators.max(99999),
      ],
    },
    {
      columnId: 'direction',
      title: 'Direction from Original',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'directionName',
      title: 'Direction from Original',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'pastUse',
      title: 'Past Use of Water',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(4000)],
    },
    {
      columnId: 'additionalInformation',
      title: 'Additional Information',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(2000)],
    },
  ];

  public distanceAndDirection = false;
  public pastUseOfWater = false;

  constructor(
    public service: ApplicationsChangeDescriptionService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private directionsService: DirectionsService,
    private cardinalDirectionsService: CardinalDirectionsService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  initFunction(): void {
    this._get();
  }

  _getHelperFunction(data: { get: ChangeDescriptionInterface }): any {
    return { ...data.get };
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0),
    };
  }

  // Renaming this function ensures it does not get called
  // in ngOnInit
  public populateDropdowns2(): void {
    // create a separate observable that only emits the one value
    // this way, the getPrograms http request and the selectArr
    // population only happens once
    if (['634', '644'].includes(this._appTypeCode)) {
      if (this._appTypeCode === '634') {
        this.observables.directions = new ReplaySubject(1);
        this.cardinalDirectionsService
          .get(this.queryParameters)
          .subscribe(
            (directions: {
              results: { description: string; value: string }[];
            }) => {
              this._getColumn('direction').selectArr = directions.results.map(
                (item) => ({
                  name: item.description,
                  value: item.value,
                })
              );
              // Add a null value so the user can clear out the entry
              this._getColumn('direction').selectArr.unshift({
                name: '',
                value: '',
              });
              this.observables.directions.next(directions);
              this.observables.directions.complete();
            }
          );
      } else if (this._appTypeCode === '644') {
        this.observables.directions = new ReplaySubject(1);
        this.directionsService
          .get(this.queryParameters)
          .subscribe(
            (directions: {
              results: { description: string; value: string }[];
            }) => {
              this._getColumn('direction').selectArr = directions.results.map(
                (item) => ({
                  name: item.description,
                  value: item.value,
                })
              );
              // Add a null value so the user can clear out the entry
              this._getColumn('direction').selectArr.unshift({
                name: '',
                value: '',
              });
              this.observables.directions.next(directions);
              this.observables.directions.complete();
            }
          );
      }
    }
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(enteredData?: any): void {
    // If a user has already entered form data, reuse the entered form data.
    // If not, use the data from the initial http GET request
    let dataValues;
    if (enteredData) {
      dataValues = enteredData;
    } else {
      dataValues = this.data;
    }

    // Open the dialog
    const dialogRef = this.dialog.open(ChangeDescriptionUpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title} Record`,
        columns: this.columns,
        values: dataValues,
        distanceAndDirection: this.distanceAndDirection,
        pastUseOfWater: this.pastUseOfWater,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (
          this.distanceAndDirection &&
          (!result?.distance || !result?.direction)
        ) {
          this._displayConfirmDialog(result);
        } else {
          this._update(result);
        }
      }
    });
  }

  _displayConfirmDialog(data: any): void {
    const dialogRef = this.dialog.open(
      ChangeDescriptionConfirmDialogComponent,
      { width: '500px' }
    );

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'confirm') {
        this._update(data);
      } else if (result === null) {
        this._displayEditDialog(data);
      }
    });
  }
}

export interface ChangeDescriptionInterface {
  changeDescription?: string;
  distance?: string;
  direction?: string;
  pastUse?: string;
  additionalInformation: string;
}
