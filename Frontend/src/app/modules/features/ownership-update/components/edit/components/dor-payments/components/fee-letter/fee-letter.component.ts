import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import * as moment from 'moment';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { FeeLetterServiceService } from './services/fee-letter-service.service';

@Component({
  selector: 'app-fee-letter',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    './fee-letter.component.scss',
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [FeeLetterServiceService],
})
export class FeeLetterComponent extends DataRowComponent {
  constructor(
    public service: FeeLetterServiceService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  protected initFunction(): void {
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  public showLoading = false;
  public title = 'Insufficient Fee Letter';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'dateSent',
      title: 'Sent Date',
      type: FormFieldTypeEnum.Date,
      validators: [WRISValidators.dateBeforeToday],
    },
    {
      columnId: 'dateSentPlus30',
      title: '+30 Days',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
      editable: false,
    },
  ];

  protected _getHelperFunction(data: any): { [key: string]: any } {
    let dateSentPlus30;
    if (data.get.dateSent) {
      const sentDate = moment(data.get.dateSent);
      dateSentPlus30 = sentDate.add(30, 'days');
    }
    return { ...data.get, dateSentPlus30 };
  }

  protected setPermissions() {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0),
    };
  }
}
