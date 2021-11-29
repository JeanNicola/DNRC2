import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ObjectorsRepresentativeDialogComponent } from 'src/app/modules/features/applications/components/edit/components/objections/components/objectors-representative-dialog/objectors-representative-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ApplicationsObjectionsObjectorsService } from '../../services/applications-objections-objectors.service';

@Component({
  selector: 'app-objections-objectors',
  templateUrl: '../../templates/objections-subtable.html',
  styleUrls: [
    '../../../templates/code-table/code-table.template.scss',
    '../../templates/objections-subtable.scss',
  ],
  providers: [ApplicationsObjectionsObjectorsService],
})
export class ObjectionsObjectorsComponent extends BaseCodeTableComponent {
  // This block sets the value of idArray when idArray is updated.
  // Clear existing data when a null objectionId is received
  @Input() set idArray(value: string[]) {
    this._idArray = value;
    if (value.includes(null)) {
      this.data = null;
      this.rows = null;
      this.dataMessage = 'No data found';
    } else {
      this._get();
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public hideActions = true;
  public zHeight = 1;
  public title = 'Objectors';
  public primarySortColumn = 'contactId';
  public sortDirection = 'desc';
  protected clickableRow = true;
  protected dblClickableRow = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID#',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'name',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
    },
    {
      columnId: 'more',
      title: 'Representatives',
      type: FormFieldTypeEnum.Input,
      showCounter: true,
      counterRef: 'representativeCount',
      noSort: true,
    },
  ];

  constructor(
    public service: ApplicationsObjectionsObjectorsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  // Override the initial focus
  protected setTableFocus(): void {}

  public cellClick(data: any): void {
    if (
      data?.columnId === 'more' &&
      this.rows[data.row]?.representativeCount > 0
    ) {
      this.dialog.open(ObjectorsRepresentativeDialogComponent, {
        width: '700px',
        data: {
          idArray: [...this.idArray, this.rows[data.row].contactId],
          name: this.rows[data.row].name,
          contactId: this.rows[data.row].contactId,
        },
      });
    }
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'contacts', data.contactId]);
  }
}
