/* eslint-disable max-len */
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PurposeDropdownsService } from '../purposes/components/edit/components/edit-header/services/purpose-dropdowns.service';
import { EditPurposesComponent } from '../purposes/components/edit/edit.component';
import { SearchComponentForPurposes } from '../purposes/components/search/search.component';
import { PodDropdownService } from '../water-rights-version-details/components/edit/components/point-of-diversion/services/pod-dropdown.service';
import { EditVersionsComponent } from '../water-rights-version-details/components/edit/edit.component';
import { SearchComponentForVersions } from '../water-rights-version-details/components/search/search.component';
import { EditComponent } from './components/edit/edit.component';
import { SearchComponent } from './components/search/search.component';

const routes: Routes = [
  {
    path: '',
    component: SearchComponent,
  },
  {
    path: 'versions/purposes',
    component: SearchComponentForPurposes,
  },
  {
    path: ':waterRightId/versions/:versionId/purposes/:purposeId',
    component: EditPurposesComponent,
    resolve: {
      purposesLoaded: PurposeDropdownsService,
    },
  },
  {
    path: 'versions',
    component: SearchComponentForVersions,
  },
  {
    path: ':waterRightId/versions/:versionId',
    component: EditVersionsComponent,
    resolve: {
      podLoaded: PodDropdownService,
      purposesLoaded: PurposeDropdownsService,
    },
  },
  {
    path: ':id',
    component: EditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WaterRightsRoutingModule {}
