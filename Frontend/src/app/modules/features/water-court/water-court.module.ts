import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { WaterCourtRoutingModule } from './water-court-routing.module';
import { SearchComponent as ExaminationsSearchComponent } from './components/examinations/components/search/search.component';
import { EditComponent as ExaminationEditComponent } from './components/examinations/components/edit/edit.component';
import { EditHeaderComponent as ExaminationEditHeaderComponent } from './components/examinations/components/edit/components/edit-header/edit-header.component';
import { ExaminationDataSourcesComponent } from './components/examinations/components/edit/components/examination-data-sources/examination-data-sources.component';
import { SearchComponent as EnforcementsSearchComponent } from './components/enforcements/components/search/search.component';
import { EditComponent as EnforcementsEditComponent } from './components/enforcements/components/edit/edit.component';
import { EditHeaderComponent as EnforcementsEditHeaderComponent } from './components/enforcements/components/edit/components/edit-header/edit-header.component';
import { EnforcementPodsComponent } from './components/enforcements/components/edit/components/enforcement-pods/enforcement-pods.component';
import { DataSourceTableComponent } from './components/examinations/components/edit/components/examination-data-sources/components/data-source-table/data-source-table.component';
import { UsgsQuadMapTableComponent } from './components/examinations/components/edit/components/examination-data-sources/components/usgs-quad-map-table/usgs-quad-map-table.component';
import { InsertUsgsQuadMapComponent } from './components/examinations/components/edit/components/examination-data-sources/components/usgs-quad-map-table/components/insert-usgs-quad-map/insert-usgs-quad-map.component';
import { CreateDataSourceDialogComponent } from './components/examinations/components/edit/components/examination-data-sources/components/data-source-table/components/create-data-source-dialog/create-data-source-dialog.component';
import { AerialPhotosTableComponent } from './components/examinations/components/edit/components/examination-data-sources/components/aerial-photos-table/aerial-photos-table.component';
import { WaterSurveyTableComponent } from './components/examinations/components/edit/components/examination-data-sources/components/water-survey-table/water-survey-table.component';
import { FieldInvestigationRowComponent } from './components/examinations/components/edit/components/examination-data-sources/components/field-investigation-row/field-investigation-row.component';
import { ParcelTableComponent } from './components/examinations/components/edit/components/examination-data-sources/components/parcel-table/parcel-table.component';
import { EnforcementDataTableComponent } from './components/enforcements/components/edit/components/enforcement-pods/components/enforcement-data-table/enforcement-data-table.component';
import { EditComponent as CasesEditComponent } from './components/cases/components/edit/edit.component';
import { EditHeaderComponent as CasesEditHeaderComponent } from './components/cases/components/edit/components/edit-header/edit-header.component';
import { UpdateCasesDialogComponent } from './components/cases/components/edit/components/edit-header/components/update-cases-dialog/update-cases-dialog.component';
import { RegisterComponent } from './components/cases/components/edit/components/register/register.component';
import { CaseWaterRightsComponent } from './components/cases/components/edit/components/case-water-rights/case-water-rights.component';
import { CaseApplicationsComponent } from './components/cases/components/edit/components/case-applications/case-applications.component';
import { CaseAssignmentsComponent } from './components/cases/components/edit/components/case-assignments/case-assignments.component';
import { CaseScheduleComponent } from './components/cases/components/edit/components/case-schedule/case-schedule.component';
import { CaseDistrictCourtComponent } from './components/cases/components/edit/components/case-district-court/case-district-court.component';
import { CaseCommentsComponent } from './components/cases/components/edit/components/case-comments/case-comments.component';
import { RegisterDataTableComponent } from './components/cases/components/edit/components/register/components/register-data-table/register-data-table.component';
import { CaseApplicantsCodeTableComponent } from './components/cases/components/edit/components/case-applications/components/case-applicants-code-table/case-applicants-code-table.component';
import { CaseObjectionsCodeTableComponent } from './components/cases/components/edit/components/case-applications/components/case-objections-code-table/case-objections-code-table.component';
import { CaseScheduleDataTableComponent } from './components/cases/components/edit/components/case-schedule/components/case-schedule-data-table/case-schedule-data-table.component';
import { CourtBaseCodeTableComponent } from './components/cases/components/edit/components/case-district-court/components/court-base-code-table/court-base-code-table.component';
import { DistrictCourtEventsCodeTableComponent } from './components/cases/components/edit/components/case-district-court/components/district-court-events-code-table/district-court-events-code-table.component';
import { DistrictDataTableComponent } from './components/cases/components/edit/components/case-district-court/components/district-court-events-code-table/components/district-data-table/district-data-table.component';
import { WaterRightsCodeTableComponent } from './components/cases/components/edit/components/case-water-rights/components/water-rights-code-table/water-rights-code-table.component';
import { InsertCaseWaterRightComponent } from './components/cases/components/edit/components/case-water-rights/components/insert-case-water-right/insert-case-water-right.component';
import { CaseWaterRightObjectionsComponent } from './components/cases/components/edit/components/case-water-rights/components/case-water-right-objections/case-water-right-objections.component';
import { CreateComponent as CasesCreateComponent } from './components/cases/components/create/create.component';
import { SearchComponent as CasesSearchComponent } from './components/cases/components/search/search.component';
import { SearchComponent as ObjectionsSearchComponent } from './components/objections/components/search/search.component';
import { WaterCourtCaseFormComponent } from './components/cases/components/edit/components/edit-header/components/update-cases-dialog/components/water-court-case-form/water-court-case-form.component';
import { NewAppropriationsCaseFormComponent } from './components/cases/components/edit/components/edit-header/components/update-cases-dialog/components/new-appropriations-case-form/new-appropriations-case-form.component';
import { CreateComponent as CreateObjectionsComponent } from './components/objections/components/create/create.component';
import { DisplayDecreesForObjectionsComponent } from './components/objections/components/create/components/display-decrees-for-objections/display-decrees-for-objections.component';
import { DisplayWrForObjectionsComponent } from './components/objections/components/create/components/display-wr-for-objections/display-wr-for-objections.component';
import { DisplayObjectorsForObjectionsComponent } from './components/objections/components/create/components/display-objectors-for-objections/display-objectors-for-objections.component';
import { DisplayAppsForObjectionsComponent } from './components/objections/components/create/components/display-apps-for-objections/display-apps-for-objections.component';

