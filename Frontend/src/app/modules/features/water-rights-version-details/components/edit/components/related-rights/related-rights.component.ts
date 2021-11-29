import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-related-rights',
  templateUrl: './related-rights.component.html',
  styleUrls: ['./related-rights.component.scss'],
})
export class RelatedRightsComponent implements OnInit {
  public selectedRelatedRight: number;

  public onSelection(relatedRightId: any): void {
    this.selectedRelatedRight = relatedRightId;
  }

  constructor() {}

  ngOnInit(): void {}
}
