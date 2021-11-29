import { Component, ComponentFactoryResolver } from '@angular/core';
import {
  EditScreenAccordionDefinition,
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { CaseProgramTypes } from '../create/enums/caseProgramTypes';
import { CaseApplicationsComponent } from './components/case-applications/case-applications.component';
import { CaseAssignmentsComponent } from './components/case-assignments/case-assignments.component';
import { CaseCommentsComponent } from './components/case-comments/case-comments.component';
import { CaseDistrictCourtComponent } from './components/case-district-court/case-district-court.component';
import { CaseScheduleComponent } from './components/case-schedule/case-schedule.component';
import { CaseWaterRightsComponent } from './components/case-water-rights/case-water-rights.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { RegisterComponent } from './components/register/register.component';

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
        title: 'Case/Hearing Details View / Edit',
      },
      events: {
        errorEvent: (): void => {
          this.error = true;
        },
        dataEvent: (data) => {
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
        component: RegisterComponent,
        title: 'Register',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.hasCaseAdminRole = data.hasCaseAdminRole;
          thisAccordion.properties.caseTypeCode = data.caseType;
        },
      },
      {
        component: CaseWaterRightsComponent,
        title: 'Water Rights',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.disabled =
            data.programType === CaseProgramTypes.NA_PROGRAM;
          thisAccordion.properties.decreeId = data.decreeId;
          thisAccordion.properties.decreeBasin = data.decreeBasin;
          thisAccordion.properties.hasCaseAdminRole = data.hasCaseAdminRole;
        },
      },
      {
        component: CaseApplicationsComponent,
        title: 'Applications',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.applicationId = data.applicationId;
          if (
            data.programType === CaseProgramTypes.WC_PROGRAM ||
            !data.applicationId
          ) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
          }
        },
      },

      {
        component: CaseAssignmentsComponent,
        title: 'Assignments',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.hasCaseAdminRole = data.hasCaseAdminRole;
          thisAccordion.properties.programType = data.programType;
        },
      },
      {
        component: CaseScheduleComponent,
        title: 'Schedule',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.disabled =
            data.programType === CaseProgramTypes.WC_PROGRAM;
          if (thisAccordion.disabled) {
            setTimeout(() => {
              this.accordionInstances.get(index).close();
            });
          }
        },
      },
      {
        component: CaseDistrictCourtComponent,
        title: 'District Court',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.disabled =
            data.programType === CaseProgramTypes.WC_PROGRAM;
          if (thisAccordion.disabled) {
            setTimeout(() => {
              this.accordionInstances.get(index).close();
            });
          }
        },
      },
      {
        component: CaseCommentsComponent,
        title: 'Comments',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.hasCaseAdminRole = data.hasCaseAdminRole;
          thisAccordion.properties.programType = data.programType;
        },
      },
    ],
  };
}
