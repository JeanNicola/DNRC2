// Essentials
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Modules
import { SharedModule } from '../shared/shared.module';

// Components
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { NavComponent } from './components/nav/nav.component';
import { LoginMenuComponent } from './components/login-menu/login-menu.component';

@NgModule({
  imports: [CommonModule, RouterModule, SharedModule],
  declarations: [PageNotFoundComponent, NavComponent, LoginMenuComponent],
  exports: [PageNotFoundComponent, NavComponent, LoginMenuComponent],
})
export class CoreModule {}
