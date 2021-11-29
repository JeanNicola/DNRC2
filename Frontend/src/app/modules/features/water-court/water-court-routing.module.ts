// Essentials
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EditComponent as ExaminationEditComponent } from './components/examinations/components/edit/edit.component';

// Components
import { SearchComponent as ExaminationsSearchComponent } from './components/examinations/components/search/search.component';
import { SearchComponent as EnforcementsSearchComponent } from './components/enforcements/components/search/search.component';
import { EditComponent as EnforcementsEditComponent } from './components/enforcements/components/edit/edit.component';
import { SearchComponent as CasesSearchComponent } from './components/cases/components/search/search.component';
import { SearchComponent as ObjectionsSearchComponent } from './components/objections/components/search/search.component';
import { EditComponent as CasesEditComponent } from './components/cases/components/edit/edit.component';

const routes: Routes = [
  {
    path: 'examinations',
    component: ExaminationsSearchComponent,
  },
  {
    path: 'examinations/:examinationId',
    component: ExaminationEditComponent,
  },
  {
    path: 'enforcement-projects',
    component: EnforcementsSearchComponent,
  },
  {
    path: 'enforcement-projects/:areaId',
    component: EnforcementsEditComponent,
  },
  {
    path: 'case-hearings',
    component: CasesSearchComponent,
  },

  {
    path: 'case-hearings/:caseId',
    component: CasesEditComponent,
  },
  {
    path: 'objections',
    component: ObjectionsSearchComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WaterCourtRoutingModule {}
