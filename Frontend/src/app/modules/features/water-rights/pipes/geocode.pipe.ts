import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'geocode',
})
export class GeocodePipe implements PipeTransform {
  transform(value: string): string {
    if (value) {
      value = value.replace(/\-/g, '').toUpperCase();
      const geocodeParts = [
        value.substr(0, 2),
        value.substr(2, 4),
        value.substr(6, 2),
        value.substr(8, 1),
        value.substr(9, 2),
        value.substr(11, 2),
        value.substr(13, 4),
      ];
      return geocodeParts.join('-') + value.substr(17);
    } else {
      return value;
    }
  }
}
