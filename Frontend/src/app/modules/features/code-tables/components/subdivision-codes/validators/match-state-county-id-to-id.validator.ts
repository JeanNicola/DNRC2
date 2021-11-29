import { AbstractControl } from '@angular/forms';
import { ValidationErrors } from '@angular/forms';
import { CountiesRowInterface } from 'src/app/modules/shared/interfaces/counties-row.interface';

export const matchStateCountyIdToIdValidator = (
  countiesArr: CountiesRowInterface[]
) => (control: AbstractControl): ValidationErrors | null => {
  if (
    control.value !== null &&
    (control.value as string).length > 1 &&
    !countiesArr.filter(
      (x) => x.stateCountyNumber === (control.value as string).substring(0, 2)
    ).length
  ) {
    return { matchStateCountyIdToIdValidator: true };
  } else {
    return null;
  }
};
