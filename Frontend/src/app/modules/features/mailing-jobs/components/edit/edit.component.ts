import { Component, ComponentFactoryResolver } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import {
  EditScreenAccordionDefinition,
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { InterestedPartiesComponent } from './components/interested-parties/interested-parties.component';
import { WaterRightsComponent } from './components/water-rights/water-rights.component';

@Component({
  selector: 'app-edit',
  templateUrl:
    '../../../../shared/components/templates/edit-screen/edit-screen.component.html',
  styleUrls: [
    '../../../../shared/components/templates/edit-screen/edit-screen.component.scss',
  ],
})
export class MailingJobEditComponent extends EditScreenComponent {
  constructor(
    componentFactoryResolver: ComponentFactoryResolver,
    public route: ActivatedRoute
  ) {
    super(componentFactoryResolver);
  }

  public reloadHeaderData = new Subject();

  public pageDefinition: EditScreenDefinition = {
    header: {
      component: EditHeaderComponent,
      properties: {
        title: 'View / Edit Mail Job',
        idArray: [this.route.snapshot.params.id],
        reloadHeader: this.reloadHeaderData.asObservable(),
      },
      events: {
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
        component: WaterRightsComponent,
        title: 'Water Rights',
        properties: {},
        events: {
          waterRightsChanged: (): void => {
            this.reloadHeaderData.next();
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.mailingJobNumber];
        },
      },
      {
        component: InterestedPartiesComponent,
        title: 'Interested Parties',
        properties: {},
        events: {
          interestedPartiesChanged: (): void => {
            this.reloadHeaderData.next();
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.mailingJobNumber];
          thisAccordion.properties.responsibleOfficeId =
            data.responsibleOfficeId;
        },
      },
    ],
  };
}
