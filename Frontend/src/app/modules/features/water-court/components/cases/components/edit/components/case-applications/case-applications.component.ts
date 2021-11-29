import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-case-applications',
  templateUrl: './case-applications.component.html',
  styleUrls: ['./case-applications.component.scss'],
})
export class CaseApplicationsComponent {
  @Input() applicationId = null;

  public selectedObjectionId = null;
}
