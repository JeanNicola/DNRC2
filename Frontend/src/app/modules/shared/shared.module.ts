// Essentials
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Material Modules
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import {
  MatTooltipModule,
  MAT_TOOLTIP_DEFAULT_OPTIONS,
} from '@angular/material/tooltip';
import { MatRadioModule } from '@angular/material/radio';
import {
  MatDialogModule,
  MAT_DIALOG_DEFAULT_OPTIONS,
} from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatExpansionModule } from '@angular/material/expansion';
import { TextFieldModule } from '@angular/cdk/text-field';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatStepperModule } from '@angular/material/stepper';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MatMomentDateModule,
  MAT_MOMENT_DATE_ADAPTER_OPTIONS,
} from '@angular/material-moment-adapter';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTabsModule } from '@angular/material/tabs';

// Components
import { DeleteDialogComponent } from './components/dialogs/delete-dialog/delete-dialog.component';
import { LeavePageDialogComponent } from './components/dialogs/leave-page-dialog/leave-page-dialog.component';
import { InsertDialogComponent } from './components/dialogs/data-management/components/insert-dialog.component';
import { SearchDialogComponent } from './components/dialogs/data-management/components/search-dialog.component';
import { UpdateDialogComponent } from './components/dialogs/data-management/components/update-dialog.component';
import { DataTableComponent } from './components/data-table/data-table';
import { DataManagementFormFieldComponent } from './components/data-form-field/data-form-field.component';
import { BaseCodeTableComponent } from './components/templates/code-table/code-table.template';
import { DataRowComponent } from './components/templates/data-row/data-row.component';
import { DataRowFieldComponent } from './components/templates/data-row/components/data-row-field/data-row-field.component';
import { DataFieldErrorsComponent } from './components/data-field-errors/data-field-errors.component';
import { MoreInfoDialogComponent } from './components/dialogs/data-management/components/more-info-dialog/more-info-dialog.component';
import { ReadOnlyFieldComponent } from './components/dialogs/data-management/components/read-only-field/read-only-field.component';
import { EditScreenComponent } from './components/templates/edit-screen/edit-screen.component';

// Constants
import { customTooltipConfig } from './constants/custom-tooltip-config';
import { customDialogConfig } from './constants/custom-dialog-config';
import {
  customDateFormats,
  customDateOptions,
} from './constants/date-picker-formats';

