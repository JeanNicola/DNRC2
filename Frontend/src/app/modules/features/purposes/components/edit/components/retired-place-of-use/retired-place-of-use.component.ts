import { Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { CopyPodsService } from '../period-and-place-of-use/services/copy-pods.service';
import { SubdivisionInformationService } from '../period-and-place-of-use/services/subdivision-information.service';
import { RetiredPouSubdivisionsService } from './services/retired-pou-subdivisions.service';

@Component({
  selector: 'app-retired-place-of-use',
  templateUrl: './retired-place-of-use.component.html',
  styleUrls: ['./retired-place-of-use.component.scss'],
  providers: [
    RetiredPouSubdivisionsService,
    CopyPodsService,
    SubdivisionInformationService,
  ],
})
export class RetiredPlaceOfUseComponent {
  @Input() waterRightTypeCode = null;
  @Input() waterRightStatusCode = null;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = null;

  constructor(
    private route: ActivatedRoute,
    public retiredPouSubdivisionsService: RetiredPouSubdivisionsService
  ) {}

  public purposeId = this.route.snapshot.params.purposeId;
  public placeId;
  public selectedPlaceOfUse;
  public reloadRetiredPlacesOfUse = new Subject();

  public onPlaceOfUseSelect(data: any): void {
    this.selectedPlaceOfUse = data;
    this.placeId = data ? data.placeId : null;
  }
}
