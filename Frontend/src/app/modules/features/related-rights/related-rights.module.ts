import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/modules/shared/shared.module';
import { RelatedRightsRoutingModule } from './related-rights-routing.module';

// Components
import { EditComponent } from './components/edit/edit.component';
import { CreateComponent } from './components/create/create.component';
import { SearchComponent } from './components/search/search.component';
import { ReviewInformationComponent } from './components/search/components/review-information/review-information.component';
import { RelatedRightRowComponent } from './components/search/components/related-right-row/related-right-row.component';
import { WaterRightsComponent } from './components/search/components/water-rights/water-rights.component';

import { EditHeaderComponent } from './components/edit/components/edit-header/edit-header.component';
import { SharedAndRelatedElementsComponent } from './components/edit/components/shared-and-related-elements/shared-and-related-elements.component';
import { WaterRightsAccordionComponent } from './components/edit/components/water-rights-accordion/water-rights-accordion.component';
import { InsertWaterRightComponent } from './components/search/components/water-rights/components/insert-water-right/insert-water-right.component';
import { SharedElementsComponent } from './components/edit/components/shared-and-related-elements/components/shared-elements/shared-elements.component';
import { RelatedElementsComponent } from './components/edit/components/shared-and-related-elements/components/related-elements/related-elements.component';
import { ResetRelatedElementsComponent } from './components/edit/components/edit-header/components/reset-related-elements/reset-related-elements.component';

@NgModule({
  declarations: [
    EditComponent,
    CreateComponent,
    SearchComponent,
    ReviewInformationComponent,
    RelatedRightRowComponent,
    WaterRightsComponent,
    EditHeaderComponent,
    SharedAndRelatedElementsComponent,
    WaterRightsAccordionComponent,
    InsertWaterRightComponent,
    SharedElementsComponent,
    RelatedElementsComponent,
    ResetRelatedElementsComponent,
  ],
  imports: [CommonModule, RelatedRightsRoutingModule, SharedModule],
})
export class RelatedRightsModule {}
