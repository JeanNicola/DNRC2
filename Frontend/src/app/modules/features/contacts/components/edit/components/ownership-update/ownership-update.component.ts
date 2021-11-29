import { Component } from '@angular/core';

@Component({
  selector: 'app-ownership-update',
  templateUrl: './ownership-update.component.html',
  styleUrls: ['./ownership-update.component.scss'],
})
export class OwnershipUpdateComponent {
  constructor() {}

  public selectedOwnerUpdateId;

  public onOwnerUpdateClickHandler(ownerUpdateId: number): void {
    this.selectedOwnerUpdateId = ownerUpdateId;
  }
}
