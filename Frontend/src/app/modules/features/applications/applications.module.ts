/* eslint-disable max-len */
// Essentials
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/modules/shared/shared.module';
import { ApplicationsRoutingModule } from './applications-routing.module';

// Components
import { SearchComponent } from './components/search/search.component';
import { CreateComponent } from './components/create/create.component';
import { ApplicantComponent } from './components/edit/components/applicant/applicant.component';
import { ApplicantTableComponent } from './components/edit/components/applicant/components/applicant-table/applicant-table.component';
import { RepresentativesComponent } from './components/edit/components/applicant/components/representatives/representatives.component';
import { EventsComponent } from './components/edit/components/events/events.component';
import { LocationComponent } from './components/edit/components/location/location.component';
import { PaymentsComponent } from './components/edit/components/payments/payments.component';
import { WaterRightsComponent } from './components/edit/components/water-rights/water-rights.component';
import { NoticeListComponent } from './components/edit/components/notice-list/notice-list.component';
import { ObjectionsComponent } from './components/edit/components/objections/objections.component';
import { ChangeDescriptionComponent } from './components/edit/components/change-description/change-description.component';
import { RelatedApplicationsComponent } from './components/edit/components/related-applications/related-applications.component';
import { EditHeaderComponent } from './components/edit/components/edit-header/edit-header.component';
import { ApplicationEditDialogComponent } from './components/edit/components/edit-header/components/application-edit-dialog.component';
import { EventsInsertDialogComponent } from './components/edit/components/events/components/events-insert-dialog.component';
import { EventsUpdateDialogComponent } from './components/edit/components/events/components/events-update-dialog.component';
import { FeeSummaryComponent } from './components/edit/components/payments/components/fee-summary/fee-summary.component';
import { ChangeDescriptionUpdateDialogComponent } from './components/edit/components/change-description/components/change-description-update-dialog/change-description-update-dialog.component';
import { ChangeDescriptionConfirmDialogComponent } from './components/edit/components/change-description/components/change-description-confirm-dialog/change-description-confirm-dialog.component';
import { NoticeWaterRightsComponent } from './components/edit/components/notice-list/components/water-rights/notice-water-rights.component';
// eslint-disable-next-line max-len
import { RepresentativesDialogComponent } from './components/edit/components/applicant/components/representatives-dialog/representatives-dialog.component';
import { RepresentativesTableComponent } from './components/edit/components/applicant/components/representatives-table/representatives-table.component';
import { EventsDataTableComponent } from './components/edit/components/events/components/events-data-table/events-data-table.component';
import { ObjectionsMainComponent } from './components/edit/components/objections/components/objections-main/objections-main.component';
import { ObjectionsSummaryComponent } from './components/edit/components/objections/components/objections-summary/objections-summary.component';
import { MailingJobComponent } from './components/edit/components/notice-list/components/mailing-job/mailing-job.component';
import { OtherPartiesComponent } from './components/edit/components/notice-list/components/other-parties/other-parties.component';
import { WaterRightsSummaryComponent } from './components/edit/components/water-rights/components/water-rights-summary/water-rights-summary.component';
import { WaterRightsTableComponent } from './components/edit/components/water-rights/components/water-rights-table/water-rights-table.component';
import { InsertWaterRightComponent } from './components/edit/components/water-rights/components/insert-water-right/insert-water-right.component';
import { WaterRightsUpdateDialogComponent } from './components/edit/components/water-rights/components/water-rights-update-dialog/water-rights-update-dialog.component';
import { OwnersApplicationTableComponent } from './components/search/owners-application-table/owners-application-table.component';
import { OwnersApplicationDialogComponent } from './components/search/owners-application-dialog/owners-application-dialog.component';
import { RepsApplicationDialogComponent } from './components/search/reps-application-dialog/reps-application-dialog.component';
import { RepsApplicationTableComponent } from './components/search/reps-application-table/reps-application-table.component';
import { ApplicationSearchDialogComponent } from './components/search/application-search-dialog/application-search-dialog.component';
import { FeeSummaryUpdateDialogComponent } from './components/edit/components/payments/components/fee-summary-update-dialog/fee-summary-update-dialog.component';
import { ObjectorsRepresentativeDialogComponent } from './components/edit/components/objections/components/objectors-representative-dialog/objectors-representative-dialog.component';
import { ObjectorsRepresentativeTableComponent } from './components/edit/components/objections/components/objectors-representative-table/objectors-representative-table.component';
import { ApplicationResponsibleOfficeComponent } from './components/edit/components/location/components/application-responsible-office/application-responsible-office.component';
import { ApplicationProcessorComponent } from './components/edit/components/location/components/application-processor/application-processor.component';
import { ApplicationOfficeComponent } from './components/edit/components/location/components/application-office/application-office.component';
import { ApplicationStaffComponent } from './components/edit/components/location/components/application-staff/application-staff.component';
import { MarketingMitigationComponent } from './components/edit/components/marketing-mitigation/marketing-mitigation.component';
import { EditComponent } from './components/edit/edit.component';
import { ApplWrDataTableComponent } from './components/edit/components/water-rights/components/water-rights-table/appl-wr-data-table/appl-wr-data-table.component';

@NgModule({
  declarations: [
    EditComponent,
    SearchComponent,
    CreateComponent,
    ApplicantComponent,
    ApplicantTableComponent,
    RepresentativesComponent,
    EventsComponent,
    LocationComponent,
    MarketingMitigationComponent,
    PaymentsComponent,
    WaterRightsComponent,
    NoticeListComponent,
    ObjectionsComponent,
    ChangeDescriptionComponent,
    RelatedApplicationsComponent,
    EditHeaderComponent,
    ApplicationEditDialogComponent,
    EventsInsertDialogComponent,
    EventsUpdateDialogComponent,
    FeeSummaryComponent,
    ChangeDescriptionUpdateDialogComponent,
    ChangeDescriptionConfirmDialogComponent,
    RepresentativesDialogComponent,
    RepresentativesTableComponent,
    EventsDataTableComponent,
    ObjectionsMainComponent,
    ObjectionsSummaryComponent,
    MailingJobComponent,
    NoticeWaterRightsComponent,
    OtherPartiesComponent,
    WaterRightsSummaryComponent,
    WaterRightsTableComponent,
    InsertWaterRightComponent,
    WaterRightsUpdateDialogComponent,
    OwnersApplicationTableComponent,
    OwnersApplicationDialogComponent,
    RepsApplicationDialogComponent,
    RepsApplicationTableComponent,
    ApplicationSearchDialogComponent,
    FeeSummaryUpdateDialogComponent,
    ObjectorsRepresentativeDialogComponent,
    ObjectorsRepresentativeTableComponent,
    ApplicationResponsibleOfficeComponent,
    ApplicationProcessorComponent,
    ApplicationOfficeComponent,
    ApplicationStaffComponent,
    ApplWrDataTableComponent,
  ],
  imports: [CommonModule, ApplicationsRoutingModule, SharedModule],
})
export class ApplicationsModule {}
