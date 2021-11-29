import { Component, ComponentFactoryResolver } from '@angular/core';
import {
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { ExaminationDataSourcesComponent } from './components/examination-data-sources/examination-data-sources.component';

@Component({
  selector: 'app-edit',
  templateUrl:
    '../../../../../../shared/components/templates/edit-screen/edit-screen.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/edit-screen/edit-screen.component.scss',
  ],
})
export class EditComponent extends EditScreenComponent {
  constructor(componentFactoryResolver: ComponentFactoryResolver) {
    super(componentFactoryResolver);
  }

  public pageDefinition: EditScreenDefinition = {
    header: {
      component: EditHeaderComponent,
      properties: {
        title: 'View / Edit Examination Details',
      },
      events: {
        errorEvent: (): void => {
          this.error = true;
        },
        dataEvent: (data): void => {
          this.dataWasFound = true;
          this.error = false;

          setTimeout(() => {
            this.pageDefinition.accordions.forEach((a, i) => {
              if (a.onParentData) {
                a.onParentData(i, a, data.get);
              }
            });
            this.refresh();
          }, 0);
        },
      },
    },
    accordions: [
      {
        component: ExaminationDataSourcesComponent,
        title: 'Place of Use (POU) Examinations',
        properties: {},
      },
    ],
  };
}
