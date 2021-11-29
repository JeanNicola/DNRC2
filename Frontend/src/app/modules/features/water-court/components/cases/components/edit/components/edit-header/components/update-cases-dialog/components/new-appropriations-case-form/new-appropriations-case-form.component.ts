import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { SearchResultsDialogComponent } from 'src/app/modules/shared/components/search-results-dialog/search-results-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { SearchService as SearchApplicationsService } from 'src/app/modules/shared/services/search.service';
import { CaseProgramTypes } from '../../../../../../../create/enums/caseProgramTypes';

@Component({
  selector: 'app-new-appropriations-case-form',
  templateUrl: './new-appropriations-case-form.component.html',
  styleUrls: ['./new-appropriations-case-form.component.scss'],
  providers: [SearchApplicationsService],
})
export class NewAppropriationsCaseFormComponent implements OnInit {
  constructor(
    public searchApplicationsService: SearchApplicationsService,
    public dialog: MatDialog
  ) {}

  @ViewChild('searchButton', { static: false }) searchButton: MatButton;

  @Output() onTypeChange = new EventEmitter();
  @Output() onSave = new EventEmitter();
  @Output() onClose = new EventEmitter();
  @Output() onFetch = new EventEmitter();

  @Input() displayFields = null;
  @Input() values;
  @Input() formIsDirty: Boolean = false;

  public mode = DataManagementDialogModes.Update;
  public formGroup: FormGroup = new FormGroup({});
  // Application properties
  private currentApplicationId = null;

  public searchApplicationField: ColumnDefinitionInterface[] = [
    {
      columnId: 'searchApplicationValue',
      title: 'Search Application',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public selectedApplicationColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'applicationBasinSelected',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'applicationIdSelected',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'applicationTypeSelected',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
  ];

  private applicationSearchDisplayColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },

    {
      columnId: 'applicationTypeDescription',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public ngOnInit(): void {
    this.currentApplicationId = this.values?.applicationId || null;

    setTimeout(() => {
      this.setFormInitualValues();
      if (this.formIsDirty) this.formGroup.markAsDirty();
    });
  }

  private setFormInitualValues(): void {
    this.setApplicationValues({
      basin: this.values?.basin,
      applicationId: this.values?.applicationId,
      applicationTypeDescription: this.values?.completeApplicationType,
    });
  }

  private setApplicationValues(application): void {
    this.formGroup.get('applicationBasinSelected').setValue(application?.basin);
    this.formGroup
      .get('applicationIdSelected')
      .setValue(application?.applicationId);
    this.formGroup
      .get('applicationTypeSelected')
      .setValue(application?.applicationTypeDescription);
  }

  public clearApplication(): void {
    this.currentApplicationId = null;
    this.formGroup.get('applicationBasinSelected').setValue(null);
    this.formGroup.get('applicationIdSelected').setValue(null);
    this.formGroup.get('applicationTypeSelected').setValue(null);
    this.formGroup.markAsDirty();
  }

  private applicationRowFormat(dataIn: any) {
    return {
      ...dataIn,
      applicationTypeDescription: `${dataIn.applicationTypeCode} - ${dataIn.applicationTypeDescription}`,
    };
  }

  private displaySearchDialog(): void {
    const searchValues: any = {};
    searchValues.applicationId = this.formGroup.get(
      'searchApplicationValue'
    ).value;

    // Open the dialog
    const dialogRef = this.dialog.open(SearchResultsDialogComponent, {
      width: 'auto',
      data: {
        rowFormatFunction: this.applicationRowFormat,
        title: 'Select an Application',
        searchValues: searchValues,
        searchService: this.searchApplicationsService,
        sortDirection: 'asc',
        sortColumn: 'basin',
        displayColumns: this.applicationSearchDisplayColumns,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== undefined && result !== null) {
        this.currentApplicationId = result.applicationId;
        this.setApplicationValues(result);
        this.formGroup.markAsDirty();
      }
      this.searchButton.focus();
    });
  }

  // Handle the onSearch event
  public onSearch(): void {
    this.displaySearchDialog();
  }

  public _onChange($event) {
    if ($event.fieldName === 'caseType') {
      this.onTypeChange.emit({ ...$event, data: this.getData() });
    }
  }

  private getData() {
    const result: any = {
      caseType: this.formGroup.get('caseType').value,
      caseStatus: this.formGroup.get('caseStatus').value,
      officeId: this.formGroup.get('officeId').value,
      applicationId: this.currentApplicationId,
    };
    return result;
  }

  public save(): void {
    const extraData: any = {
      programType: CaseProgramTypes.NA_PROGRAM,
      basin: this.formGroup.get('applicationBasinSelected').value,
      completeApplicationType: this.formGroup.get('applicationTypeSelected')
        .value,
    };

    this.onSave.emit({ requestData: this.getData(), extraData });
  }
}
