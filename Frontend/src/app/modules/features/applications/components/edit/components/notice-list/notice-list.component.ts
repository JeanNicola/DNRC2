import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-notice-list',
  templateUrl: './notice-list.component.html',
  styleUrls: ['./notice-list.component.scss'],
})
export class NoticeListComponent {
  @Input() mailingJobSelectedId;

  constructor() {}

  setMailingJobSelectedId(mailingJobId: string) {
    if (mailingJobId) {
      this.mailingJobSelectedId = mailingJobId;
    }
  }
}
