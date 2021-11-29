import { Component, ComponentFactoryResolver } from '@angular/core';
import {
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { EnforcementPodsComponent } from './components/enforcement-pods/enforcement-pods.component';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/edit-screen/edit-screen.component.scss',
    './edit.component.scss',
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
        title: 'View / Edit Enforcement Projects',
      },
      events: {
        errorEvent: (): void => {
          this.error = true;
        },
        dataEvent: (data) => {
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
        component: EnforcementPodsComponent,
        title: 'Point of Diversion',
        properties: {},
        expanded: true,
      },
    ],
  };
}
