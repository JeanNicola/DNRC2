import {
  AfterViewInit,
  Component,
  Inject,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { merge, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { SearchDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/search-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

@Component({
  selector: 'app-application-search-dialog',
  templateUrl: 'application-search-dialog.component.html',
  styleUrls: [
    '../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
    'application-search-dialog.component.scss',
  ],
})
export class ApplicationSearchDialogComponent
  extends SearchDialogComponent
  implements AfterViewInit, OnDestroy
{
  private unsubscribe = new Subject();

  constructor(
    public dialogRef: MatDialogRef<ApplicationSearchDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {
    super(dialogRef, data);
  }

  @ViewChild('tabs') tabs: MatTabGroup;
  public mainColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationTypeCode',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
  ];
  public ownerColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'ownerContactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'ownerLastName',
      title: 'Last Name',
      type: FormFieldTypeEnum.Input,
      searchValidators: [
        WRISValidators.requireOtherFieldIfNonNull('ownerFirstName'),
      ],
    },
    {
      columnId: 'ownerFirstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      searchValidators: [
        WRISValidators.updateValidityOfOtherField('ownerLastName'),
      ],
    },
  ];
  public repColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'repContactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'repLastName',
      title: 'Last Name',
      type: FormFieldTypeEnum.Input,
      searchValidators: [
        WRISValidators.requireOtherFieldIfNonNull('repFirstName'),
      ],
    },
    {
      columnId: 'repFirstName',
      title: 'First Name',
      type: FormFieldTypeEnum.Input,
      searchValidators: [
        WRISValidators.updateValidityOfOtherField('repLastName'),
      ],
    },
  ];

  public ngAfterViewInit(): void {
    this.tabs.selectedTabChange
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        if (event.index === 1) {
          this.formGroup.get('ownerContactId').patchValue(null);
          this.formGroup.get('ownerLastName').patchValue(null);
          this.formGroup.get('ownerFirstName').patchValue(null);
        } else {
          this.formGroup.get('repContactId').patchValue(null);
          this.formGroup.get('repLastName').patchValue(null);
          this.formGroup.get('repFirstName').patchValue(null);
        }
      });
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
