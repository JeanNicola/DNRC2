import { CountiesRowInterface } from '../interfaces/counties-row.interface';

export const idToStateCountyNumber = (
  id: number,
  countiesArr: CountiesRowInterface[]
): string => {
  const arr = countiesArr.filter((x) => id === x.id);
  if (arr.length === 1) {
    const stateCountyNumber = arr[0].stateCountyNumber;
    if (stateCountyNumber.length === 2) {
      return stateCountyNumber;
    } else {
      console.error(
        'Counties Error: idToStateCountyNumber return value length incorrect.'
      );
    }
  } else if (arr.length > 1) {
    console.error(
      'Counties Error: idToStateCountyNumber array picking up multiple matches.'
    );
  }
};
