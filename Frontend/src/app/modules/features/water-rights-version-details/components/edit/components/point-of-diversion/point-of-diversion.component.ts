import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { PodService } from './services/pod.service';

@Component({
  selector: 'app-point-of-diversion',
  templateUrl: './point-of-diversion.component.html',
  styleUrls: ['./point-of-diversion.component.scss'],
  providers: [PodService],
})
export class PointOfDiversionComponent {
  constructor(public service: PodService, public snackBar: SnackBarService) {}

  private _headerData: any;
  @Input() set headerData(d: any) {
    this.waterRightId = d?.waterRightId;
    this.version = d?.version;
    this.waterRightTypeCode = d?.waterRightTypeCode;
    this.canEdit = d?.canEdit;
    this._headerData = d;
  }
  get headerData(): any {
    return this._headerData;
  }

  @Output() podsUpdated = new EventEmitter<void>();

  public data: any;
  public canEdit = false;
  public waterRightId: string = null;
  public version: string = null;
  public waterRightTypeCode: string = null;
  public podId: string;
  public headerReloadEvent = new EventEmitter<void>();
  public periodReloadEvent = new EventEmitter<void>();

  public onPodSelect(podId): void {
    this.podId = podId;
    if (podId) {
      this._get(podId);
    } else {
      this.data = null;
    }
  }

  public reloadDetails(): void {
    this._get(this.podId);
    this.headerReloadEvent.next();
  }

  public reloadPeriods(): void {
    this.periodReloadEvent.next();
  }

  protected _get(podId: string): void {
    this.service.get({}, this.waterRightId, this.version, podId).subscribe(
      (data) => {
        this.data = { ...data.results };
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        const message = errorBody.userMessage || ErrorMessageEnum.GET;
        this.snackBar.open(message);
      }
    );
  }

  public onPODUpdate(): void {
    this.podsUpdated.emit();
  }
}
