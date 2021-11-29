import { Address } from '../interfaces/contact-interface';
import { formatAddressForDisplay } from './format-address';
import { sortAddresses } from './sort-address';

export function updateAddressesArr(
  index: number,
  address: Address,
  currentAddresses: Address[]
) {
  if (address.isPrimMail) {
    currentAddresses = currentAddresses.map((address) => {
      return { ...address, isPrimMail: false, primaryMail: 'N' };
    });
  }

  if (index === -1) {
    currentAddresses?.unshift(address);
  } else {
    currentAddresses[index] = { ...address };
  }

  return currentAddresses.sort(sortAddresses).map(formatAddressForDisplay);
}
