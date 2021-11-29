import { Component, ComponentFactoryResolver } from '@angular/core';
import { Subject } from 'rxjs';
import {
  EditScreenAccordionDefinition,
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { DorPaymentsComponent } from './components/dor-payments/dor-payments.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { FileLocationAndProcessorComponent } from './components/file-location-and-processor/file-location-and-processor.component';
import { NotesComponent } from './components/notes/notes.component';
import { TransferDetailsComponent } from './components/transfer-details/transfer-details.component';

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

  public reloadPaymentsTable = new Subject();
  public reloadHeaderData = new Subject();

  public pageDefinition: EditScreenDefinition = {
    header: {
      component: EditHeaderComponent,
      properties: {
        title: 'View / Edit Ownership Update',
        reloadHeader: this.reloadHeaderData.asObservable(),
      },
      events: {
        errorEvent: (): void => {
          this.error = true;
        },
        reloadPayments: (): void => {
          this.reloadPaymentsTable.next(null);
        },
        dataEvent: (data): void => {
          this.dataWasFound = true;
          this.error = false;

          setTimeout(() => {
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
        component: TransferDetailsComponent,
        title: 'Transfer Details',
        expanded: true,
        properties: {
          ownershipUpdateDateProcessed: null,
          ownershipUpdateDateTerminated: null,
        },
        events: {
          feeDueChanged: () => {
            this.reloadPaymentsTable.next(null);
          },
          dataChanged: () => {
            this.reloadHeaderData.next(null);
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.ownershipUpdateDateProcessed =
            data.dateProcessed;
          thisAccordion.properties.ownershipUpdateDateTerminated =
            data.dateTerminated;
          thisAccordion.properties.ownershipUpdateType =
            data.ownershipUpdateType;
        },
      },
      {
        component: FileLocationAndProcessorComponent,
        title: 'File Location and Processor',
        properties: {
          date: null,
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          // If the Ownership Type is ADM, then close the 2nd expansion panel
          // (File Location and Processor) and disable it. Otherwise enable it
          if (data?.ownershipUpdateType === 'ADM') {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
          }

          thisAccordion.properties.date = data.dateReceived;
        },
      },
      {
        component: DorPaymentsComponent,
        title: '608 / DOR Payments',
        properties: {
          reloadPaymentsData: this.reloadPaymentsTable.asObservable(),
        },
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
          // If the Ownership Type is not either DOR 608 or 608, then close the 3rd expansion panel
          // (608 / DOR Payments) and disable it. Otherwise enable it
          if (!['DOR 608', '608'].includes(data.ownershipUpdateType)) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.properties.ownershipUpdateDateReceived =
              data.dateReceived;
            thisAccordion.properties.ownershipUpdateDateProcessed =
              data.dateProcessed;
            thisAccordion.properties.ownershipUpdateDateTerminated =
              data.dateTerminated;

            thisAccordion.disabled = false;
          }
        },
      },
      {
        component: NotesComponent,
        title: 'Notes',
        properties: {},
      },
    ],
  };
}
