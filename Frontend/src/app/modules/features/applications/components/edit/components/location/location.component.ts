import { Component, Input } from '@angular/core';
// eslint-disable-next-line max-len
import { FileLocationProcessorComponent } from 'src/app/modules/shared/components/templates/file-location-processor/file-location-processor.component';
import { OfficeService } from 'src/app/modules/shared/components/templates/file-location-processor/services/office.service';
import { StaffService } from 'src/app/modules/shared/components/templates/file-location-processor/services/staff.service';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/file-location-processor/file-location-processor.component.scss',
  ],
  providers: [OfficeService, StaffService],
})
export class LocationComponent extends FileLocationProcessorComponent {
  @Input() idArray: string[] = [''];
  constructor(
    public officeService: OfficeService,
    public staffService: StaffService
  ) {
    super(officeService, staffService);
  }
}
