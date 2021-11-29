import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { first } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { OwnershipUpdateService } from '../../../../services/ownership-update.service';

@Component({
  selector: 'app-notes',
  templateUrl: './notes.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './notes.component.scss',
  ],
  providers: [OwnershipUpdateService],
})
export class NotesComponent extends DataRowComponent {
  constructor(
    public service: OwnershipUpdateService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'notes',
      title: 'Notes',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(4000)],
    },
  ];

  public dialogWidth = '600px';
  public onFetchData = new Subject();

  initFunction(): void {
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  protected _onGetSuccessHandler(data: any) {
    // Post-process "get" data
    this.data = this._getHelperFunction(data);

    // Post process data to display
    this.displayData = this._getDisplayData(this.data);

    if (data.get.results?.length) {
      this.dataMessage = null;
    } else {
      this.dataMessage = 'No data found';
    }
    this.onFetchData.next();
  }

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    this._get();
    this.onFetchData.pipe(first()).subscribe(() => {
      this.service
        .update(
          {
            ...updatedRow,
            dateReceived: this.data.dateReceived,
            ownershipUpdateType: this.data.ownershipUpdateType,
            ownershipUpdateId: this.data.ownershipUpdateId,
          },
          ...this.idArray
        )
        .subscribe(
          () => {
            this._get();
            this.snackBar.open('Record successfully updated.', null);
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error;
            let message = 'Cannot update record. ';
            message += errorBody.userMessage || ErrorMessageEnum.PUT;
            this.snackBar.open(message);

            // Redisplay the dialog with the input data
            this._displayEditDialog(updatedRow);
          }
        );
    });
  }
}
