import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { forkJoin, ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { PermissionsInterface } from 'src/app/modules/shared/interfaces/permissions.interface';
import { ApplicationTypesService } from '../../services/application-types.service';
import {
  ApplicationType,
  Basin,
} from '../edit/components/edit-header/edit-header.component';
import { ApplicationConstants } from '../../../../shared/constants/application-constants';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { BasinsService } from 'src/app/modules/shared/services/basins.service';
import { InsertApplicantComponent } from 'src/app/modules/shared/components/insert-applicant/insert-applicant.component';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss'],
  providers: [ApplicationTypesService, BasinsService],
})
export class CreateComponent
  extends DataManagementDialogComponent
  implements OnInit
{
  public title = 'Create New Application';
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Autocomplete,
      validators: [Validators.required],
    },
    {
      columnId: 'applicationTypeCode',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Autocomplete,
      validators: [Validators.required],
    },
    {
      columnId: 'dateTimeReceived',
      title: 'Date/Time Received',
      type: FormFieldTypeEnum.DateTime,
      validators: [Validators.required, WRISValidators.dateBeforeToday],
    },
    {
      columnId: 'contactIds',
      title: 'Applicant',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
      list: [
        {
          columnId: 'contactId',
          title: 'Contact ID',
          type: FormFieldTypeEnum.Input,
        },
        {
          columnId: 'lastName',
          title: 'Last Name',
          type: FormFieldTypeEnum.Input,
          displayInTable: false,
        },
        {
          columnId: 'firstName',
          title: 'First Name',
          type: FormFieldTypeEnum.Input,
          displayInTable: false,
        },
        {
          columnId: 'name',
          title: 'Name',
          type: FormFieldTypeEnum.Input,
          displayInSearch: false,
        },
      ],
    },
  ];

  // Used to reset the focus to the insert button
  @ViewChild('insert', { read: ElementRef }) private insertButton: ElementRef;

  public mode = DataManagementDialogModes.Insert;
  // public formGroup: FormGroup = new FormGroup({});

  protected observables: { [key: string]: ReplaySubject<any> } = {};
  public loaded = false;
  public permissions: PermissionsInterface = {
    canDELETE: true,
    canPUT: false,
    canPOST: false,
    canGET: true,
  };

  public applicants = [];
  public dataSource = new MatTableDataSource(this.applicants);
  private dialogWidth = '650px';

  constructor(
    public dialogRef: MatDialogRef<CreateComponent>,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private applicationTypesService: ApplicationTypesService,
    private basinsService: BasinsService
  ) {
    super(dialogRef);
  }

  public initFunction(): void {
    this.populateDropdowns();
    forkJoin({ ...this.observables }).subscribe((data) => {
      this.loaded = true;
    });
  }

  public populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the getPrograms http request and the selectArr
    // population only happens once
    this.observables.appTypes = new ReplaySubject(1);
    this.applicationTypesService
      .getAll()
      .subscribe((appTypes: { results: ApplicationType[] }) => {
        this.getColumn('applicationTypeCode').selectArr = appTypes.results
          .filter(
            (appType: ApplicationType) =>
              !ApplicationConstants.disallowedCreationTypes.includes(
                appType.code
              )
          )
          .map((appType: ApplicationType) => ({
            name: `${appType.code} - ${appType.description}`,
            value: appType.code,
          }));
        this.getColumn('applicationTypeCode').validators.push(
          WRISValidators.matchToSelectArray(
            this.getColumn('applicationTypeCode').selectArr
          )
        );
        this.observables.appTypes.next(appTypes);
        this.observables.appTypes.complete();
      });

    this.observables.basins = new ReplaySubject(1);
    this.basinsService.getAll().subscribe((basins: { results: Basin[] }) => {
      this.getColumn('basin').selectArr = basins.results.map((basin) => ({
        value: basin.code,
        name: `${basin.code} - ${basin.description}`,
      }));
      this.getColumn('basin').validators.push(
        WRISValidators.matchToSelectArray(this.getColumn('basin').selectArr)
      );
      this.observables.basins.next(basins);
      this.observables.basins.complete();
    });
  }

  protected setFocus(): void {
    forkJoin({
      open: this.dialogRef.afterOpened(),
      ...this.observables,
    }).subscribe(() => {
      // for the race condition with the ngIf in this dialog
      setTimeout(() => {
        for (const item of this.formFields) {
          if (item.isNotDisabled()) {
            item.initFocus();
            break;
          }
        }
      }, 0);
    });
  }

  public onDelete(row: number): void {
    this.applicants.splice(row, 1);
    this.dataSource._updateChangeSubscription();
  }

  public save(): any {
    const data = { ...this.formGroup.getRawValue() };
    data.contactIds = this.applicants.map((owner) => owner.contactId);
    this.dialogRef.close(data);
  }

  public onInsertApplicant(): void {
    this._displayInsertDialog(null);
  }

  private _displayInsertDialog(data: any): void {
    const dialogRef = this.dialog.open(InsertApplicantComponent, {
      width: this.dialogWidth,
      data: {
        columns: this.getColumn('contactIds').list,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.contactId != null) {
        const contactIds = this.applicants.map((app) => app.contactId);
        if (contactIds.indexOf(result.contactId) === -1) {
          this.applicants.push(result);
          this.dataSource.data = this.applicants;
        } else {
          this._displayInsertDialog(data);
          this.snackBar.open(`${result.name} is already selected`);
        }
      }
      // set the focus on the insert button...
      if (this.insertButton) {
        this.insertButton.nativeElement.focus();
      }
    });
  }

  public getColumn(columnId: string): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = this.columns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }
}
