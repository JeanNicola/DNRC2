import { MatDateFormats } from '@angular/material/core';
export const customDateFormats: MatDateFormats = {
  parse: {
    dateInput: ['M/D/YY', 'M/D/YYYY', 'M-D-YY', 'M-D-YYYY'],
  },
  display: {
    dateInput: 'MM/DD/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

export const customDateOptions = {
  // This was commented out since the default dates need to be
  // local dates, not UTC. UTC woudl advance the calendar to tomorrow
  // LONG before it was tomorrow in MT
  // useUtc: true,
  strict: true,
};
