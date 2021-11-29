import { Component, ComponentFactoryResolver, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import {
  EditScreenAccordionDefinition,
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { EditPurposeHeaderComponent } from './components/edit-header/edit-header.component';
import { MarketingMitigationPurposesComponent } from './components/marketing-mitigation-purposes/marketing-mitigation-purposes.component';
import { PerfectedFlowVolumeComponent } from './components/perfected-flow-volume/perfected-flow-volume.component';
import { PeriodAndPlaceOfUseComponent } from './components/period-and-place-of-use/period-and-place-of-use.component';
import { RetiredPlaceOfUseComponent } from './components/retired-place-of-use/retired-place-of-use.component';

@Component({
  selector: 'app-edit',
  templateUrl:
    '../../../../shared/components/templates/edit-screen/edit-screen.component.html',
  styleUrls: [
    '../../../../shared/components/templates/edit-screen/edit-screen.component.scss',
  ],
})
export class EditPurposesComponent
  extends EditScreenComponent
  implements OnInit
{
  constructor(
    componentFactoryResolver: ComponentFactoryResolver,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(componentFactoryResolver);
  }

  ngOnInit(): void {
    if (!this.route.snapshot.data.purposesLoaded) {
      void this.router.navigate(['/error']);
    }
  }

  public reloadHeaderData = new Subject();

  public pageDefinition: EditScreenDefinition = {
    header: {
      component: EditPurposeHeaderComponent,
      properties: {
        title: 'View / Edit Purpose/Place of Use (POU) Details',
        reloadHeaderData: this.reloadHeaderData.asObservable(),
      },
      events: {
        errorEvent: (): void => {
          this.error = true;
        },
        dataEvent: (data): void => {
          this.dataWasFound = true;
          this.error = false;

          setTimeout(() => {
            this.error = false;
            this.pageDefinition.accordions.forEach((a, i) => {
              if (a.onParentData) {
                a.onParentData(i, a, data);
              }
            });
            this.refresh();
          }, 0);
        },
      },
    },
    accordions: [
      {
        component: PeriodAndPlaceOfUseComponent,
        title: 'Place and Period of Use (POU)',
        expanded: true,
        properties: {},
        events: {
          dataChanged: () => {
            this.reloadHeaderData.next(null);
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.headerData = data;
        },
      },
      {
        component: RetiredPlaceOfUseComponent,
        title: 'Retired Place of Use (POU)',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.canEdit = data.canEdit;
          thisAccordion.properties.isDecreed = data.isDecreed;
          thisAccordion.properties.isEditableIfDecreed =
            data.isEditableIfDecreed;
          thisAccordion.properties.waterRightTypeCode = data.waterRightTypeCode;
          thisAccordion.properties.waterRightStatusCode =
            data.waterRightStatusCode;

          if (data.applicationTypeCodes?.includes('650')) {
            thisAccordion.disabled = false;
          } else {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          }
        },
      },
      {
        component: MarketingMitigationPurposesComponent,
        title: 'Marketing / Mitigation for Aquifer Recharge',
        properties: {},
      },
      {
        component: PerfectedFlowVolumeComponent,
        title: 'Perfected Flow/Volume',
        properties: {},
      },
    ],
  };
}
