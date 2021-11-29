import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnInit, SecurityContext } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import {
  ReportDefinition,
  ReportTypes,
} from '../../../interfaces/report-definition.interface';
import { ReportDialogInterface } from '../../../interfaces/report-dialog.interface';
import { ReportUrlService } from './services/report-url.service';

@Component({
  selector: 'app-reports-dialog',
  templateUrl: './reports-dialog.component.html',
  styleUrls: ['./reports-dialog.component.scss'],
  providers: [ReportUrlService],
})
export class ReportsDialogComponent implements OnInit {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: ReportDialogInterface,
    public dialogRef: MatDialogRef<ReportsDialogComponent>,
    private reportUrlService: ReportUrlService,
    private sessionStorage: SessionStorageService,
    private sanitizer: DomSanitizer
  ) {}

  public regularReportURL: string = null;
  public scannedReportURL: string = null;

  public ngOnInit() {
    this.getUrl().subscribe((response) => {
      response.results.map((ref) => {
        if (ref.value.includes('SSRS_REPORT_PREFIX')) {
          this.regularReportURL = ref.description;
        }
        if (ref.value.includes('FILENET_PREFIX')) {
          this.scannedReportURL = ref.description;
        }
      });
    });
  }

  private getUrl(): Observable<any> {
    const queryParameters: any = {
      filters: {
        env: this.sessionStorage.dbEnvironment,
      },
    };
    return this.reportUrlService.get(queryParameters);
  }

  public buildUrl(report: ReportDefinition): string {
    // This is here for timing - if the database call to get the base URL has not completed
    // yet, then no valid URL can be created
    if (this.regularReportURL === null && this.scannedReportURL === null) {
      return '';
    }

    let params: HttpParams = new HttpParams();
    Object.entries(report.params).forEach((k) => {
      params = params.set(k[0], k[1]);
    });
    if (!report.type || report.type === ReportTypes.REPORT) {
      params = params.set('rs:Command', 'Render');
      params = params.set('rs:Format', 'PDF');
      return this.sanitizer.sanitize(
        SecurityContext.URL,
        `${this.regularReportURL}${report.reportId}&${params.toString()}`
      );
    }
    return this.sanitizer.sanitize(
      SecurityContext.URL,
      `${this.scannedReportURL}${params.toString()}`
    );
  }
}
