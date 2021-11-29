// Essentials
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PageNotFoundComponent } from '../../core/components/page-not-found/page-not-found.component';
import { EditComponent } from './components/edit/edit.component';
import { SearchComponent } from './components/search/search.component';

// Components

const routes: Routes = [
  {
    path: ':id',
    component: EditComponent,
  },
  {
    path: '',
    component: SearchComponent,
  },
  {
    path: '**',
    component: PageNotFoundComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ApplicationsRoutingModule {}
