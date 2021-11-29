import { ViewportScroller } from '@angular/common';
import { Component, ComponentFactoryResolver, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import {
  EditScreenAccordionDefinition,
  EditScreenComponent,
  EditScreenDefinition,
} from 'src/app/modules/shared/components/templates/edit-screen/edit-screen.component';
import { ApplicationsComponent } from './components/applications/applications.component';
import { CasesComponent } from './components/cases/cases.component';
import { CompactsComponent } from './components/compacts/compacts.component';
import { DecreesComponent } from './components/decrees/decrees-table.component';
import { EditVersionHeaderComponent } from './components/edit-header/edit-header.component';
import { HistoricalComponent } from './components/historical/historical.component';
import { MeasurementReportsComponent } from './components/measurement-reports/measurement-reports.component';
import { ObjectionsForVersionsComponent } from './components/objections-for-versions/objections-for-versions.component';
import { PointOfDiversionComponent } from './components/point-of-diversion/point-of-diversion.component';
import { PurposePlaceOfUseComponent } from './components/purpose-place-of-use/purpose-place-of-use.component';
import { RelatedRightsComponent } from './components/related-rights/related-rights.component';
import { RemarksComponent } from './components/remarks/remarks.component';
import { ReservoirComponent } from './components/reservoir/reservoir.component';

@Component({
  selector: 'app-edit',
  templateUrl:
    '../../../../shared/components/templates/edit-screen/edit-screen.component.html',
  styleUrls: [
    '../../../../shared/components/templates/edit-screen/edit-screen.component.scss',
  ],
})
export class EditVersionsComponent
  extends EditScreenComponent
  implements OnInit
{
  constructor(
    componentFactoryResolver: ComponentFactoryResolver,
    private scroller: ViewportScroller,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(componentFactoryResolver);
  }

  ngOnInit(): void {
    if (
      !this.route.snapshot.data.purposesLoaded ||
      !this.route.snapshot.data.podLoaded
    ) {
      void this.router.navigate(['/error']);
    }
  }

  private adjudicationVersions = [
    'ORIG',
    'POST',
    'SPLT',
    'SPPD',
    'REXM',
    'FINL',
  ];

  private invalidWRTypes = [
    '62GW',
    'CDWR',
    'EXEX',
    'GWCT',
    'NAPP',
    'PRPM',
    'STWP',
    'TPRP',
    'WRWR',
    'NFWP',
    'DMAL',
  ];

  private validWRTypes = ['IRRD', 'RSCL', 'STOC', 'PRDL'];

  private changeVersions = ['CHAU', 'CHSP', 'ERSV', 'REDU', 'REDX'];

  private scrollFlag = false;

  private reloadHeaderData = new Subject();
  public podsUpdated = new Subject();

  public pageDefinition: EditScreenDefinition = {
    header: {
      component: EditVersionHeaderComponent,
      properties: {
        title: 'View / Edit Water Rights Version Details',
        reloadHeaderData: this.reloadHeaderData.asObservable(),
      },
      events: {
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
        scrollApplicationEvent: (): void => {
          const index = this.pageDefinition.accordions.findIndex(
            (a: EditScreenAccordionDefinition) => a.title === 'Applications'
          );
          if (this.accordionInstances.get(index).expanded) {
            this.scroller.scrollToAnchor('applications-anchor');
          } else {
            this.accordionInstances.get(index).open();
            this.scrollFlag = true;
          }
        },
      },
    },
    accordions: [
      {
        component: PointOfDiversionComponent,
        title: 'Point of Diversion (POD) Details',
        expanded: this.route.snapshot.queryParamMap.get('true') === null,
        properties: {},
        events: {
          podsUpdated: () => {
            this.podsUpdated.next();
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
        component: PurposePlaceOfUseComponent,
        title: 'Purpose / Place of Use (POU)',
        properties: {},
        expanded: this.route.snapshot.queryParamMap.get('true') !== null,
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.headerData = data;
        },
      },
      {
        component: ReservoirComponent,
        title: 'Reservoir / Pit',
        properties: {},
        condensed: true,
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId, data.version];
          thisAccordion.properties.isDecreed = data.isDecreed;
          thisAccordion.properties.isEditableIfDecreed =
            data.isEditableIfDecreed;
          thisAccordion.properties.canEdit = data.canEdit;
          thisAccordion.properties.podsUpdated = this.podsUpdated;
        },
      },
      {
        component: RemarksComponent,
        title: 'Remarks',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.isDecreed = data.isDecreed;
          thisAccordion.properties.isEditableIfDecreed =
            data.isEditableIfDecreed;
          thisAccordion.properties.canEdit = data.canEdit;
        },
      },
      {
        component: HistoricalComponent,
        title: 'Priority / Historical Data',
        properties: {},
        events: {
          headerUpdates: () => {
            this.reloadHeaderData.next();
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.waterRightTypeCode = data.waterRightTypeCode;
          thisAccordion.properties.versionTypeCode = data.versionTypeCode;
          thisAccordion.properties.isDecreed = data.isDecreed;
          thisAccordion.properties.isEditableIfDecreed =
            data.isEditableIfDecreed;
          thisAccordion.properties.canEdit = data.canEdit;
        },
      },
      {
        component: RelatedRightsComponent,
        title: 'Related Rights',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          if (this.invalidWRTypes.includes(data.waterRightTypeCode)) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
          }
        },
      },
      {
        component: DecreesComponent,
        title: 'Decrees',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          if (
            this.invalidWRTypes.includes(data.waterRightTypeCode) ||
            (this.changeVersions.includes(data.versionTypeCode) &&
              this.validWRTypes.includes(data.waterRightTypeCode))
          ) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
          }
        },
      },
      {
        component: ObjectionsForVersionsComponent,
        title: 'Objections',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          if (
            this.invalidWRTypes.includes(data.waterRightTypeCode) ||
            (this.changeVersions.includes(data.versionTypeCode) &&
              this.validWRTypes.includes(data.waterRightTypeCode))
          ) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
          }
        },
      },
      {
        component: CasesComponent,
        title: 'Cases',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          if (
            this.invalidWRTypes.includes(data.waterRightTypeCode) ||
            (this.changeVersions.includes(data.versionTypeCode) &&
              this.validWRTypes.includes(data.waterRightTypeCode))
          ) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
          }
        },
      },
      {
        component: CompactsComponent,
        title: 'Compacts',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          thisAccordion.properties.idArray = [data.waterRightId, data.version];
          thisAccordion.properties.isDecreed = data.isDecreed;
          thisAccordion.properties.isEditableIfDecreed =
            data.isEditableIfDecreed;
          thisAccordion.properties.canEdit = data.canEdit;
          thisAccordion.properties.canCompact = data.canCompact;
        },
      },
      {
        component: MeasurementReportsComponent,
        title: 'Measurement Reports',
        properties: {},
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          if (
            ['ITSC', 'NNAD', 'CMPT'].includes(data.waterRightTypeCode) ||
            (this.adjudicationVersions.includes(data.versionTypeCode) &&
              ['IRRD', 'RSCL', 'STOC', 'PRDL'].includes(
                data.waterRightTypeCode
              ))
          ) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
            thisAccordion.properties.idArray = [
              data.waterRightId,
              data.version,
            ];
            thisAccordion.properties.isDecreed = data.isDecreed;
            thisAccordion.properties.isEditableIfDecreed =
              data.isEditableIfDecreed;
            thisAccordion.properties.canEdit = data.canEdit;
            thisAccordion.properties.operatingAuthorityDate =
              data.operatingAuthority;
          }
        },
      },
      {
        component: ApplicationsComponent,
        title: 'Applications',
        properties: {},
        events: {
          applicationLoaded: () => {
            if (this.scrollFlag) {
              setTimeout(() => {
                this.scroller.scrollToAnchor('applications-anchor');
              });
              this.scrollFlag = false;
            }
          },
          applicationChanged: () => {
            this.reloadHeaderData.next(null);
          },
        },
        onParentData: (
          index: number,
          thisAccordion: EditScreenAccordionDefinition,
          data: any
        ): void => {
          if (
            ['ITSC', 'NNAD', 'CMPT'].includes(data.waterRightTypeCode) ||
            (this.adjudicationVersions.includes(data.versionTypeCode) &&
              ['IRRD', 'RSCL', 'STOC', 'PRDL'].includes(
                data.waterRightTypeCode
              ))
          ) {
            this.accordionInstances.get(index).close();
            thisAccordion.disabled = true;
          } else {
            thisAccordion.disabled = false;
            thisAccordion.properties.idArray = [
              data.waterRightId,
              data.version,
            ];
            thisAccordion.properties.isDecreed = data.isDecreed;
            thisAccordion.properties.isEditableIfDecreed =
              data.isEditableIfDecreed;
            thisAccordion.properties.canEdit = data.canEdit;
            thisAccordion.properties.waterRightBasin = data.basin;
            thisAccordion.properties.showScannedUrl =
              data.version > 1 &&
              (data.versionTypeCode.includes('CHAU') ||
                data.versionTypeCode.includes('REDU'));
          }
        },
      },
    ],
  };
}
