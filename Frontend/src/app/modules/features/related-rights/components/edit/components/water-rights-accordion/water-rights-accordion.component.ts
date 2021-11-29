import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-water-rights-accordion',
  templateUrl: './water-rights-accordion.component.html',
  styleUrls: ['./water-rights-accordion.component.scss'],
})
export class WaterRightsAccordionComponent implements OnInit {
  relatedRightId;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.relatedRightId = this.route.snapshot.params.id;
  }
}
