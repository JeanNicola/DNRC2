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
import { CaseProgramTypes } from '../../../../../../../create/enums/caseProgramTypes';
import { DecreeBasinsService } from '../../../../../../../create/services/decree-basins.service';

@Component({
  selector: 'app-water-court-case-form',
  templateUrl: './water-court-case-form.component.html',
  styleUrls: ['./water-court-case-form.component.scss'],
  providers: [DecreeBasinsService],
})
export class WaterCourtCaseFormComponent implements OnInit {
  constructor(
    public decreeBasinsService: DecreeBasinsService,
    public dialog: MatDialog
  ) {}

  @ViewChild('searchButton', { static: false }) searchButton: MatButton;

  @Output() onTypeChange = new EventEmitter();
  @Output() onSave = new EventEmitter();
  @Output() onClose = new EventEmitter();
  @Output() onFetch = new EventEmitter();

  @Input() displayFields = null;
  @Input() values = null;
  @Input() formIsDirty: Boolean = false;

  public mode = DataManagementDialogModes.Update;
  public formGroup: FormGroup = new FormGroup({});
  // Decree properties
  private currentDecreeId = null;

  public searchDecreeField: ColumnDefinitionInterface[] = [
    {
      columnId: 'searchBasinValue',
      title: 'Search Decree Basin',
      type: FormFieldTypeEnum.Input,
    },
  ];
  public selectedDecreeColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'decreeBasinSelected',
      title: 'Decree Basin',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'decreeTypeSelected',
      title: 'Decree Type',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'decreeDateSelected',
      title: 'Decree Issued Date',
      type: FormFieldTypeEnum.Date,
      editable: false,
    },
  ];

  private decreeBasinSearchDisplayColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dctpCodeDescription',
      title: 'Decree Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'issueDate',
      title: 'Decree Issued Date',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public ngOnInit(): void {
    this.currentDecreeId = this.values?.decreeId || null;

    setTimeout(() => {
      this.setFormInitualValues();
      if (this.formIsDirty) this.formGroup.markAsDirty();
    });
  }

  private setFormInitualValues(): void {
    setTimeout(() => {
      this.setDecreeValues({
        basin: this.currentDecreeId,
        dctpCodeDescription: this.values?.decreeTypeDescription,
        issueDate: this.values?.decreeIssueDate,
      });
    });
  }

  private setDecreeValues(decree): void {
    this.formGroup
      .get('decreeTypeSelected')
      .setValue(decree?.dctpCodeDescription);
    this.formGroup.get('decreeDateSelected').setValue(decree?.issueDate);
    this.formGroup.get('decreeBasinSelected').setValue(decree?.basin);
  }

  public clearDecree(): void {
    this.currentDecreeId = null;
    this.formGroup.get('decreeTypeSelected').setValue(null);
    this.formGroup.get('decreeDateSelected').setValue(null);
    this.formGroup.get('decreeBasinSelected').setValue(null);
    this.formGroup.markAsDirty();
  }

  private displaySearchDialog(): void {
    const searchValues: any = {};

    searchValues.basin = this.formGroup.get('searchBasinValue').value;

    // Open the dialog
    const dialogRef = this.dialog.open(SearchResultsDialogComponent, {
      width: 'auto',
      data: {
        title: 'Select a Decree Basin',
        searchValues: searchValues,
        searchService: this.decreeBasinsService,
        sortDirection: 'asc',
        sortColumn: 'basin',
        displayColumns: this.decreeBasinSearchDisplayColumns,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== undefined && result !== null) {
        this.currentDecreeId = result.decreeId;
        this.setDecreeValues(result);

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
      decreeId: this.currentDecreeId,
      waterCourtCaseNumber: this.formGroup.get('waterCourtCaseNumber').value,
    };

    return result;
  }

  public save(): void {
    const extraData: any = {
      programType: CaseProgramTypes.WC_PROGRAM,
      decreeBasin: this.formGroup.get('decreeBasinSelected').value,
      decreeTypeDescription: this.formGroup.get('decreeTypeSelected').value,
      decreeIssueDate: this.formGroup.get('decreeDateSelected').value,
    };

    this.onSave.emit({ requestData: this.getData(), extraData });
  }
}
