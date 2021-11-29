import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-case-district-court',
  templateUrl: './case-district-court.component.html',
  styleUrls: ['./case-district-court.component.scss'],
})
export class CaseDistrictCourtComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  public currentDistrictId = null;
  public currentCaseNumber = null;

  public ngOnInit() {
    this.currentCaseNumber = this.route.snapshot.params.caseId;
  }
}