@NgModule({
  declarations: [
    ExaminationsSearchComponent,
    ExaminationEditComponent,
    ExaminationEditHeaderComponent,
    ExaminationDataSourcesComponent,
    EnforcementsSearchComponent,
    EnforcementsEditComponent,
    EnforcementsEditHeaderComponent,
    EnforcementPodsComponent,
    DataSourceTableComponent,
    UsgsQuadMapTableComponent,
    InsertUsgsQuadMapComponent,
    CreateDataSourceDialogComponent,
    AerialPhotosTableComponent,
    WaterSurveyTableComponent,
    FieldInvestigationRowComponent,
    ParcelTableComponent,
    EnforcementDataTableComponent,
    CasesEditComponent,
    CasesEditHeaderComponent,
    UpdateCasesDialogComponent,
    RegisterComponent,
    CaseWaterRightsComponent,
    CaseApplicationsComponent,
    CaseAssignmentsComponent,
    CaseScheduleComponent,
    CaseDistrictCourtComponent,
    CaseCommentsComponent,
    RegisterDataTableComponent,
    CaseApplicantsCodeTableComponent,
    CaseObjectionsCodeTableComponent,
    CaseScheduleDataTableComponent,
    CourtBaseCodeTableComponent,
    DistrictCourtEventsCodeTableComponent,
    DistrictDataTableComponent,
    WaterRightsCodeTableComponent,
    InsertCaseWaterRightComponent,
    CaseWaterRightObjectionsComponent,
    CasesCreateComponent,
    CasesSearchComponent,
    ObjectionsSearchComponent,
    WaterCourtCaseFormComponent,
    NewAppropriationsCaseFormComponent,
    CreateObjectionsComponent,
    DisplayDecreesForObjectionsComponent,
    DisplayWrForObjectionsComponent,
    DisplayObjectorsForObjectionsComponent,
    DisplayAppsForObjectionsComponent,
  ],
  imports: [CommonModule, WaterCourtRoutingModule, SharedModule],
})
export class WaterCourtModule {}
