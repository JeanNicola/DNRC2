import { Component, EventEmitter, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-remarks',
  templateUrl: './remarks.component.html',
  styleUrls: ['./remarks.component.scss'],
})
export class RemarksComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;

  public variableUpdate: EventEmitter<void> = new EventEmitter<void>();

  public selectedRemark;
  public waterRightId;
  public versionId;

  ngOnInit(): void {
    this.waterRightId = this.route.snapshot.params.waterRightId;
    this.versionId = this.route.snapshot.params.versionId;
  }

  public onRemarkSelect(remarkId: any): void {
    this.selectedRemark = remarkId;
  }

  public onVariableUpdate(): void {
    this.variableUpdate.emit();
  }
}
