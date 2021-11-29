import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';
import { OwnershipUpdatePaymentsService } from './services/ownership-update-payments.service';

@Component({
  selector: 'app-dor-payments',
  templateUrl: './dor-payments.component.html',
  styleUrls: ['./dor-payments.component.scss'],
  providers: [OwnershipUpdatePaymentsService],
})
export class DorPaymentsComponent implements OnInit {
  constructor(
    public service: OwnershipUpdatePaymentsService,
    public endpointService: EndpointsService,
    private route: ActivatedRoute
  ) {}

  @Output() dataChanged = new EventEmitter();

  @Input() reloadPaymentsData: Observable<any> = null;

  @Input() set ownershipUpdateDateReceived(value) {
    this._ownershipUpdateDateReceived = value;
  }
  public _ownershipUpdateDateReceived;

  @Input() set ownershipUpdateDateProcessed(value) {
    this._ownershipUpdateDateProcessed = value;
  }
  public _ownershipUpdateDateProcessed;

  @Input() set ownershipUpdateDateTerminated(value) {
    this._ownershipUpdateDateTerminated = value;
  }
  public _ownershipUpdateDateTerminated;

  protected observables: { [key: string]: ReplaySubject<unknown> } = {};
  public refreshFeeSummaryOnChange = Math.random() + Date.now();
  public paymentsWereLoadedTheFirstTime;
  public paymentsData;
  public totalDue = 0;
  private queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public permissions: PermissionsInterface = {
    canGET: false,
    canPOST: false,
    canDELETE: false,
    canPUT: false,
  };

  public ngOnInit() {
    this.setPermissions();
    this.getPaymentData(this.queryParameters);
  }

  public onFeeDueChanged() {
    this.dataChanged.emit(null);
  }

  public onPaymentChangedHandlerHandler(
    queryParameters: DataQueryParametersInterface
  ): void {
    this.getPaymentData(queryParameters);
  }

  private getPaymentData(queryParameters: DataQueryParametersInterface): void {
    this.refreshFeeSummaryOnChange = Math.random() + Date.now();
    if (this.paymentsWereLoadedTheFirstTime) {
      this.dataChanged.emit(null);
    }
    this.paymentsWereLoadedTheFirstTime = true;

    this.service
      .get(queryParameters, this.route.snapshot.params.id)
      .subscribe((data) => {
        if (data) {
          this.paymentsData = { ...data, results: { details: data.results } };
        }
      });
  }

  protected setPermissions(): void {
    this.permissions = {
      canGET: this.endpointService.canGET(this.service.url),
      canPOST: this.endpointService.canPOST(this.service.url),
      canDELETE: this.endpointService.canDELETE(this.service.url),
      canPUT: this.endpointService.canPUT(this.service.url),
    };
  }
}
