import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { Purpose } from '../../../../interfaces/purpose.interface';
import { PurposeDropdownsService } from '../edit-header/services/purpose-dropdowns.service';

@Component({
  selector: 'app-period-and-place-of-use',
  templateUrl: './period-and-place-of-use.component.html',
  styleUrls: ['./period-and-place-of-use.component.scss'],
})
export class PeriodAndPlaceOfUseComponent {
  constructor(
    private route: ActivatedRoute,
    public dropdownService: PurposeDropdownsService
  ) {}

  public reloadPlacesOfUse = new Subject();

  @Output() dataChanged: EventEmitter<void> = new EventEmitter<void>();

  private _headerData: Purpose;
  @Input() set headerData(d: Purpose) {
    this.waterRightTypeCode = d.waterRightTypeCode;
    this.waterRightStatusCode = d.waterRightStatusCode;
    this.versionNumber = d.versionNumber;
    this.purposeId = d.purposeId;
    this.has645Application = null;
    this.has650Application = null;
    this.has645Application = d.applicationTypeCodes?.includes('645');
    this.has650Application = d.applicationTypeCodes?.includes('650');
    this.isDecreed = d.isDecreed;
    this.isEditableIfDecreed = d.isEditableIfDecreed;
    this.canEdit = d.canEdit;
    this._headerData = d;
  }
  get headerData(): Purpose {
    return this._headerData;
  }

  public waterRightTypeCode: string;
  public waterRightStatusCode: string;
  public versionNumber: number;
  public has645Application: boolean;
  public has650Application: boolean;
  public isDecreed: boolean;
  public isEditableIfDecreed: boolean;
  public canEdit: boolean;
  public purposeId: number;
  public placeId;
  public selectedPlaceOfUse;

  public onPlaceOfUseSelect(data: any): void {
    this.selectedPlaceOfUse = data;
    this.placeId = data ? data.placeId : null;
  }
}
