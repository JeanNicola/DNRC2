/* eslint-disable max-len */
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { AffectedWaterRightsService } from 'src/app/modules/shared/components/affected-water-rights/services/affected-water-rights.service';
import { RecalculateFeeDueComponent } from '../dor-payments/components/recalculate-fee-due/recalculate-fee-due.component';
import { CalculateFeeDueService } from '../dor-payments/services/calculate-fee-due.service';
import { WaterRightsByGeocodesDialogComponent } from './components/water-rights-by-geocodes-dialog/water-rights-by-geocodes-dialog.component';
import { PopulateByGeocodesService } from './services/populate-by-geocodes.service';
import { PopulateBySellersService } from './services/populate-by-sellers.service';

@Component({
  selector: 'app-transfer-details',
  templateUrl: './transfer-details.component.html',
  styleUrls: ['./transfer-details.component.scss'],
  providers: [
    PopulateByGeocodesService,
    PopulateBySellersService,
    AffectedWaterRightsService,
    CalculateFeeDueService,
  ],
})
export class TransferDetailsComponent {
  // Events
  @Output() dataChanged = new EventEmitter();
  @Output() feeDueChanged = new EventEmitter();
  public onWaterRightDeleteSubject = new Subject();
  public refreshWaterRightsData = new Subject();

  // Properties
  @Input() ownershipUpdateDateProcessed = null;
  @Input() ownershipUpdateDateTerminated = null;
  @Input() ownershipUpdateType = null;
  public ownershipUpdateId;
  public currentBuyersData = null;
  public currentSellersData = null;
  public currentAppsData = null;
  public waterRightIds = null;

  constructor(
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public calculateFeeDueService: CalculateFeeDueService,
    public populateByGeocodesService: PopulateByGeocodesService,
    public populateBySellersService: PopulateBySellersService,
    public affectedWaterRightsService: AffectedWaterRightsService,
    private route: ActivatedRoute
  ) {}

  public ngOnInit() {
    this.ownershipUpdateId = this.route.snapshot.params.id;
  }

  public onBuyersTableLoaded(buyers) {
    if (this.currentBuyersData) {
      this.dataChanged.emit(null);
    }
    this.currentBuyersData = buyers.get.results;
  }

  public onSellersTableLoaded(sellers) {
    if (this.currentSellersData) {
      this.dataChanged.emit(null);
    }
    this.currentSellersData = sellers.get.results;
  }

  public onApplicationsTableLoaded(apps) {
    if (this.currentAppsData) {
      this.dataChanged.emit(null);
    }
    this.currentAppsData = apps.get.results;
  }

  public onWaterRightsTableLoaded(waterRights) {
    if (this.waterRightIds) {
      this.dataChanged.emit(null);
    }
    this.waterRightIds = waterRights.map((wr) => wr.waterRightId);
  }

  private handleFeeDueUpdateSuccess() {
    this.snackBar.open('Fee Due successfully updated.', null);
    this.feeDueChanged.emit(null);
  }

  private handleFeeDueErrorOnUpdate(err: HttpErrorResponse) {
    const errorBody = err.error;
    let message = 'Cannot update Fee Due. ';
    message += errorBody.userMessage || ErrorMessageEnum.PUT;
    this.snackBar.open(message);
  }

  private recalculateFeeDue() {
    if (['DOR 608', '608'].includes(this.ownershipUpdateType)) {
      const recalculateFeeDueDialog = this.dialog.open(
        RecalculateFeeDueComponent,
        {
          width: '500px',
        }
      );
      recalculateFeeDueDialog.afterClosed().subscribe((result) => {
        if (result === 'yes') {
          this.calculateFeeDueService
            .update({}, this.ownershipUpdateId)
            .subscribe({
              next: this.handleFeeDueUpdateSuccess.bind(this),
              error: this.handleFeeDueErrorOnUpdate.bind(this),
            });
        }
      });
    }
  }

  public populateByGeoCodesOrSellersHandler(service, event) {
    // Open the dialog
    const dialogRef = this.dialog.open(WaterRightsByGeocodesDialogComponent, {
      data: {
        title: 'Insert Water Rights',
        values: {
          ownershipUpdateId: this.ownershipUpdateId,
          service,
        },
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        const resultsToInsert = result
          .filter((wr) => this.waterRightIds.indexOf(wr.waterRightId) === -1)
          .map((wr) => wr.waterRightId);
        if (resultsToInsert.length > 0) {
          this.affectedWaterRightsService
            .insert({ waterRightIds: resultsToInsert }, this.ownershipUpdateId)
            .subscribe(() => {
              this.recalculateFeeDue();
              this.refreshWaterRightsData.next(null);
            });
        }
      }
      if (event?.srcElement?.focus) {
        if (event.srcElement.nodeName === 'SPAN') {
          event.srcElement.offsetParent.focus();
        } else {
          event.srcElement.focus();
        }
      }
    });
  }

  // Event used to refresh sellers and applications
  public onWaterRightDelete() {
    if (this.currentSellersData?.length || this.currentAppsData?.length) {
      this.onWaterRightDeleteSubject.next(null);
    }
  }
}
