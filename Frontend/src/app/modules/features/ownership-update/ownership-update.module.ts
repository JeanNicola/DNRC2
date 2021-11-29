import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/modules/shared/shared.module';
import { OwnershipUpdateRoutingModule } from './ownership-update-routing.module';
import { SearchComponent } from './components/search/search.component';
import { CreateComponent } from './components/create/create.component';
import { EditComponent } from './components/edit/edit.component';
import { TransferDetailsComponent } from './components/edit/components/transfer-details/transfer-details.component';
import { FileLocationAndProcessorComponent } from './components/edit/components/file-location-and-processor/file-location-and-processor.component';
import { EditHeaderComponent } from './components/edit/components/edit-header/edit-header.component';
import { DorPaymentsComponent } from './components/edit/components/dor-payments/dor-payments.component';
import { NotesComponent } from './components/edit/components/notes/notes.component';
import { OwnershipupdateSearchDialogComponent } from './components/search/components/ownershipupdate-search-dialog/ownershipupdate-search-dialog.component';
import { OwnershipUpdateDialogComponent } from './components/search/components/ownership-update-dialog/ownership-update-dialog.component';
import { OwnershipUpdateTableComponent } from './components/search/components/ownership-update-table/ownership-update-table.component';
import { WaterRightsDialogComponent } from './components/search/components/water-rights-dialog/water-rights-dialog.component';
import { InsertSellerBuyerComponent } from './components/create/components/insert-seller-buyer/insert-seller-buyer.component';
import { EditOwnershipUpdateDialogComponent } from './components/edit/components/edit-header/components/edit-ownership-update-dialog/edit-ownership-update-dialog.component';
import { CustomersTableComponent } from './components/create/components/insert-seller-buyer/components/customers-table/customers-table.component';
import { ResetSellersDialogComponent } from './components/create/components/reset-sellers-dialog/reset-sellers-dialog.component';
import { OwnershipUpdateOfficeComponent } from './components/edit/components/file-location-and-processor/components/ownership-update-office/ownership-update-office.component';
import { OwnershipUpdateStaffComponent } from './components/edit/components/file-location-and-processor/components/ownership-update-staff/ownership-update-staff.component';
import { OwnershipUpdateProcessorComponent } from './components/edit/components/file-location-and-processor/components/ownership-update-processor/ownership-update-processor.component';
import { OwnershipUpdateResponsibleOfficeComponent } from './components/edit/components/file-location-and-processor/components/ownership-update-responsible-office/ownership-update-responsible-office.component';
import { ReviewInformationDialogComponent } from './components/search/components/review-information-dialog/review-information-dialog.component';

import { SellersTableComponent } from './components/edit/components/transfer-details/components/sellers-table/sellers-table.component';
import { BuyersTableComponent } from './components/edit/components/transfer-details/components/buyers-table/buyers-table.component';
import { OwnershipUpdateDataRowComponent } from './components/search/components/ownership-update-data-row/ownership-update-data-row.component';

import { WaterRightsByGeocodesDialogComponent } from './components/edit/components/transfer-details/components/water-rights-by-geocodes-dialog/water-rights-by-geocodes-dialog.component';
import { InsertMultipleWaterRightsComponent } from './components/edit/components/transfer-details/components/insert-multiple-water-rights/insert-multiple-water-rights.component';
import { GeocodesInfoDialogComponent } from './components/edit/components/transfer-details/components/insert-multiple-water-rights/components/geocodes-info-dialog/geocodes-info-dialog.component';
import { GeocodesTableComponent } from './components/edit/components/transfer-details/components/insert-multiple-water-rights/components/geocodes-table/geocodes-table.component';
import { OwnersTableComponent } from './components/edit/components/transfer-details/components/insert-multiple-water-rights/components/owners-table/owners-table.component';
import { WaterRightRowComponent } from './components/edit/components/transfer-details/components/insert-multiple-water-rights/components/water-right-row/water-right-row.component';
import { FeeSummaryComponent } from './components/edit/components/dor-payments/components/fee-summary/fee-summary.component';
import { FeeLetterComponent } from './components/edit/components/dor-payments/components/fee-letter/fee-letter.component';
import { RecalculateFeeDueComponent } from './components/edit/components/dor-payments/components/recalculate-fee-due/recalculate-fee-due.component';

// Components
@NgModule({
  declarations: [
    SearchComponent,
    CreateComponent,
    EditComponent,
    EditHeaderComponent,
    TransferDetailsComponent,
    FileLocationAndProcessorComponent,
    DorPaymentsComponent,
    NotesComponent,
    OwnershipupdateSearchDialogComponent,
    OwnershipUpdateDialogComponent,
    OwnershipUpdateTableComponent,
    WaterRightsDialogComponent,
    InsertSellerBuyerComponent,
    CustomersTableComponent,
    EditOwnershipUpdateDialogComponent,
    FeeSummaryComponent,
    FeeLetterComponent,
    ResetSellersDialogComponent,
    SellersTableComponent,
    BuyersTableComponent,
    OwnershipUpdateOfficeComponent,
    OwnershipUpdateStaffComponent,
    OwnershipUpdateProcessorComponent,
    OwnershipUpdateResponsibleOfficeComponent,
    ReviewInformationDialogComponent,
    SellersTableComponent,
    BuyersTableComponent,
    OwnershipUpdateDataRowComponent,
    WaterRightsByGeocodesDialogComponent,
    InsertMultipleWaterRightsComponent,
    GeocodesInfoDialogComponent,
    GeocodesTableComponent,
    OwnersTableComponent,
    WaterRightRowComponent,
    RecalculateFeeDueComponent,
  ],
  imports: [CommonModule, OwnershipUpdateRoutingModule, SharedModule],
})
export class OwnershipUpdateModule {}
