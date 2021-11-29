import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-case-water-rights',
  templateUrl: './case-water-rights.component.html',
  styleUrls: ['./case-water-rights.component.scss'],
})
export class CaseWaterRightsComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  @Input() decreeId = null;
  @Input() decreeBasin = null;
  @Input() hasCaseAdminRole: boolean = false;

  public currentWaterRight = null;
  public currentCaseNumber = null;

  public ngOnInit() {
    this.currentCaseNumber = this.route.snapshot.params.caseId;
  }
}
