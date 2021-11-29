import { Component, ComponentFactoryResolver } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import {
  EditScreenAccordionDefinition,
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { VersionService } from '../../services/version.service';
import { CompactsComponent } from './components/compacts/compacts.component';
import { EditHeaderComponent } from './components/edit-header/edit-header.component';
import { OwnersComponent } from './components/owners/owners.component';
import { FileLocationOfficeComponent } from './components/file-location-office/file-location-office.component';
import { WaterRightVersionsComponent } from './components/water-right-versions/water-right-versions.component';
import { ConservationComponent } from './components/conservation/conservation.component';
import { OwnershipUpdatesComponent } from './components/ownership-updates/ownership-updates.component';
import { GeocodeComponent } from './components/geocode/geocode.component';

@Component({
  selector: 'app-edit',
  templateUrl:
    '../../../../shared/components/templates/edit-screen/edit-screen.component.html',
  styleUrls: [
    '../../../../shared/components/templates/edit-screen/edit-screen.component.scss',
  ],
  providers: [VersionService],
})
export class EditComponent extends EditScreenComponent {
  constructor(componentFactoryResolver: ComponentFactoryResolver) {
    super(componentFactoryResolver);
  }

  private reloadHeader: Subject<void> = new Subject();

  pageDefinition: EditScreenDefinition = {
    header: {
      component: EditHeaderComponent,
      properties: {
        title: 'View / Edit Water Right',
        reloadHeader: this.reloadHeader,
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
        component: WaterRightVersionsComponent,
        title: 'Version',
        properties: {},
        events: {
          reloadEvent: () => {
            this.reloadHeader.next();
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId];
          thisAccordion.properties.waterRightTypeCode = data.typeCode;
          thisAccordion.properties.isDecreed = data.isDecreed;
          thisAccordion.properties.isEditableIfDecreed =
            data.isEditableIfDecreed;
        },
      },
      {
        component: OwnersComponent,
        title: 'Ownership',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId];
          thisAccordion.properties.isEditableIfDecreed =
            data.isEditableIfDecreed;
          thisAccordion.properties.isDecreed = data.isDecreed;
          thisAccordion.properties.canEdit = data.isEditableIfDecreed;
          thisAccordion.properties.waterRightType = data.typeCode;
        },
      },
      {
        component: OwnershipUpdatesComponent,
        title: 'Ownership Update',
        properties: {},
        events: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId];
        },
      },
      {
        component: GeocodeComponent,
        title: 'Geocodes',
        properties: {},
        events: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId];
          thisAccordion.properties.headerData = data;

          if (
            ['ACTV', 'TERM'].includes(data.statusCode) &&
            data.typeCode === 'CMPT'
          ) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
          }
        },
      },
      {
        component: FileLocationOfficeComponent,
        title: 'File Location and Responsible Office',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId];
          thisAccordion.properties.date = data.createdDate;
          thisAccordion.properties.isDecreed = data.isDecreed;
          thisAccordion.properties.isEditableIfDecreed =
            data.isEditableIfDecreed;

          if (
            [
              '62GW',
              'HDRT',
              'ITSC',
              'IRRD',
              'ITSC',
              'NNAD',
              'PRDL',
              'RSCL',
              'STOC',
              'CMPT',
            ].includes(data.typeCode)
          ) {
            thisAccordion.disabled = false;
          } else {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          }
        },
      },
      {
        component: ConservationComponent,
        title: 'Water Reservation and Conservation District',
        properties: {},
        events: {
          reloadEvent: () => {
            this.reloadHeader.next();
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId];
          thisAccordion.properties.headerData = data;
        },
      },
      {
        component: CompactsComponent,
        title: 'Compacts',
        properties: {},
        events: {
          reloadEvent: () => {
            this.reloadHeader.next();
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId];
          thisAccordion.properties.canEdit = data.canCompactType;
          thisAccordion.properties.headerData = data;
        },
      },
    ],
  };
}
