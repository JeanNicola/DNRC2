import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { of, ReplaySubject, throwError } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { Office } from 'src/app/modules/shared/components/templates/file-location-processor/file-location-processor.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import {
  ColumnDefinitionInterface,
  SelectionInterface,
} from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ByOfficeService } from '../../../../services/by-office.service';
import { RegionalOfficeService } from '../../../../services/regional-office.service';
import { InterestedPartiesService } from '../../../../services/interested-parties.service';
import { InterestedPartyOfficeSearchDialogComponent } from '../interested-party-office-search-dialog/interested-party-office-search-dialog.component';
import { InterestedPartySelectDialogComponent } from '../interested-party-select-dialog/interested-party-select-dialog.component';

@Component({
  selector: 'app-interested-parties',
  templateUrl: './interested-parties.component.html',
  styleUrls: [
    './interested-parties.component.scss',
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [InterestedPartiesService, RegionalOfficeService, ByOfficeService],
})
export class InterestedPartiesComponent extends BaseCodeTableComponent {
  @Output() interestedPartiesChanged = new EventEmitter();

  constructor(
    public service: InterestedPartiesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public router: Router,
    public officeService: RegionalOfficeService,
    public byOfficeService: ByOfficeService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() public responsibleOfficeId: number = null;

  @ViewChild('officeInsert', { static: false }) officeInsert: MatButton;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'firstLastName',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'contactTypeDescription',
      title: 'Contact Type',
      type: FormFieldTypeEnum.Input,
    },
  ];
  public searchable = false;
  public hideEdit = true;
  public title = '';
  private officeSelections: SelectionInterface[];
  public primarySortColumn = 'firstLastName';

  public searchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'firstLastName',
      title: 'Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'contactTypeDescription',
      title: 'Contact Type',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];

  protected initFunction(): void {
    this._get();
  }

  protected populateDropdowns(): void {
    this.observables.offices = new ReplaySubject(1);
    this.officeService.get({}).subscribe((offices: { results: Office[] }) => {
      const selectArr = offices.results.map((office: Office) => ({
        name: office.description,
        value: office.officeId,
      }));
      this.officeSelections = selectArr;
      this.observables.offices.next(offices);
      this.observables.offices.complete();
    });
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.service
      .insert(this._buildInsertDto(newRow), ...this._buildInsertIdArray(newRow))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.interestedPartiesChanged.next();
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot insert new record. ';
          message += errorBody.userMessage || ErrorMessageEnum.POST;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayInsertDialog(newRow);
        }
      );
  }

  /*
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this._getDeleteService()
      .delete(...this._buildDeleteIdArray(row))
      .subscribe(
        () => {
          this._get();
          this.snackBar.open('Record successfully deleted.');
          this.interestedPartiesChanged.next();
          this._setInitialButtonFocus();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
        }
      );
  }

  protected _buildInsertDto(dto: any): any {
    return { contactId: dto.contactId };
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InterestedPartySelectDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Add New Interested Party',
        columns: this.searchColumns,
        values: data ?? null,
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
  }

  protected onInsertByOffice(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(
      InterestedPartyOfficeSearchDialogComponent,
      {
        width: this.dialogWidth,
        data: {
          title: this.getInsertDialogTitle(),
          columns: this.searchColumns,
          values: data ?? null,
          officeSelectArr: this.officeSelections,
          responsibleOfficeId: this.responsibleOfficeId,
          idArray: this.idArray,
        },
      }
    );

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insertByOffice(result);
      } else {
        this.officeInsert.focus();
      }
    });
  }

  protected _buildOfficeInsertDto(dto: any): any {
    return { contactIds: dto.contactIds, includeAll: dto.includeAll };
  }

  protected _buildOfficeInsertIdArray(dto: any): string[] {
    return [...this.idArray, dto.officeId];
  }

  protected _insertByOffice(newRow: any): void {
    this.byOfficeService
      .insert(
        this._buildOfficeInsertDto(newRow),
        ...this._buildOfficeInsertIdArray(newRow)
      )
      .subscribe(
        (dto) => {
          let messages = ['Record successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.interestedPartiesChanged.next();
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot insert new record. ';
          message += errorBody.userMessage || ErrorMessageEnum.POST;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this.onInsertByOffice(newRow);
        }
      );
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].contactId];
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'contacts', data.contactId]);
  }

  // Override the initial focus
  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}
}
