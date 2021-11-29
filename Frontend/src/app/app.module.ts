// Essential Modules
import { BrowserModule, Title } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Custom Modules
import { CoreModule } from './modules/core/core.module';
import { SharedModule } from './modules/shared/shared.module';
import { AppRoutingModule } from './app-routing.module';

// Hot Loaded Components
import { AppComponent } from './app.component';
import { SnackBarService } from './modules/core/services/snack-bar/snack-bar.service';
import { AuthGuard } from './modules/auth/guards/auth.guard';
import { AuthTokenRequestInterceptor } from './modules/auth/services/auth-token.req.interceptor.service';
import { SessionStorageService } from './modules/core/services/session-storage/session-storage.service';
import { SpinnerHttpInterceptorService } from './modules/shared/components/http-spinner/http-spinner.component';
import { LoginContainerComponent } from './modules/auth/components/login-container.component';
import { LoginFormComponent } from './modules/auth/components/login-form/login-form.component';
import { CustomReuseStrategy } from './custom-reuse-strategy';
import { RouteReuseStrategy } from '@angular/router';
import { InsertVersionCompactComponent } from './modules/features/water-rights-version-details/components/edit/components/compacts/components/insert-version-compact/insert-version-compact.component';
import { EditVersionCompactComponent } from './modules/features/water-rights-version-details/components/edit/components/compacts/components/edit-version-compact/edit-version-compact.component';
import { FlowRateSummaryUpdateDialogComponent } from './modules/features/water-rights-version-details/components/edit/components/point-of-diversion/components/flow-rate-summary-update-dialog/flow-rate-summary-update-dialog.component';
import { FlowRateDescriptionDialogComponent } from './modules/features/water-rights-version-details/components/edit/components/point-of-diversion/components/flow-rate-description-dialog/flow-rate-description-dialog.component';

@NgModule({
  declarations: [AppComponent, LoginContainerComponent, LoginFormComponent, InsertVersionCompactComponent, EditVersionCompactComponent, FlowRateSummaryUpdateDialogComponent, FlowRateDescriptionDialogComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    CoreModule,
    SharedModule,
    AppRoutingModule,
  ],
  providers: [
    { provide: SnackBarService },
    { provide: AuthGuard },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SpinnerHttpInterceptorService,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthTokenRequestInterceptor,
      multi: true,
    },
    { provide: SessionStorageService },
    // This is to turn off reuse of routes, so the application screen
    // will reload every time we navigate, including to another application
    { provide: RouteReuseStrategy, useClass: CustomReuseStrategy },
    { provide: Title },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
