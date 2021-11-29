import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-measurement-reports',
  templateUrl: './measurement-reports.component.html',
  styleUrls: ['./measurement-reports.component.scss'],
})
export class MeasurementReportsComponent {
  constructor() {}

  private _idArray: string[];
  @Input() set idArray(value: string[]) {
    this._idArray = value;
  }
  get idArray(): string[] {
    return this._idArray;
  }
  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() operatingAuthorityDate = '1970-01-01';
  public selectedReport: string;

  public onReportSelect(remarkId: any): void {
    this.selectedReport = remarkId;
  }
}
