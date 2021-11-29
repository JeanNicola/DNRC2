import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FileLocationProcessorComponent } from 'src/app/modules/shared/components/templates/file-location-processor/file-location-processor.component';
import { OfficeService } from 'src/app/modules/shared/components/templates/file-location-processor/services/office.service';
import { StaffService } from 'src/app/modules/shared/components/templates/file-location-processor/services/staff.service';

@Component({
  selector: 'app-file-location-and-location',
  templateUrl: './file-location-and-processor.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/file-location-processor/file-location-processor.component.scss',
  ],
  providers: [OfficeService, StaffService],
})
export class FileLocationAndProcessorComponent extends FileLocationProcessorComponent {
  idArray: string[];
  constructor(
    public officeService: OfficeService,
    public staffService: StaffService,
    private route: ActivatedRoute
  ) {
    super(officeService, staffService);
  }

  protected initFunction(): void {
    this.idArray = [this.route.snapshot.params.id];
  }
}
