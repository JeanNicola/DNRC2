import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MailingJobEditComponent } from './components/edit/edit.component';
import { SearchComponent } from './components/search/search.component';

const routes: Routes = [
  {
    path: '',
    component: SearchComponent,
  },
  {
    path: ':id',
    component: MailingJobEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MailingJobsRoutingModule {}
