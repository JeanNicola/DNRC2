import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-applicant-table',
  templateUrl: './applicant-table.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/data-table/data-table.scss',
    './applicant-table.component.scss',
  ],
  providers: [DatePipe, CurrencyPipe],
})
export class ApplicantTableComponent
  extends DataTableComponent
  implements OnInit
{
  @Output() representativesEvent: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    datePipe: DatePipe,
    currencyPipe: CurrencyPipe,
    private router: Router
  ) {
    super(datePipe, currencyPipe);
  }

  ngOnInit(): void {
    super.ngOnInit();

    // Puts representatives column between data and actions
    this.displayedColumns.pop();
    this.displayedColumns.push('representatives');
    this.displayedColumns.push('actions');
  }

  openRepresentatives($event: Event, row: number): void {
    $event.stopPropagation();
    this.representativesEvent.emit(row);
  }

  dblclickRow(event: Event, i: number): void {
    void this.router.navigate(['wris', 'contacts', this.data[i].contactId]);
  }
}
