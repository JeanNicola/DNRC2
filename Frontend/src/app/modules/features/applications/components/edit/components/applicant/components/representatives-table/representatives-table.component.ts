import { DatePipe, CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataTableComponent } from 'src/app/modules/shared/components/data-table/data-table';

@Component({
  selector: 'app-representatives-table',
  templateUrl:
    '../../../../../../../../shared/components/data-table/data-table.html',
  styleUrls: [
    '../../../../../../../../shared/components/data-table/data-table.scss',
  ],
  providers: [DatePipe, CurrencyPipe],
})
export class RepresentativesTableComponent
  extends DataTableComponent
  implements OnInit
{
  isInMain = false;

  constructor(
    datePipe: DatePipe,
    currencyPipe: CurrencyPipe,
    private router: Router
  ) {
    super(datePipe, currencyPipe);
  }

  public dblclickRow(event: Event, i: number): void {
    void this.router.navigate(['wris', 'contacts', this.data[i].contactId]);
  }
}
