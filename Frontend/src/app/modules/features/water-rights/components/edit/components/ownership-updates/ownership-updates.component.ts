import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-ownership-updates',
  templateUrl: './ownership-updates.component.html',
  styleUrls: ['./ownership-updates.component.scss'],
})
export class OwnershipUpdatesComponent {
  public _idArray: string[];
  @Input() set idArray(value: string[]) {
    this._idArray = value;
  }

  constructor() {}

  public selectedOwnerUpdateId;

  public onOwnerUpdateClickHandler(ownerUpdateId: number): void {
    this.selectedOwnerUpdateId = ownerUpdateId;
  }
}
