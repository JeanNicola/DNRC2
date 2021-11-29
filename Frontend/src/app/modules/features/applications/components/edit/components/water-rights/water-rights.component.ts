import { Component, Input } from '@angular/core';
import { Subject } from 'rxjs';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';

@Component({
  selector: 'app-water-rights',
  templateUrl: './water-rights.component.html',
  styleUrls: ['./water-rights.component.scss'],
})
export class WaterRightsComponent {
  @Input() idArray: string[] = null;
  @Input() appTypeCode = '';

  public waterRightChanged = new Subject<DataQueryParametersInterface>();

  public onWaterRightsChanged(
    queryParameters: DataQueryParametersInterface
  ): void {
    this.waterRightChanged.next(queryParameters);
  }
}
