import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-shared-and-related-elements',
  templateUrl: './shared-and-related-elements.component.html',
  styleUrls: ['./shared-and-related-elements.component.scss'],
})
export class SharedAndRelatedElementsComponent implements OnInit {
  @Output() relatedElementsChanged: EventEmitter<void> =
    new EventEmitter<void>();
  @Input() reloadRelatedElements: Observable<any> = null;
  public relatedRightId;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.relatedRightId = this.route.snapshot.params.id;
  }
}
