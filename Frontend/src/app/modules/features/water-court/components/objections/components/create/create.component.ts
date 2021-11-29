import { CdkStepper } from '@angular/cdk/stepper';
import { Component, Inject, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ObjectionsSearchService } from '../search/services/objections-search.service';

enum DynamicSteps {
  DECREE,
  WATER_RIGHT,
  OBJECTOR,
  APPLICATION,
}

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss'],
  providers: [ObjectionsSearchService, CdkStepper],
})
export class CreateComponent extends InsertDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<CreateComponent>,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public service: ObjectionsSearchService
  ) {
    super(dialogRef, data);
  }

  public title = this.data.title;
  public tooltip = 'Create';

  public dynamicStepsEnum = DynamicSteps;
  public currentTabsAvailable: DynamicSteps[] = null;
  public tabsAvailableBasedOnObjectionType = {
    COB: [DynamicSteps.OBJECTOR, DynamicSteps.DECREE, DynamicSteps.WATER_RIGHT],
    ITA: [DynamicSteps.OBJECTOR, DynamicSteps.DECREE, DynamicSteps.WATER_RIGHT],
    OTW: [DynamicSteps.OBJECTOR, DynamicSteps.DECREE, DynamicSteps.WATER_RIGHT],
    OMO: [DynamicSteps.DECREE, DynamicSteps.WATER_RIGHT],
    OTD: [DynamicSteps.OBJECTOR, DynamicSteps.DECREE],
    OTA: [DynamicSteps.OBJECTOR, DynamicSteps.APPLICATION],
  };

  @ViewChild('tabs') tabs: MatTabGroup;

  // Decree Basin properties
  public showDecreeResults = false;
  public decreeBasinRow;
  public selectedDecreeRowIdx = null;
  public decreeBasinSearchForm = new FormGroup({});
  public decreeBasinSearchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Decree Basin',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
  ];
  // Water Right properties
  public showWaterRightResults = false;
  public waterRightRow;
  public selectedWaterRightRowIdx = null;
  public waterRightSearchForm = new FormGroup({});
  public waterRightSearchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterNumber',
      title: 'Water Right Number',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
  ];

  // Objector properties
  public showObjectorResults = false;
  public objectorRow;
  public selectedObjectorRowIdx = null;
  public objectorsSearchForm = new FormGroup({});
  public objectorSearchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'lastName',
      title: 'Last Name/Corporation',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
  ];

  // Application properties
  public showAppResults = false;
  public appRow;
  public selectedAppRowIdx = null;
  public appSearchForm = new FormGroup({});
  public appSearchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
  ];

  protected initFunction() {
    // Set default type equal to 'Objection to Water Right'
    this.data.values.objectionType = 'OTW';
    this._onChange({ value: 'OTW', fieldName: 'objectionType' });
  }

  public onDecree($event) {
    this.decreeBasinRow = $event.value;
    this.selectedDecreeRowIdx = $event.idx;
  }

  public onDecreeSearch() {
    this.decreeBasinRow = null;
    this.selectedDecreeRowIdx = null;
    this.showDecreeResults = false;
    setTimeout(() => {
      this.showDecreeResults = true;
    });
  }

  public onWaterRight($event) {
    this.waterRightRow = $event.value;
    this.selectedWaterRightRowIdx = $event.idx;
  }

  public onWaterRightSearch() {
    this.waterRightRow = null;
    this.selectedWaterRightRowIdx = null;
    this.showWaterRightResults = false;
    setTimeout(() => {
      this.showWaterRightResults = true;
    });
  }

  public onObjector($event) {
    this.objectorRow = $event.value;
    this.selectedObjectorRowIdx = $event.idx;
  }

  public onObjectorSearch() {
    this.objectorRow = null;
    this.selectedObjectorRowIdx = null;
    this.showObjectorResults = false;
    setTimeout(() => {
      this.showObjectorResults = true;
    });
  }

  public onApplication($event) {
    this.appRow = $event.value;
    this.selectedAppRowIdx = $event.idx;
  }

  public onApplicationSearch() {
    this.appRow = null;
    this.selectedAppRowIdx = null;
    this.showAppResults = false;
    setTimeout(() => {
      this.showAppResults = true;
    });
  }

  private clearSelectedValues() {
    this.objectorRow = null;
    this.selectedObjectorRowIdx = null;
    this.decreeBasinRow = null;
    this.selectedDecreeRowIdx = null;
    this.waterRightRow = null;
    this.selectedWaterRightRowIdx = null;
    this.appRow = null;
    this.selectedAppRowIdx = null;
    this.showObjectorResults = false;
    this.showDecreeResults = false;
    this.showWaterRightResults = false;
    this.showAppResults = false;
  }

  public _onChange($event) {
    if ($event.fieldName === 'objectionType') {
      this.clearSelectedValues();
      if (this.tabs) this.tabs.selectedIndex = 0;

      this.currentTabsAvailable =
        this.tabsAvailableBasedOnObjectionType[$event.value];
    }
  }

  public save() {
    if (
      this.currentTabsAvailable.indexOf(DynamicSteps.APPLICATION) !== -1 &&
      !this.appRow
    ) {
      this.snackBar.open('Application is required to save.');
      return;
    }
    console.log(this.decreeBasinRow, this.waterRightRow, this.objectorRow);
  }
}
