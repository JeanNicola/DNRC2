import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TridentFormsMainRouting } from './trident-forms-main-routing.module';
import { TridentFormsMainComponent } from './components/trident-forms-main.component';
import { CoreModule } from '../core/core.module';
import { HomeComponent } from '../features/home/components/home.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [CommonModule, CoreModule, SharedModule, TridentFormsMainRouting],
  declarations: [TridentFormsMainComponent, HomeComponent],
})
export class TridentFormsModule {}
