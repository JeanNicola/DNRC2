import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './modules/auth/guards/auth.guard';
import { LoginContainerComponent } from './modules/auth/components/login-container.component';
import { HomeComponent } from './modules/features/home/components/home.component';
import { EndpointsService } from './modules/core/services/endpoint/endpoints.service';

const routes: Routes = [
  {
    path: 'wris',
    canActivate: [AuthGuard],
    // This ensures endpoints are loaded before application starts
    // If endpoints fails it passes "false" to TridentFormsMain component
    resolve: {
      canLoad: EndpointsService,
    },

    children: [
      {
        path: '',
        loadChildren: () =>
          import('./modules/trident-forms-main/trident-forms-main.module').then(
            (m) => m.TridentFormsModule
          ),
      },
    ],
  },
  {
    path: 'error',
    component: HomeComponent,
  },
  {
    // default path is to go to login screen
    path: '',
    component: LoginContainerComponent,
  },
  {
    // the wildcard is to go to login screen
    path: '**',
    redirectTo: '',
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      // preloadingStrategy: PreloadAllModules,
      onSameUrlNavigation: 'reload',
      useHash: false,
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
