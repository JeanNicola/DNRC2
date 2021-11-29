import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnDestroy } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { ReplaySubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { PhoneEmail } from 'src/app/modules/features/contacts/interfaces/phone-email-interface';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ContactPhoneEmailService } from '../../services/contact-phone-email.service';

@Component({
  selector: 'app-phone-email',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './phone-email.component.scss',
  ],
  providers: [ContactPhoneEmailService],
})
export class PhoneEmailComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  constructor(
    public service: ContactPhoneEmailService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = '';
  public searchable = false;
  public dialogWidth = '400px';
  public currentElectronicId;

  @Input() containerStyles = {};

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'electronicTypeValue',
      title: 'Contact Method',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
      displayInSearch: false,
      noSort: true,
    },
    {
      columnId: 'electronicType',
      title: 'Contact Method',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'electronicValue',
      title: 'Contact Value',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(40)],
      noSort: true,
    },
    {
      columnId: 'electronicNotes',
      title: 'Contact Notes',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(40)],
      noSort: true,
    },
  ];

  initFunction(): void {
    this.dataMessage = 'No data found';
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  public ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  // Override the table row focus
  protected setTableFocus(): void {}

  public populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the getContactMethods http request and the selectArr
    // population only happens once
    this.observables.electronicType = new ReplaySubject(1);
    this.service.getElectronicContactTypes().subscribe((data) => {
      this._getColumn('electronicType').selectArr = data.results.map(
        (electronicType: { description: string; value: string }) => ({
          name: electronicType.description,
          value: electronicType.value,
        })
      );
      this.observables.electronicType.next(data.results);
      this.observables.electronicType.complete();
    });
  }

  // Handle the onInsert event
  public onEdit(updatedData: any, index: number): void {
    this.currentElectronicId = this.rows[index].electronicId;
    super.onEdit(updatedData, index);
  }

  public onDelete(index) {
    this.currentElectronicId = this.rows[index].electronicId;
    super.onDelete(index);
  }

  public _insert(newRow: PhoneEmail): void {
    newRow.customerId = this.route.snapshot.params.id;
    super._insert(newRow);
  }

  private getColumnsWithValidators(validators): ColumnDefinitionInterface[] {
    const columns = [...this.columns].map((column) => ({ ...column }));
    this.getColumn(columns, 'electronicValue').validators = validators;
    return columns;
  }

  protected _update(updatedRow: any): void {
    updatedRow.customerId = this.route.snapshot.params.id;
    this.service
      .update(updatedRow, ...this.idArray, this.currentElectronicId)
      .subscribe(
        (dto) => {
          let messages = ['Record successfully updated.'];
          if (!!dto.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot update record. ';
          message += errorBody.userMessage || ErrorMessageEnum.PUT;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayEditDialog(updatedRow);
        }
      );
  }

  protected _delete(row: number): void {
    this.service.delete(...this.idArray, this.currentElectronicId).subscribe(
      () => {
        this._get();
        this.snackBar.open('Record successfully deleted.');
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot delete record. ';
        message += errorBody.userMessage || ErrorMessageEnum.DELETE;
        this.snackBar.open(message);
      }
    );
  }

  protected getColumn(columns, columnId: string): ColumnDefinitionInterface {
    let index: number;
    for (let i = 0; i < columns.length; i++) {
      if (columns[i].columnId === columnId) {
        index = i;
      }
    }
    return columns[index];
  }

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    const reloadColumns = new Subject<{
      columns: ColumnDefinitionInterface[];
      markAsDirty?;
      markAllAsTouched?;
    }>();
    // Open the dialog
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Add New ${this.title} Record`,
        columns: this.columns,
        values: data,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });

    dialogRef.componentInstance.reloadColumns$ = reloadColumns.asObservable();
    dialogRef.componentInstance.changeEvent.subscribe(($event) => {
      if ($event.source.ngControl.name === 'electronicType') {
        let columns = this.columns;
        if ($event.source.ngControl.value === 'EMAL') {
          columns = this.getColumnsWithValidators([
            Validators.required,
            Validators.email,
          ]);
        }
        reloadColumns.next({
          columns,
          markAsDirty: true,
          markAllAsTouched: true,
        });
      }
    });
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    const reloadColumns = new Subject<{
      columns: ColumnDefinitionInterface[];
      markAsDirty?;
      markAllAsTouched?;
    }>();

    let columns = this.columns;
    if (data.electronicType === 'EMAL') {
      columns = this.getColumnsWithValidators([
        Validators.required,
        Validators.email,
      ]);
    }

    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title} Record`,
        columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(result);
      }
    });

    dialogRef.componentInstance.reloadColumns$ = reloadColumns.asObservable();
    dialogRef.componentInstance.changeEvent
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(($event) => {
        if ($event.source.ngControl.name === 'electronicType') {
          let columns = this.columns;
          if ($event.source.ngControl.value === 'EMAL') {
            columns = this.getColumnsWithValidators([
              Validators.required,
              Validators.email,
            ]);
          }
          reloadColumns.next({
            columns,
            markAsDirty: true,
            markAllAsTouched: true,
          });
        }
      });
  }
}
