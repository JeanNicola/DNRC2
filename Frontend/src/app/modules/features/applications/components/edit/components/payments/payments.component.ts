import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  OnDestroy,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { ReplaySubject, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';
import { FeeStatusService } from './services/fee-status.service';
import { PaymentsService } from './services/payments.service';
import { EditApplicationInterface } from '../../edit.component';

@Component({
  selector: 'app-payments',
  templateUrl: './payments.component.html',
  styleUrls: ['./payments.component.scss'],
  providers: [PaymentsService, FeeStatusService],
})
export class PaymentsComponent implements OnInit, OnDestroy {
  @Input() reloadPayments: Observable<any> = null;
  @Output() reloadEvents = new EventEmitter();
  @Output() paymentsChanged = new EventEmitter();

  private unsubscribe = new Subject();
  private _appData: EditApplicationInterface | null = null;

  private queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  @Input() set appData(appData: EditApplicationInterface) {
    this._appData = appData;
    this.getPaymentData(this.queryParameters);
  }

  get appData(): EditApplicationInterface | null {
    return this._appData;
  }

  public permissions: PermissionsInterface = {
    canGET: false,
    canPOST: false,
    canDELETE: false,
    canPUT: false,
  };
  // protected observables: { [key: string]: Observable<object> } = {};
  protected observables: { [key: string]: ReplaySubject<unknown> } = {};

  public paymentsData;

  constructor(
    public service: PaymentsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public feeStatusService: FeeStatusService,
    private route: ActivatedRoute
  ) {}

  url = '/applications/{applicationId}/payments';
  title = '';
  paging = true;

  public ngOnInit(): void {
    this.setPermissions();
    this.reloadPayments.pipe(takeUntil(this.unsubscribe)).subscribe(() => {
      this.getPaymentData(this.queryParameters);
    });
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  protected setPermissions(): void {
    this.permissions = {
      canGET: this.endpointService.canGET(this.service.url),
      canPOST: this.endpointService.canPOST(this.service.url),
      canDELETE: this.endpointService.canDELETE(this.service.url),
      canPUT: this.endpointService.canPUT(this.service.url),
    };
  }

  public onChange(params: DataQueryParametersInterface): void {
    this.paymentsChanged.next();
    this.getPaymentData(params);
  }

  private getPaymentData(params: DataQueryParametersInterface): void {
    this.service
      .get(params, this.route.snapshot.params.id)
      .subscribe((data) => {
        if (data) {
          this.paymentsData = data;
        }
      });
  }

  public passEventReload(): void {
    this.reloadEvents.next();
  }
}
