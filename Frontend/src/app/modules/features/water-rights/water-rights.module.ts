/* eslint-disable max-len */
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchComponent } from './components/search/search.component';
import { WaterRightsRoutingModule } from './water-rights-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { WaterRightSearchDialogComponent } from './components/search/components/water-right-search-dialog/water-right-search-dialog.component';
import { GeocodePipe } from './pipes/geocode.pipe';
import { EditComponent } from './components/edit/edit.component';
import { EditHeaderComponent } from './components/edit/components/edit-header/edit-header.component';
import { ChildRightsTableComponent } from './components/edit/components/edit-header/components/child-rights-table/child-rights-table.component';
import { ChildRightsDialogComponent } from './components/edit/components/edit-header/components/child-rights-dialog/child-rights-dialog.component';
import { OriginalRightComponent } from './components/edit/components/edit-header/components/original-right/original-right.component';
import { UpdateOriginalRightComponent } from './components/edit/components/edit-header/components/update-original-right/update-original-right.component';
import { FileLocationOfficeComponent } from './components/edit/components/file-location-office/file-location-office.component';
import { WaterRightResponsibleOfficeComponent } from './components/edit/components/file-location-office/components/water-right-responsible-office/water-right-responsible-office.component';
import { WaterRightOfficeComponent } from './components/edit/components/file-location-office/components/water-right-office/water-right-office.component';
import { WaterRightStaffComponent } from './components/edit/components/file-location-office/components/water-right-staff/water-right-staff.component';
import { WaterRightVersionsComponent } from './components/edit/components/water-right-versions/water-right-versions.component';
import { OwnersComponent } from './components/edit/components/owners/owners.component';
import { RepresentativesDialogComponent } from './components/edit/components/owners/components/representatives-dialog/representatives-dialog.component';
import { RepresentativesTableComponent } from './components/edit/components/owners/components/representatives-table/representatives-table.component';
import { ConservationComponent } from './components/edit/components/conservation/conservation.component';
import { CompactsComponent } from './components/edit/components/compacts/compacts.component';
import { ChangeCompactDialogComponent } from './components/edit/components/compacts/components/change-compact-dialog/change-compact-dialog.component';
import { UpdateConservationDialogComponent } from './components/edit/components/conservation/components/update-conservation-dialog/update-conservation-dialog.component';
import { OwnershipUpdatesComponent } from './components/edit/components/ownership-updates/ownership-updates.component';
import { OwnershipUpdateTableComponent } from './components/edit/components/ownership-updates/components/ownership-update-table/ownership-update-table.component';
import { OwnershipUpdateSellersComponent } from './components/edit/components/ownership-updates/components/ownership-update-sellers/ownership-update-sellers.component';
import { OwnershipUpdateBuyersComponent } from './components/edit/components/ownership-updates/components/ownership-update-buyers/ownership-update-buyers.component';
import { WaterRightInsertDialogComponent } from './components/search/components/water-right-insert-dialog/water-right-insert-dialog.component';
import { WaterRightOwnerSearchDialogComponent } from './components/search/components/water-right-owner-search-dialog/water-right-owner-search-dialog.component';
import { VersionDialogComponent } from './components/search/components/version-dialog/version-dialog.component';
import { VersionTableComponent } from './components/search/components/version-table/version-table.component';
import { SearchComponentForVersions } from '../water-rights-version-details/components/search/search.component';
import { EditVersionsComponent } from '../water-rights-version-details/components/edit/edit.component';
import { EditVersionHeaderComponent } from '../water-rights-version-details/components/edit/components/edit-header/edit-header.component';
import { PointOfDiversionComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/point-of-diversion.component';
import { PurposePlaceOfUseComponent } from '../water-rights-version-details/components/edit/components/purpose-place-of-use/purpose-place-of-use.component';
import { ReservoirComponent } from '../water-rights-version-details/components/edit/components/reservoir/reservoir.component';
import { RemarksComponent } from '../water-rights-version-details/components/edit/components/remarks/remarks.component';
import { HistoricalComponent } from '../water-rights-version-details/components/edit/components/historical/historical.component';
import { PriorityDateComponent } from '../water-rights-version-details/components/edit/components/historical/components/priority-date/priority-date.component';
import { ClaimFilingComponent } from '../water-rights-version-details/components/edit/components/historical/components/claim-filing/claim-filing.component';
import { CourthouseFilingComponent } from '../water-rights-version-details/components/edit/components/historical/components/courthouse-filing/courthouse-filing.component';
import { ChangesComponent } from '../water-rights-version-details/components/edit/components/historical/components/changes/changes.component';
import { RelatedRightsComponent } from '../water-rights-version-details/components/edit/components/related-rights/related-rights.component';
import { CasesComponent } from '../water-rights-version-details/components/edit/components/cases/cases.component';
import { CompactsComponent as VersionCompactsComponent } from '../water-rights-version-details/components/edit/components/compacts/compacts.component';
import { MeasurementReportsComponent } from '../water-rights-version-details/components/edit/components/measurement-reports/measurement-reports.component';
import { ApplicationsComponent } from '../water-rights-version-details/components/edit/components/applications/applications.component';
import { ObjectionsForVersionsComponent } from '../water-rights-version-details/components/edit/components/objections-for-versions/objections-for-versions.component';
import { RelatedRightsTableComponent } from '../water-rights-version-details/components/edit/components/related-rights/components/related-rights-table/related-rights-table.component';
import { GeocodeComponent } from './components/edit/components/geocode/geocode.component';
import { InsertGeocodesDialogComponent } from './components/edit/components/geocode/components/insert-geocodes-dialog/insert-geocodes-dialog.component';
import { GeocodeReadTableComponent } from './components/edit/components/geocode/components/geocode-read-table/geocode-read-table.component';
import { UpdateGeocodeDialogComponent } from './components/edit/components/geocode/components/update-geocode-dialog/update-geocode-dialog.component';
import { EditMessageComponent } from '../../shared/components/dialogs/edit-message/edit-message.component';
import { ObjectorsTableComponent } from '../water-rights-version-details/components/edit/components/objections-for-versions/components/objectors-table/objectors-table.component';
import { ObjectionsTableComponent } from '../water-rights-version-details/components/edit/components/objections-for-versions/components/objections-table/objections-table.component';
import { ElementsTableComponent } from '../water-rights-version-details/components/edit/components/objections-for-versions/components/elements-table/elements-table.component';
import { RelatedWaterRightsTableComponent } from '../water-rights-version-details/components/edit/components/related-rights/components/related-water-rights-table/related-water-rights-table.component';
import { RelatedElementsTableComponent } from '../water-rights-version-details/components/edit/components/related-rights/components/related-elements-table/related-elements-table.component';
import { RemarksTableComponent } from '../water-rights-version-details/components/edit/components/remarks/components/remarks-table/remarks-table.component';
import { VariablesTableComponent } from '../water-rights-version-details/components/edit/components/remarks/components/variables-table/variables-table.component';
import { ReservoirDialogComponent } from '../water-rights-version-details/components/edit/components/reservoir/components/reservoir-dialog/reservoir-dialog.component';
import { FullTextComponent } from '../water-rights-version-details/components/edit/components/remarks/components/full-text/full-text.component';
import { ReportsTableComponent } from '../water-rights-version-details/components/edit/components/measurement-reports/components/reports-table/reports-table.component';
import { MeasurementVariablesTableComponent } from '../water-rights-version-details/components/edit/components/measurement-reports/components/measurement-variables-table/measurement-variables-table.component';
import { MeasurementsTableComponent } from '../water-rights-version-details/components/edit/components/measurement-reports/components/measurements-table/measurements-table.component';
import { SearchComponentForPurposes } from '../purposes/components/search/search.component';
import { CreateComponentForPurposes } from '../purposes/components/create/create.component';
import { EditPurposesComponent } from '../purposes/components/edit/edit.component';
import { GeocodesDataTableComponent } from './components/edit/components/geocode/components/geocodes-data-table/geocodes-data-table.component';
import { ApplicationsDataTableComponent } from '../water-rights-version-details/components/edit/components/applications/components/applications-data-table/applications-data-table.component';
import { EditPurposeHeaderComponent } from '../purposes/components/edit/components/edit-header/edit-header.component';
import { PurposeInsertOrEditDialogComponent } from '../purposes/components/edit/components/edit-header/components/purpose-insert-dialog/purpose-insert-or-edit-dialog.component';
import { RetiredPlaceOfUseComponent } from '../purposes/components/edit/components/retired-place-of-use/retired-place-of-use.component';
import { PerfectedFlowVolumeComponent } from '../purposes/components/edit/components/perfected-flow-volume/perfected-flow-volume.component';
import { MarketingMitigationPurposesComponent } from '../purposes/components/edit/components/marketing-mitigation-purposes/marketing-mitigation-purposes.component';
import { InsertUpdateAcreageComponent } from '../purposes/components/create/components/insert-update-acreage/insert-update-acreage.component';
import { InsertUpdatePeriodComponent } from '../purposes/components/create/components/insert-update-period/insert-update-period.component';
import { InsertRemarkComponent } from '../water-rights-version-details/components/edit/components/remarks/components/insert-remark/insert-remark.component';
import { PodTableComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-table/pod-table.component';
import { InsertPodDialogComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/insert-pod-dialog/insert-pod-dialog.component';
import { SourceSelectDialogComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/source-select-dialog/source-select-dialog.component';
import { PodMainDetailComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-main-detail/pod-main-detail.component';
import { PodMainUpdateDialogComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-main-update-dialog/pod-main-update-dialog.component';
import { DitchSelectDialogComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/ditch-select-dialog/ditch-select-dialog.component';
import { PodSourceDetailComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-source-detail/pod-source-detail.component';
import { PodSourceUpdateDialogComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-source-update-dialog/pod-source-update-dialog.component';
import { PodSubdivisionInfoComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-subdivision-info/pod-subdivision-info.component';
import { PodSubdivisionUpdateDialogComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-subdivision-update-dialog/pod-subdivision-update-dialog.component';
import { SubdivisionSelectDialogComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/subdivision-select-dialog/subdivision-select-dialog.component';
import { PodWellDataComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-well-data/pod-well-data.component';
import { PodAddressUpdateDialogComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-address-update-dialog/pod-address-update-dialog.component';
import { PodAddressDetailComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-address-detail/pod-address-detail.component';
import { PeriodOfDiversionsComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/period-of-diversions/period-of-diversions.component';
import { PodEnforcementsComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-enforcements/pod-enforcements.component';
import { PodEnforcementInsertComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-enforcement-insert/pod-enforcement-insert.component';
import { CountiesService } from '../../shared/services/counties.service';
import { AliquotsService } from '../../shared/services/aliquots.service';
import { TownshipDirectionsService } from '../../shared/services/township-directions.service';
import { RangeDirectionsService } from '../../shared/services/range-directions.service';
import { PodOriginsService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/pod-origins.service';
import { SourceOriginsService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/source-origins.service';
import { MajorTypeService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/major-type.service';
import { MeansOfDiversionService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/means-of-diversion.service';
import { PodTypeService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/pod-type.service';
import { DiversionTypesService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/diversion-types.service';
import { MinorTypesService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/minor-types.service';
import { FlowRateSummaryComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/flow-rate-summary/flow-rate-summary.component';
import { PodDataTableComponent } from '../water-rights-version-details/components/edit/components/point-of-diversion/components/pod-data-table/pod-data-table.component';
import { MaxVolumeComponent } from '../water-rights-version-details/components/edit/components/purpose-place-of-use/components/max-volume/max-volume.component';
import { MaxAcresComponent } from '../water-rights-version-details/components/edit/components/purpose-place-of-use/components/max-acres/max-acres.component';
import { PeriodAndPlaceOfUseComponent } from '../purposes/components/edit/components/period-and-place-of-use/period-and-place-of-use.component';
import { PlaceOfUseComponent } from '../purposes/components/edit/components/period-and-place-of-use/components/place-of-use/place-of-use.component';
import { PeriodComponent } from '../purposes/components/edit/components/period-and-place-of-use/components/period/period.component';
import { SubdivisionInformationComponent } from '../purposes/components/edit/components/period-and-place-of-use/components/subdivision-information/subdivision-information.component';
import { InsertSubdivisionComponent } from '../purposes/components/edit/components/period-and-place-of-use/components/subdivision-information/components/insert-subdivision/insert-subdivision.component';
import { PlaceOfUseDataTableComponent } from '../purposes/components/edit/components/period-and-place-of-use/components/place-of-use/components/place-of-use-data-table/place-of-use-data-table.component';
import { RetiredPlaceOfUseTableComponent } from '../purposes/components/edit/components/retired-place-of-use/components/retired-place-of-use/retired-place-of-use-table.component';
import { PurposesTableComponent } from '../water-rights-version-details/components/edit/components/purpose-place-of-use/components/purposes-table/purposes-table.component';
import { PurposeTypesService } from '../purposes/components/edit/components/edit-header/services/purpose-types.service';
import { OwnerOriginsService } from './services/owner-origins.service';
import { IrrigationTypesService } from '../purposes/components/edit/components/edit-header/services/irrigation-types.service';
import { YesNoValuesService } from '../../shared/services/yes-no-values.service';
import { ClimaticAreasService } from '../purposes/components/edit/components/edit-header/services/climatic-areas.service';
import { LeaseYearValuesService } from '../purposes/components/edit/components/edit-header/components/purpose-insert-dialog/services/lease-year-values.service';
import { PurposeDropdownsService } from '../purposes/components/edit/components/edit-header/services/purpose-dropdowns.service';
import { PodDropdownService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/pod-dropdown.service';
import { DecreesComponent } from '../water-rights-version-details/components/edit/components/decrees/decrees-table.component';
import { MaxVolumeDescriptionDialogComponent } from '../water-rights-version-details/components/edit/components/purpose-place-of-use/components/max-volume/components/max-volume-description-dialog/max-volume-description-dialog.component';
import { MaxVolumeUpdateDialogComponent } from '../water-rights-version-details/components/edit/components/purpose-place-of-use/components/max-volume/components/max-volume-update-dialog/max-volume-update-dialog.component';

@NgModule({
  declarations: [
    SearchComponent,
    WaterRightSearchDialogComponent,
    GeocodePipe,
    EditComponent,
    EditHeaderComponent,
    ChildRightsTableComponent,
    ChildRightsDialogComponent,
    OriginalRightComponent,
    UpdateOriginalRightComponent,
    FileLocationOfficeComponent,
    WaterRightResponsibleOfficeComponent,
    WaterRightOfficeComponent,
    WaterRightStaffComponent,
    WaterRightVersionsComponent,
    OwnersComponent,
    RepresentativesDialogComponent,
    RepresentativesTableComponent,
    ConservationComponent,
    CompactsComponent,
    ChangeCompactDialogComponent,
    UpdateConservationDialogComponent,
    OwnershipUpdatesComponent,
    OwnershipUpdateTableComponent,
    OwnershipUpdateSellersComponent,
    OwnershipUpdateBuyersComponent,
    WaterRightInsertDialogComponent,
    WaterRightOwnerSearchDialogComponent,
    VersionDialogComponent,
    VersionTableComponent,
    GeocodeComponent,
    InsertGeocodesDialogComponent,
    GeocodeReadTableComponent,
    UpdateGeocodeDialogComponent,
    // Version Components
    SearchComponentForVersions,
    EditVersionsComponent,
    EditVersionHeaderComponent,
    PointOfDiversionComponent,
    PurposePlaceOfUseComponent,
    ReservoirComponent,
    RemarksComponent,
    HistoricalComponent,
    PriorityDateComponent,
    ClaimFilingComponent,
    CourthouseFilingComponent,
    ChangesComponent,
    RelatedRightsComponent,
    DecreesComponent,
    ObjectionsForVersionsComponent,
    CasesComponent,
    VersionCompactsComponent,
    MeasurementReportsComponent,
    MeasurementVariablesTableComponent,
    MeasurementsTableComponent,
    ReportsTableComponent,
    ApplicationsComponent,
    RelatedRightsTableComponent,
    EditMessageComponent,
    ObjectionsTableComponent,
    ObjectorsTableComponent,
    ElementsTableComponent,
    RelatedWaterRightsTableComponent,
    RelatedElementsTableComponent,
    RemarksTableComponent,
    VariablesTableComponent,
    ReservoirDialogComponent,
    FullTextComponent,
    ReservoirDialogComponent,
    FullTextComponent,
    InsertRemarkComponent,
    GeocodesDataTableComponent,
    ApplicationsDataTableComponent,
    PodDataTableComponent,
    PodTableComponent,
    InsertPodDialogComponent,
    SourceSelectDialogComponent,
    PodMainDetailComponent,
    PodMainUpdateDialogComponent,
    DitchSelectDialogComponent,
    PodSourceDetailComponent,
    PodSourceUpdateDialogComponent,
    PodSubdivisionInfoComponent,
    PodSubdivisionUpdateDialogComponent,
    SubdivisionSelectDialogComponent,
    PodWellDataComponent,
    PodAddressDetailComponent,
    PodAddressUpdateDialogComponent,
    PeriodOfDiversionsComponent,
    PodEnforcementsComponent,
    PodEnforcementInsertComponent,
    FlowRateSummaryComponent,
    MaxVolumeDescriptionDialogComponent,
    MaxVolumeUpdateDialogComponent,
    // Purposes Components
    SearchComponentForPurposes,
    CreateComponentForPurposes,
    EditPurposesComponent,
    EditPurposeHeaderComponent,
    PurposeInsertOrEditDialogComponent,
    PeriodAndPlaceOfUseComponent,
    RetiredPlaceOfUseComponent,
    MarketingMitigationPurposesComponent,
    PerfectedFlowVolumeComponent,
    InsertUpdateAcreageComponent,
    InsertUpdatePeriodComponent,
    MaxVolumeComponent,
    MaxAcresComponent,
    PlaceOfUseComponent,
    PeriodComponent,
    SubdivisionInformationComponent,
    InsertSubdivisionComponent,
    PlaceOfUseDataTableComponent,
    RetiredPlaceOfUseTableComponent,
    PurposesTableComponent,
  ],
  imports: [CommonModule, WaterRightsRoutingModule, SharedModule],
  providers: [
    PurposeDropdownsService,
    PodDropdownService,
    CountiesService,
    AliquotsService,
    TownshipDirectionsService,
    RangeDirectionsService,
    PodOriginsService,
    SourceOriginsService,
    MajorTypeService,
    MeansOfDiversionService,
    PodTypeService,
    DiversionTypesService,
    MinorTypesService,
    PurposeTypesService,
    OwnerOriginsService,
    YesNoValuesService,
    IrrigationTypesService,
    ClimaticAreasService,
    LeaseYearValuesService,
  ],
})
export class WaterRightsModule {}
