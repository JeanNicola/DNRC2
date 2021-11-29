import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';

import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { ContactAddressesService } from './services/contact-addresses.service';

@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
  styleUrls: ['./address.component.scss'],
  providers: [ContactAddressesService],
})
export class AddressComponent extends BaseCodeTableComponent {
  @Input() contactStatus = null;
  @Input() reloadAddressesData: Observable<any> = null;
  @Output() reloadAddresses = new EventEmitter();

  constructor(
    public service: ContactAddressesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public containerStyles = {
    width: '100%',
  };

  protected initFunction() {
    this.dataMessage = 'No data found';
    this.idArray = [this.route.snapshot.params.id];
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  public onReloadAddressesHandler() {
    this.reloadAddresses.next(null);
  }
}
