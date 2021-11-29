/* eslint-disable prefer-arrow/prefer-arrow-functions */
import { Address } from '../interfaces/contact-interface';

export function formatAddressForDisplay(address: Address): any {
  // If the passed in address structure is null, return null
  if (!address) {
    return null;
  }

  // Build the City/State/Zip
  const cityStateAndZip = [
    address.cityName?.trim(),
    address.stateCode?.trim(),
    getZipAndPl4(address),
  ];

  // If the address is local build the complete address using Addr1/Addr2/Addr3/City/State/Zip
  // Else for foreign, use Addr1/Addr2/foreignPostal
  let completeAddress: string[];

  if (address.foreignAddress === 'Y') {
    completeAddress = [
      address.addressLine1?.trim(),
      address.addressLine2?.trim(),
      address.addressLine3?.trim(),
      address.foreignPostal?.trim(),
    ];
  } else {
    completeAddress = [
      address.addressLine1?.trim(),
      address.addressLine2?.trim(),
      address.addressLine3?.trim(),
      cityStateAndZip.filter((item) => !!item).join(' '),
    ];
  }

  // Convert the addary of values to newline seperated strings
  const completeAddressString = completeAddress
    .filter((addressFragment) => !!addressFragment)
    .join('\n');

  return {
    ...address,
    completeAddress: completeAddressString,
    isPrimMail: address.primaryMail === 'Y',
    isForeign: address.foreignAddress === 'Y',
    rtnMail: address.unresolvedFlag === 'Y',
  };
}

function getZipAndPl4(address: Address) {
  let zipCodeAndPl4 = address?.zipCode || '';
  if (address.zipCode && address.pl4) {
    zipCodeAndPl4 += '-';
  }
  if (address.pl4) {
    zipCodeAndPl4 += address.pl4;
  }
  return zipCodeAndPl4;
}
