import { Component, Input } from '@angular/core';
import { FileLocationProcessorComponent } from 'src/app/modules/shared/components/templates/file-location-processor/file-location-processor.component';
import { OfficeService } from 'src/app/modules/shared/components/templates/file-location-processor/services/office.service';
import { StaffService } from 'src/app/modules/shared/components/templates/file-location-processor/services/staff.service';

@Component({
  selector: 'app-file-location-office',
  templateUrl: './file-location-office.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/file-location-processor/file-location-processor.component.scss',
  ],
  providers: [OfficeService, StaffService],
})
export class FileLocationOfficeComponent extends FileLocationProcessorComponent {
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;

  constructor(
    public officeService: OfficeService,
    public staffService: StaffService
  ) {
    super(officeService, staffService);
  }
}
