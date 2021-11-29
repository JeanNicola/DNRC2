import { Component, ComponentFactoryResolver } from '@angular/core';
import * as moment from 'moment';
import { Subject } from 'rxjs';
import {
  EditScreenAccordionDefinition,
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { ApplicationConstants } from 'src/app/modules/shared/constants/application-constants';
import { ApplicantComponent } from './components/applicant/applicant.component';
import { ChangeDescriptionComponent } from './components/change-description/change-description.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { EventsComponent } from './components/events/events.component';
import { LocationComponent } from './components/location/location.component';
import { MarketingMitigationComponent } from './components/marketing-mitigation/marketing-mitigation.component';
import { NoticeListComponent } from './components/notice-list/notice-list.component';
import { ObjectionsComponent } from './components/objections/objections.component';
import { PaymentsComponent } from './components/payments/payments.component';
import { RelatedApplicationsComponent } from './components/related-applications/related-applications.component';
import { WaterRightsComponent } from './components/water-rights/water-rights.component';

export interface EditApplicationInterface {
  applicationId: number;
  applicationTypeCode: string;
  applicationTypeDescription: string;
  basin: string;
  dateTimeReceived: string;
  feeStatus: string;
  filingFee: number;
  issued: string;
  reissued: string;
  hasGeocode: boolean;
  hasAutoCompleteCode: boolean;
}

@Component({
  selector: 'app-edit',
  templateUrl:
    '../../../../shared/components/templates/edit-screen/edit-screen.component.html',
  styleUrls: [
    '../../../../shared/components/templates/edit-screen/edit-screen.component.scss',
    './edit.component.scss',
  ],
})
export class EditComponent extends EditScreenComponent {
  constructor(componentFactoryResolver: ComponentFactoryResolver) {
    super(componentFactoryResolver);
  }

  public reloadEvents = new Subject();
  public reloadPayments = new Subject();
  public reloadHeaderData = new Subject();

  public pageDefinition: EditScreenDefinition = {
    header: {
      component: EditHeaderComponent,
      properties: {
        title: 'View / Edit Application',
        reloadHeaderData: this.reloadHeaderData.asObservable(),
      },
      events: {
        errorEvent: (): void => {
          this.error = true;
        },
        dataEvent: (data: EditApplicationInterface): void => {
          this.dataWasFound = true;
          this.error = false;

          setTimeout(() => {
            const appCode = data.applicationTypeCode;
            const startsWithP =
              appCode
                .substring(appCode.length - 1, appCode.length)
                .toUpperCase() === 'P';

            this.pageDefinition.accordions.forEach((accordion, index) =>
              accordion.onParentData?.(
                index,
                accordion,
                data,
                appCode,
                startsWithP
              )
            );

            this.refresh();
          });
          this.reloadEvents.next();
        },
      },
    },
    accordions: [
      {
        component: EventsComponent,
        title: 'Events',
        expanded: false,
        properties: {
          appData: null,
          idArray: null,
          reloadEvents: this.reloadEvents.asObservable(),
        },
        events: {
          reloadPayments: (): void => {
            this.reloadPayments.next();
          },
          eventsChanged: () => {
            this.reloadHeaderData.next();
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          thisAccordion.properties.appData = data;
          thisAccordion.properties.idArray = [data.applicationId];
        },
      },
      {
        component: ApplicantComponent,
        title: 'Applicant',
        properties: {
          appData: null,
          idArray: null,
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          // Show/Hide Applicants accordion
          // Disable if '607', '617', '618', '626'
          if (['607', '617', '618', '626'].includes(appCode)) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
            // Set properties on Applicants accordion
            thisAccordion.properties.appData = data;
            thisAccordion.properties.idArray = [data.applicationId];
          }
        },
      },
      {
        component: LocationComponent,
        title: 'File Location and Processor',
        properties: {
          idArray: null,
          date: null,
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          // Set properties on File Location accordion
          thisAccordion.properties.date = data.dateTimeReceived;
          thisAccordion.properties.idArray = [data.applicationId];
        },
      },
      {
        component: PaymentsComponent,
        title: 'Payments',
        properties: {
          appData: null,
          reloadPayments: this.reloadPayments.asObservable(),
        },
        events: {
          reloadEvents: (): void => {
            this.reloadEvents.next();
          },
          paymentsChanged: () => {
            this.reloadHeaderData.next();
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          // Show/hide Payments accordion
          if (
            !startsWithP &&
            data.dateTimeReceived &&
            moment(data.dateTimeReceived).isAfter(
              ApplicationConstants.filingFeeStartDate
            ) &&
            (data.filingFee > 0 || data.hasAutoCompleteCode)
          ) {
            thisAccordion.disabled = false;
            thisAccordion.properties.appData = data;
          } else {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          }
        },
      },
      {
        component: WaterRightsComponent,
        title: 'Water Rights',
        properties: {
          appTypeCode: null,
          idArray: null,
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          // Show/hide Water Rights accordion
          // Disable if P type
          if (startsWithP) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
            // Set properties on Water Rights accordion
            thisAccordion.properties.appTypeCode = data.applicationTypeCode;
            thisAccordion.properties.idArray = [data.applicationId];
          }
        },
      },
      {
        component: NoticeListComponent,
        title: 'Notice List',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          // Show/hide Notices accordion
          // Disable if P type
          // Oracle Forms had a bug where these accordions for appl type 635 were never disabled
          if (startsWithP) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
          }
        },
      },
      {
        component: ChangeDescriptionComponent,
        title: 'Change Description',
        properties: {
          appTypeCode: null,
          idArray: null,
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          // Show/hide Change Description accordion
          // Enable if '606', '604', '626', '634','635', '644','650','105'
          if (
            !['105', '604', '606', '626', '634', '635', '644', '650'].includes(
              appCode
            )
          ) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
            // Set properties on Change Description accordion
            thisAccordion.properties.appTypeCode = data.applicationTypeCode;
            thisAccordion.properties.idArray = [data.applicationId];
          }
        },
      },
      {
        component: RelatedApplicationsComponent,
        title: 'Related Applications',
        properties: {
          idArray: null,
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          // Show/hide Related Applications accordion
          // Disable if P type
          // Oracle Forms had a bug where these accordions for appl type 635 were never disabled
          if (startsWithP) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
            // Set properties on Related Applications accordion
            thisAccordion.properties.idArray = [data.applicationId];
          }
        },
      },
      {
        component: MarketingMitigationComponent,
        title: 'Marketing / Mitigation for Aquifer Recharge',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {},
      },
      {
        component: ObjectionsComponent,
        title: 'Objections',
        properties: {
          appId: null,
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any,
          appCode: string,
          startsWithP: string
        ): void => {
          // Show/hide Objections accordion
          // Disable if P type
          // Oracle Forms had a bug where these accordions for appl type 635 were never disabled
          if (startsWithP) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
            // Set properties on Objections accordion
            thisAccordion.properties.appId = data.applicationId;
          }
        },
      },
    ],
  };
}
