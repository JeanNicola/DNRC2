import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-objections',
  templateUrl: './objections.component.html',
  styleUrls: ['./objections.component.scss'],
})
export class ObjectionsComponent {
  @Input() appId: string = null;
  objectionId: string = null;
  summaryData: any;

  setObjection(objectionId: string): void {
    this.objectionId = objectionId;
  }

  setSummary(summaryData: any): void {
    this.summaryData = summaryData;
  }
}
