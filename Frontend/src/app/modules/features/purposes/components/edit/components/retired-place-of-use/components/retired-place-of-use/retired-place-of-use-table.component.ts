/* eslint-disable max-len */
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Validators } from '@angular/forms';
import { EditMessageComponent } from 'src/app/modules/shared/components/dialogs/edit-message/edit-message.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { InsertUpdateAcreageComponent } from '../../../../../create/components/insert-update-acreage/insert-update-acreage.component';
import { EditPurposeHeaderComponent } from '../../../edit-header/edit-header.component';
import { PlaceOfUseComponent } from '../../../period-and-place-of-use/components/place-of-use/place-of-use.component';
import { CopyPousToRetiredService } from '../../../period-and-place-of-use/services/copy-pous-to-retired.service';
import { RetiredPlacesOfUseService } from '../../services/retired-places-of-use.service';
import { PurposeDropdownsService } from '../../../edit-header/services/purpose-dropdowns.service';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CopyPodsService } from '../../../period-and-place-of-use/services/copy-pods.service';
import { PodDropdownService } from 'src/app/modules/features/water-rights-version-details/components/edit/components/point-of-diversion/services/pod-dropdown.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

@Component({
  selector: 'app-retired-place-of-use-table',
  templateUrl: './retired-place-of-use-table.component.html',
  styleUrls: [
    '../../../period-and-place-of-use/components/place-of-use/place-of-use.component.scss',
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    { provide: BaseDataService, useClass: RetiredPlacesOfUseService },
    CopyPodsService,
    CopyPousToRetiredService,
  ],
})
export class RetiredPlaceOfUseTableComponent
  extends PlaceOfUseComponent
  implements OnInit
{
  constructor(
    public service: BaseDataService,
    public copyPodsService: CopyPodsService,
    public dropdownService: PurposeDropdownsService,
    public podDropdownService: PodDropdownService,
    public copyPousToRetiredService: CopyPousToRetiredService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public route: ActivatedRoute
  ) {
    super(
      service,
      copyPodsService,
      dropdownService,
      podDropdownService,
      copyPousToRetiredService,
      endpointService,
      dialog,
      snackBar,
      route
    );
  }

  public ngOnInit(): void {
    // Changes specific to the Retired Place Of Use
    this._getColumn('elementOrigin').title = 'Retired Origin';
    this._getColumn('elementOriginDescription').title = 'Retired Origin';

    super.afterInit();
  }

  protected setValidators(): void {
    this._getColumn('elementOrigin').validators = [
      Validators.required,
      EditPurposeHeaderComponent.originValidator(
        this.waterRightTypeCode,
        this.waterRightStatusCode
      ),
    ];
  }

  protected _displayInsertDialog(data): void {
    this.setValidators();
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdateAcreageComponent, {
      data: {
        title: 'Add New Retired Place Of Use Record',
        mode: DataManagementDialogModes.Insert,
        placeOfUseColumns: this.columns,
        firstLegalLandDescriptionColumns: this.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.secondLegalLandDescriptionColumns,
        values: {
          ...data,
          versionNumber: +this.route.snapshot.params.versionId,
        },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(this.createPlaceOfUseDto(result));
      } else {
        this.firstInsert.focus();
      }
    });
  }

  protected _displayEditDialog(data): void {
    this.setValidators();
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdateAcreageComponent, {
      data: {
        title: 'Update Retired Place Of Use Record',
        mode: DataManagementDialogModes.Update,
        placeOfUseColumns: this.columns,
        firstLegalLandDescriptionColumns: this.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.secondLegalLandDescriptionColumns,
        values: {
          ...data,
          versionNumber: +this.route.snapshot.params.versionId,
        },
      },
    });
    // Get the input data and peform the update
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this.createPlaceOfUseDto(result), data);
      }
    });
  }

  // Handle the onCopy event
  public copyPOUS(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._doCopyPOUs.bind(this)
    );
  }

  public _doCopyPOUs(): void {
    const confirmDialog = this.dialog.open(EditMessageComponent, {
      width: '500px',
      data: {
        title: 'Copy POU Data',
        message:
          'This will copy all POU data to Place Of Use Retired. Do you want to continue?',
      },
    });

    confirmDialog.afterClosed().subscribe((r) => {
      if (r === 'continue') {
        this.copyPousToRetiredService.insert(null, this.purposeId).subscribe(
          () => {
            this.executingCopy = true;
            this._get();
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error as ErrorBodyInterface;
            const message = errorBody.userMessage;
            this.snackBar.open(message);
          }
        );
      }
    });
  }
}
