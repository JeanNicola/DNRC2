import { CountiesRowInterface } from '../interfaces/counties-row.interface';

// stateCountyNumber always comes back from Counties endpoint as a two character string - Ex. "03"
export const stateCountyNumberToId = (
  stateCountyNumber: string,
  countiesArr: CountiesRowInterface[]
): number => {
  const arr = countiesArr.filter(
    (x) => stateCountyNumber === x.stateCountyNumber
  );
  if (arr.length === 1) {
    const id = arr[0].id;
    if (id != null) {
      return id;
    } else {
      console.error(
        'Counties Error: stateCountyNumberToId function is returning null'
      );
    }
  } else if (arr.length > 1) {
    console.error(
      'Counties Error: stateCountyNumberToId array picking up multiple matches.'
    );
  }
};
