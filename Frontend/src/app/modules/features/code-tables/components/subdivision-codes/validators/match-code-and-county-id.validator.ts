import { FormGroup, ValidationErrors } from '@angular/forms';
import { CountiesRowInterface } from 'src/app/modules/shared/interfaces/counties-row.interface';
import { stateCountyNumberToId } from 'src/app/modules/shared/utilities/state-county-number-to-id';

export const matchCodeAndCountyIdValidator = (
  countyArr: CountiesRowInterface[]
) => (group: FormGroup): ValidationErrors | null => {
  if (
    group.controls.code.value != null &&
    group.controls.countyId.value != null
  ) {
    const idFromCode = stateCountyNumberToId(
      (group.controls.code.value as string).substring(0, 2),
      countyArr
    );
    const idFromCountyId = group.controls.countyId.value as number;
    if (idFromCode !== idFromCountyId) {
      return { matchCodeAndCountyIdValidator: false };
    } else {
      return null;
    }
  } else {
    return { matchCodeAndCountyIdValidator: false };
  }
};
