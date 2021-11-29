import { Component, ComponentFactoryResolver } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import {
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { SharedAndRelatedElementsComponent } from './components/shared-and-related-elements/shared-and-related-elements.component';
import { WaterRightsAccordionComponent } from './components/water-rights-accordion/water-rights-accordion.component';
@Component({
  selector: 'app-edit',
  templateUrl:
    '../../../../shared/components/templates/edit-screen/edit-screen.component.html',
  styleUrls: [
    '../../../../shared/components/templates/edit-screen/edit-screen.component.scss',
  ],
})
export class EditComponent extends EditScreenComponent {
  constructor(componentFactoryResolver: ComponentFactoryResolver) {
    super(componentFactoryResolver);
  }

  public reloadRelatedElements = new BehaviorSubject(null);
  public reloadHeaderData = new Subject();

  public pageDefinition: EditScreenDefinition = {
    header: {
      component: EditHeaderComponent,
      properties: {
        title: 'View / Edit Related Right',
        reloadHeaderData: this.reloadHeaderData.asObservable(),
      },
      events: {
        errorEvent: (): void => {
          this.error = true;
        },
        dataEvent: (data) => {
          this.reloadRelatedElements.next(data.get);
          this.dataWasFound = true;
          this.error = false;

          setTimeout(() => {
            this.refresh();
          }, 0);
        },
      },
    },
    accordions: [
      {
        component: WaterRightsAccordionComponent,
        title: 'Water Rights',
        properties: {},
      },
      {
        component: SharedAndRelatedElementsComponent,
        title: 'Related and Shared Elements',
        properties: {
          reloadRelatedElements: this.reloadRelatedElements.asObservable(),
        },
        events: {
          relatedElementsChanged: () => {
            this.reloadHeaderData.next(null);
          },
        },
      },
    ],
  };
}
