import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-objections-for-versions',
  templateUrl: './objections-for-versions.component.html',
  styleUrls: ['./objections-for-versions.component.scss'],
})
export class ObjectionsForVersionsComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  public waterRightId;
  public versionId;
  public selectedObjection;

  ngOnInit(): void {
    this.waterRightId = this.route.snapshot.params.waterRightId;
    this.versionId = this.route.snapshot.params.versionId;
  }

  public onObjectionSelect(id: any): void {
    this.selectedObjection = id;
  }
}