// Pipes
import { DecimalPipe } from '@angular/common';
import { DateTimeFormFieldComponent } from './components/date-time-form-field/date-time-form-field.component';
import { SpinnerComponent } from './components/http-spinner/http-spinner.component';
import { FileLocationProcessorComponent } from './components/templates/file-location-processor/file-location-processor.component';
import { ResponsibleOfficeComponent } from './components/templates/file-location-processor/components/responsible-office/responsible-office.component';
import { ProcessorComponent } from './components/templates/file-location-processor/components/processor/processor.component';
import { StaffComponent } from './components/templates/file-location-processor/components/staff/staff.component';
import { OfficeComponent } from './components/templates/file-location-processor/components/office/office.component';
import { AffectedWaterRightsComponent } from './components/affected-water-rights/affected-water-rights.component';
import { AffectedChangeApplicationsComponent } from './components/affected-change-applications/affected-change-applications.component';
import { AddApplicationDialogComponent } from './components/affected-change-applications/components/add-application-dialog/add-application-dialog.component';
import { InsertWaterRightComponent } from './components/affected-water-rights/components/insert-water-right/insert-water-right.component';
import { SelectionCodeTableComponent } from './components/templates/selection-code-table/selection-code-table.component';
import { SelectionDataTableComponent } from './components/templates/selection-data-table/selection-data-table.component';
import { ConfirmationDialogComponent } from './components/dialogs/confirmation-dialog/confirmation-dialog.component';
import { ErrorDialogComponent } from './components/dialogs/error-dialog/error-dialog.component';
import { PaymentsDetailsComponent } from './components/payments-details/payments-details.component';
import { PaymentsDialogComponent } from './components/payments-details/components/payments-dialog/payments-dialog.component';
import { InsertRepresentativeComponent } from './components/insert-representatives/insert-representative.component';
import { InsertApplicantComponent } from './components/insert-applicant/insert-applicant.component';
import { SearchSelectDialogComponent } from './components/dialogs/search-select-dialog/search-select-dialog.component';
import { InsertWaterRightTemplateComponent } from './components/templates/insert-water-right-template/insert-water-right-template.component';
import { ReportsButtonComponent } from './components/reports/reports-button/reports-button.component';
import { ReportsDialogComponent } from './components/reports/reports-dialog/reports-dialog.component';
import { MonthDayDateFieldComponent } from './components/month-day-date-field/month-day-date-field.component';
import { FormErrorsComponent } from './components/form-errors/form-errors.component';
import { FileFormFieldComponent } from './components/file-form-field/file-form-field.component';
import { SearchResultsDialogComponent } from './components/search-results-dialog/search-results-dialog.component';
import { ObjectionsObjectorsComponent } from './components/objections/components/objections-objectors/objections-objectors.component';
import { ObjectionsCategoriesComponent } from './components/objections/components/objections-categories/objections-categories.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TextFieldModule,
    // Material components
    MatAutocompleteModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatMomentDateModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatSelectModule,
    MatSnackBarModule,
    MatSortModule,
    MatStepperModule,
    MatTableModule,
    MatTabsModule,
    MatTooltipModule,
  ],
  declarations: [
    DeleteDialogComponent,
    LeavePageDialogComponent,
    InsertDialogComponent,
    SearchDialogComponent,
    UpdateDialogComponent,
    DataTableComponent,
    DataManagementFormFieldComponent,
    BaseCodeTableComponent,
    DataRowComponent,
    DataRowFieldComponent,
    DataFieldErrorsComponent,
    MoreInfoDialogComponent,
    ReadOnlyFieldComponent,
    SpinnerComponent,
    DateTimeFormFieldComponent,
    EditScreenComponent,
    FileLocationProcessorComponent,
    ResponsibleOfficeComponent,
    ProcessorComponent,
    StaffComponent,
    OfficeComponent,
    AffectedWaterRightsComponent,
    AddApplicationDialogComponent,
    AffectedChangeApplicationsComponent,
    InsertWaterRightComponent,
    SelectionCodeTableComponent,
    SelectionDataTableComponent,
    ConfirmationDialogComponent,
    ErrorDialogComponent,
    PaymentsDetailsComponent,
    PaymentsDialogComponent,
    InsertRepresentativeComponent,
    InsertApplicantComponent,
    SearchSelectDialogComponent,
    InsertWaterRightTemplateComponent,
    ReportsButtonComponent,
    ReportsDialogComponent,
    MonthDayDateFieldComponent,
    FormErrorsComponent,
    FileFormFieldComponent,
    SearchResultsDialogComponent,
    ObjectionsObjectorsComponent,
    ObjectionsCategoriesComponent,
  ],

  exports: [
    ReactiveFormsModule,
    TextFieldModule,
    // Material components
    MatAutocompleteModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatMomentDateModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatSelectModule,
    MatSnackBarModule,
    MatSortModule,
    MatStepperModule,
    MatTableModule,
    MatTabsModule,
    MatTooltipModule,
    // Shared Components
    AffectedWaterRightsComponent,
    AddApplicationDialogComponent,
    AffectedChangeApplicationsComponent,
    InsertWaterRightComponent,
    BaseCodeTableComponent,
    DataManagementFormFieldComponent,
    DataRowComponent,
    DataRowFieldComponent,
    DataTableComponent,
    DeleteDialogComponent,
    InsertDialogComponent,
    LeavePageDialogComponent,
    MoreInfoDialogComponent,
    OfficeComponent,
    ProcessorComponent,
    ResponsibleOfficeComponent,
    SearchDialogComponent,
    SpinnerComponent,
    StaffComponent,
    UpdateDialogComponent,
    SelectionCodeTableComponent,
    SelectionDataTableComponent,
    PaymentsDetailsComponent,
    InsertRepresentativeComponent,
    InsertApplicantComponent,
    ReportsButtonComponent,
    MonthDayDateFieldComponent,
    FormErrorsComponent,
    SearchResultsDialogComponent,
    ObjectionsObjectorsComponent,
    ObjectionsCategoriesComponent,
  ],
  providers: [
    DecimalPipe,
    { provide: MAT_TOOLTIP_DEFAULT_OPTIONS, useValue: customTooltipConfig },
    { provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: customDialogConfig },
    { provide: MAT_DATE_FORMATS, useValue: customDateFormats },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: customDateOptions },
  ],
})
export class SharedModule {}
