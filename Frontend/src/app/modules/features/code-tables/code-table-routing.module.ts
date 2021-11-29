// Essentials
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PageNotFoundComponent } from '../../core/components/page-not-found/page-not-found.component';
// Components
import { CaseAssignmentTypesComponent } from './components/case-assignment-types/components/case-assignment-types.component';
import { CaseStatusComponent } from './components/case-status/components/case-status.component';
import { CaseTypesComponent } from './components/case-types/components/case-types.component';
import { SubdivisionCodesComponent } from './components/subdivision-codes/components/subdivision-codes.component';
import { CityZipCodesComponent } from './components/city-zipcode/components/city-zip-codes.component';
import { EventTypesContainerComponent } from './components/event-types/components/event-types-container/event-types-container.component';

const routes: Routes = [
  {
    path: 'case-assignment-types',
    component: CaseAssignmentTypesComponent,
  },
  {
    path: 'case-types',
    component: CaseTypesComponent,
  },
  {
    path: 'case-status',
    component: CaseStatusComponent,
  },
  {
    path: 'city-zipcodes',
    component: CityZipCodesComponent,
  },
  {
    path: 'event-types',
    component: EventTypesContainerComponent,
  },
  {
    path: 'subdivision-codes',
    component: SubdivisionCodesComponent,
  },
  {
    path: '**',
    component: PageNotFoundComponent,
  },
];

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CodeTableRoutingModule {}
