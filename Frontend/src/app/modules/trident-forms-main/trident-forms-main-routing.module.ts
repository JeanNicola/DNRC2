import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../core/components/page-not-found/page-not-found.component';
import { HomeComponent } from '../features/home/components/home.component';
import { TridentFormsMainComponent } from './components/trident-forms-main.component';

const routes: Routes = [
  {
    path: '',
    component: TridentFormsMainComponent,
    children: [
      {
        path: '',
        component: HomeComponent,
      },
      {
        path: 'applications',
        loadChildren: () =>
          import(
            '../../modules/features/applications/applications.module'
          ).then((m) => m.ApplicationsModule),
      },

      {
        path: 'water-rights',
        loadChildren: () =>
          import(
            '../../modules/features/water-rights/water-rights.module'
          ).then((m) => m.WaterRightsModule),
      },
      {
        path: 'contacts',
        loadChildren: () =>
          import('../../modules/features/contacts/contacts.module').then(
            (m) => m.ContactsModule
          ),
      },
      {
        path: 'related-rights',
        loadChildren: () =>
          import(
            '../../modules/features/related-rights/related-rights.module'
          ).then((m) => m.RelatedRightsModule),
      },
      {
        path: 'ownership-updates',
        loadChildren: () =>
          import(
            '../../modules/features/ownership-update/ownership-update.module'
          ).then((m) => m.OwnershipUpdateModule),
      },
      {
        path: 'water-court',
        loadChildren: () =>
          import('../../modules/features/water-court/water-court.module').then(
            (m) => m.WaterCourtModule
          ),
      },
      {
        path: 'code-tables',
        loadChildren: () =>
          import('../../modules/features/code-tables/code-table.module').then(
            (m) => m.CodeTableModule
          ),
      },
      {
        path: 'mailing-jobs',
        loadChildren: () =>
          import(
            '../../modules/features/mailing-jobs/mailing-jobs.module'
          ).then((m) => m.MailingJobsModule),
      },
      {
        path: '**',
        // Leave this in for now. Once all pages are built, it can be replaced with the commented line.
        component: PageNotFoundComponent,
        // redirectTo: '',
      },
    ],
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TridentFormsMainRouting {}
