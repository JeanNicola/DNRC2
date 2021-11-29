import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

import { HistoricalService } from './services/historical.service';

export type Reference = {
  value: string;
  description: string;
};

export type Historical = {
  priorityDate?: string;
  priorityDateOrigin?: string;
  enforceableDate?: string;
  adjudicationProcess?: string;
  flowRate?: number;
  flowRateUnit?: string;
  divertedVolume?: number;
  consumptiveVolume?: number;
  dateReceived?: string;
  lateDesignation?: string;
  feeReceived?: boolean;
  impliedClaim?: boolean;
  exemptClaim?: boolean;
  county?: string;
  caseNumber?: string;
  filingDate?: string;
  rightType?: string;
  rightTypeOrigin?: string;
  decreeAppropriator?: string;
  source?: string;
  decreedMonth?: number;
  decreedDay?: number;
  decreedYear?: number;
  minersInches?: number;
  flowDescription?: string;
};

export type HistoricalData = {
  record: Historical;
  rightTypes: Reference[];
  elementOrigins: Reference[];
  adjudicationProcesses: Reference[];
  flowRateUnits: Reference[];
};

@Component({
  selector: 'app-historical',
  templateUrl: './historical.component.html',
  styleUrls: ['./historical.component.scss'],
  providers: [HistoricalService],
})
export class HistoricalComponent implements OnInit {
  @Input() waterRightTypeCode: string | undefined = undefined;
  @Input() versionTypeCode: string | undefined = undefined;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = false;

  @Output() headerUpdates: EventEmitter<boolean> = new EventEmitter<boolean>();

  public data = new BehaviorSubject<HistoricalData | null>(null);
  public validWRType = true;

  constructor(
    public service: HistoricalService,
    private route: ActivatedRoute
  ) {}

  private invalidWRTypes = [
    '62GW',
    'CDWR',
    'GWCT',
    'NAPP',
    'PRPM',
    'STWP',
    'TPRP',
    'WRWR',
    'NFWP',
    'DMAL',
  ];

  ngOnInit(): void {
    this.validWRType = !this.invalidWRTypes.includes(this.waterRightTypeCode);
    const { waterRightId, versionId } = this.route.snapshot.params;
    this.service
      .get(waterRightId, versionId)
      .subscribe((data: any) => this.data.next(data));
  }
  public onReloadHeader(): void {
    this.headerUpdates.emit(true);
  }
}
