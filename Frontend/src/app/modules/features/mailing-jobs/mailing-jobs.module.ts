import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { SharedModule } from "../../shared/shared.module";
import { InterestedPartiesComponent } from "./components/edit/components/interested-parties/interested-parties.component";
import { WaterRightsComponent } from "./components/edit/components/water-rights/water-rights.component";
import { MailingJobEditComponent } from "./components/edit/edit.component";
import { SearchComponent } from "./components/search/search.component";
import { MailingJobsRoutingModule } from "./mailing-jobs-routing.module";
import { EditHeaderComponent } from './components/edit/components/edit-header/edit-header.component';
import { ApplicationSelectDialogComponent } from './components/edit/components/application-select-dialog/application-select-dialog.component';
import { WaterRightSelectDialogComponent } from './components/edit/components/water-right-select-dialog/water-right-select-dialog.component';
import { InterestedPartySelectDialogComponent } from './components/edit/components/interested-party-select-dialog/interested-party-select-dialog.component';
import { InterestedPartyOfficeSearchDialogComponent } from './components/edit/components/interested-party-office-search-dialog/interested-party-office-search-dialog.component';

@NgModule({
    declarations: [
        SearchComponent,
        MailingJobEditComponent,
        WaterRightsComponent,
        InterestedPartiesComponent,
        EditHeaderComponent,
        ApplicationSelectDialogComponent,
        WaterRightSelectDialogComponent,
        InterestedPartySelectDialogComponent,
        InterestedPartyOfficeSearchDialogComponent,
    ],
    imports: [CommonModule, MailingJobsRoutingModule, SharedModule],
})
export class MailingJobsModule {}