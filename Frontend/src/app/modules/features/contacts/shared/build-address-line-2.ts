import { Address } from '../interfaces/contact-interface';

export function buildAddressLine2(address: Address): string {
  let addressLine2 = '';

  if (address.cityName) addressLine2 += address.cityName;
  if (address.cityName && address.stateName) addressLine2 += ' ';
  if (address.stateName) addressLine2 += address.stateName;
  if (address.stateName && address.zipCode) addressLine2 += ' ';
  if (address.zipCode) addressLine2 += address.zipCode;

  return addressLine2;
}
