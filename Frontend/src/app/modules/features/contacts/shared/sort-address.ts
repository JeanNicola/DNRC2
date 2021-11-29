import { Address } from '../interfaces/contact-interface';

export function sortAddresses(addressA: Address, addressB: Address) {
  if (addressA.isPrimMail && !addressB.isPrimMail) return -1;
  if (!addressA.isPrimMail && addressB.isPrimMail) return 1;
  if (addressA.addressLine1 > addressB.addressLine1) return 1;
  if (addressA.addressLine1 === addressB.addressLine1) return 0;
  if (addressA.addressLine1 < addressB.addressLine1) return -1;
}
