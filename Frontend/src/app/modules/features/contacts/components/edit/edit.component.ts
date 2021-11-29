import { Component, ComponentFactoryResolver } from '@angular/core';
import { Subject } from 'rxjs';
import {
  EditScreenAccordionDefinition,
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { AddressComponent } from './components/address/address.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { NotTheSameComponent } from './components/not-the-same/not-the-same.component';
import { OwnershipUpdateComponent } from './components/ownership-update/ownership-update.component';
import { WrApplsComponent } from './components/wr-appls/wr-appls.component';

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

  public reloadHeaderSubject = new Subject();
  public reloadAddressesTable = new Subject();

  public pageDefinition: EditScreenDefinition = {
    header: {
      component: EditHeaderComponent,
      properties: {
        title: 'View / Edit Contact',
        reloadData: this.reloadHeaderSubject.asObservable(),
      },
      events: {
        reloadAddresses: (): void => {
          this.reloadAddressesTable.next(null);
        },
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
        component: AddressComponent,
        title: 'Address / Phone / E-Mail',
        events: {
          reloadAddresses: (): void => {
            this.reloadHeaderSubject.next(null);
          },
        },
        properties: {
          reloadAddressesData: this.reloadAddressesTable.asObservable(),
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.contactStatus = data.contactStatus;
        },
      },
      {
        component: WrApplsComponent,
        title: 'Water Rights / Applications',
        properties: {},
      },
      {
        component: OwnershipUpdateComponent,
        title: 'Ownership Update',
        properties: {},
      },
      {
        component: NotTheSameComponent,
        title: 'Not the Same',
        properties: {},
      },
    ],
  };
}
