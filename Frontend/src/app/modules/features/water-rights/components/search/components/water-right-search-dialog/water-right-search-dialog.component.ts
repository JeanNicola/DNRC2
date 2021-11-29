import {
  AfterViewInit,
  Component,
  Inject,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { SearchDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/search-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { GeocodePipe } from '../../../../pipes/geocode.pipe';

@Component({
  selector: 'app-water-right-search-dialog',
  templateUrl: './water-right-search-dialog.component.html',
  providers: [GeocodePipe],
})
export class WaterRightSearchDialogComponent
  extends SearchDialogComponent
  implements AfterViewInit, OnDestroy
{
  private unsubscribe = new Subject();

  constructor(
    public dialogRef: MatDialogRef<WaterRightSearchDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private geocodePipe: GeocodePipe
  ) {
    super(dialogRef, data);
  }

  @ViewChild('tabs') tabs: MatTabGroup;

  public topColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'waterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public waterRightColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'typeCode',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'subBasin',
      title: 'Sub Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterReservationId',
      title: 'Water Reservation #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'conservationDistrictNumber',
      title: 'Conservation District #',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public geocodeColumn: ColumnDefinitionInterface[] = [
    {
      columnId: 'geocodeId',
      title: 'Geocode',
      type: FormFieldTypeEnum.Input,
      searchValidators: [WRISValidators.isGeocode],
      placeholder: '00-0000-00-0-00-00-0000',
    },
  ];

  public versionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'version',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'versionType',
      title: 'Version Type',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public initFunction(): void {
    this.waterRightColumns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === 'typeCode'
    )[0].selectArr = this.data.typeCodes;
  }

  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public ngAfterViewInit(): void {
    this.tabs.selectedIndexChange
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((value: number) => {
        if (value === 2) {
          this.formGroup.controls.waterRightNumber.disable();
          this.formGroup.controls.waterRightNumber.setValue(null);
        } else {
          this.formGroup.controls.waterRightNumber.enable();
        }

        if (value !== 0) {
          this.formGroup.controls.geocodeId.setValue(null);
          this.formGroup.controls.basin.setValue(null);
          this.formGroup.controls.typeCode.setValue(null);
          this.formGroup.controls.subBasin.setValue(null);
          this.formGroup.controls.waterReservationId.setValue(null);
          this.formGroup.controls.conservationDistrictNumber.setValue(null);
        }
        if (value !== 1) {
          this.formGroup.controls.version.setValue(null);
          this.formGroup.controls.versionType.setValue(null);
        }
        if (value !== 2) {
          this.formGroup.controls.geocodeId.setValue(null);
        }
      });
  }

  _onBlur($event: any): void {
    const geocodeControl = this.formGroup.get('geocodeId');
    if ($event.fieldName === 'geocodeId' && geocodeControl.valid) {
      const formattedGeocode = this.geocodePipe.transform(geocodeControl.value);
      this.formGroup.get('geocodeId').setValue(formattedGeocode);
    }
  }
}
