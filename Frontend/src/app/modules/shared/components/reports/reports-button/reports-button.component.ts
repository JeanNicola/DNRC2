import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ReportDefinition } from '../../../interfaces/report-definition.interface';
import { ReportsDialogComponent } from '../reports-dialog/reports-dialog.component';

@Component({
  selector: 'app-reports-button',
  templateUrl: './reports-button.component.html',
  styleUrls: ['./reports-button.component.scss'],
})
export class ReportsButtonComponent implements OnChanges {
  @Input() data: any;
  @Input() reportConfig: ReportDefinition[];
  @Input() title = 'Reports';
  constructor(public dialog: MatDialog) {}

  public availableReports: ReportDefinition[];

  protected dialogWidth = '600px';

  public ngOnChanges(changes: SimpleChanges) {
    if (changes.data?.currentValue) {
      this.reportConfig.forEach((a) => {
        if (!a.params) {
          a.params = {};
        }
        a.setParams(a, this.data);
      });
      this.availableReports = this.reportConfig
        .filter(
          (report: ReportDefinition) =>
            !report.isAvailable || report.isAvailable(this.data)
        )
        .sort((a, b) => (a.title > b.title ? 1 : -1));
    }
  }

  public openReports(): void {
    this.dialog.open(ReportsDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: this.title,
        reports: this.availableReports,
      },
    });
  }
}
