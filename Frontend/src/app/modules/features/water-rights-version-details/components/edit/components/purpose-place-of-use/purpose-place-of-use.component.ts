import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { WaterRightVersionPurposesService } from 'src/app/modules/features/purposes/components/edit/components/edit-header/services/water-right-version-purposes.service';
import { PurposesService } from 'src/app/modules/features/purposes/components/search/services/purposes.service';

@Component({
  selector: 'app-purpose-place-of-use',
  templateUrl: './purpose-place-of-use.component.html',
  styleUrls: ['./purpose-place-of-use.component.scss'],
  providers: [PurposesService, WaterRightVersionPurposesService],
})
export class PurposePlaceOfUseComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  private _headerData: any;
  @Input() set headerData(d: any) {
    this.canEdit = d?.canEdit;
    this.waterRightTypeCode = d?.waterRightTypeCode;
    this.waterRightStatusCode = d?.waterRightStatusCode;
    this.versionNumber = d?.version;
    this.waterRightNumber = d?.waterRightNumber;
    this.waterRightId = d?.waterRightId;
    this.basin = d?.basin;
    this.ext = d?.ext;
    this.has645Application = d?.applicationTypeCodes?.includes('645');
    this._headerData = d;
  }
  get headerData(): any {
    return this._headerData;
  }

  public canEdit = true;
  public waterRightTypeCode = null;
  public waterRightStatusCode = null;
  public has645Application = false;
  public versionNumber = null;
  public waterRightNumber = null;
  public waterRightId = null;
  public basin = null;
  public ext = null;
  public applicationTypeCodes = null;
  public idArray = [];
  public reloadVolumeAndAcreage: Subject<any> = new Subject();

  public ngOnInit(): void {
    this.idArray = [
      this.route.snapshot.params.waterRightId,
      this.route.snapshot.params.versionId,
    ];
  }
}
