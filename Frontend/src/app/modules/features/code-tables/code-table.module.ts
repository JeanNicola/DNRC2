// Essentials
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/modules/shared/shared.module';
import { CodeTableRoutingModule } from './code-table-routing.module';

// Add the component imports here
import { CaseTypesComponent } from './components/case-types/components/case-types.component';
import { CaseStatusComponent } from './components/case-status/components/case-status.component';
import { CaseAssignmentTypesComponent } from './components/case-assignment-types/components/case-assignment-types.component';
import { SubdivisionCodesComponent } from './components/subdivision-codes/components/subdivision-codes.component';
import { SubdivisionCodesInsertDialogComponent } from './components/subdivision-codes/components/subdivision-codes-insert-dialog.component';
import { CityZipCodesComponent } from './components/city-zipcode/components/city-zip-codes.component';
import { EventTypesComponent } from './components/event-types/components/event-types.component';
import { EventTypesContainerComponent } from './components/event-types/components/event-types-container/event-types-container.component';
import { EventSubtypeComponent } from './components/event-types/templates/event-subtype.component';
import { EventTypesDecreeTypesComponent } from './components/event-types/components/sub-types/decree-types.component';
import { EventTypesApplicationTypesComponent } from './components/event-types/components/sub-types/application-types.component';
import { EventTypesCaseTypesComponent } from './components/event-types/components/sub-types/case-types.component';

@NgModule({
  imports: [CommonModule, CodeTableRoutingModule, SharedModule],
  // Declare each code table component here
  declarations: [
    CaseAssignmentTypesComponent,
    CaseTypesComponent,
    CaseStatusComponent,
    SubdivisionCodesComponent,
    SubdivisionCodesInsertDialogComponent,
    CityZipCodesComponent,
    EventTypesComponent,
    EventTypesContainerComponent,
    EventSubtypeComponent,
    EventTypesDecreeTypesComponent,
    EventTypesApplicationTypesComponent,
    EventTypesCaseTypesComponent,
  ],
})
export class CodeTableModule {}
