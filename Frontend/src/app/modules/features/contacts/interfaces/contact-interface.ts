export interface Contact {
  contactId: string;
  lastName: string;
  contactType: string;
  addresses?: Address[];
  firstName?: string;
  middleInitial?: string;
  suffix?: string;
  contactStatus?: string;
}

export interface Address {
  customerId?: string | number;
  addressId?: string | number;
  addressLine1?: string;
  primaryMail?: 'Y' | 'N';
  isPrimMail?: boolean;
  primaryMailValue?: 'YES' | 'NO';
  foreignAddress?: 'Y' | 'N';
  isForeign?: boolean;
  foreignAddressValue?: 'YES' | 'NO';
  cityName?: string;
  cityId?: string;
  stateName?: string;
  stateCode?: string;
  zipCode?: string;
  zipCodeId?: number;
  unresolvedFlag?: 'Y' | 'N';
  unresolvedFlagValue?: 'YES' | 'NO';
  rtnMail?: boolean;
  addressLine2?: string;
  addressLine3?: string;
  foreignPostal?: string;
  dateCreated?: string | Date;
  createdByValue?: string;
  modifiedBy?: string | Date;
  modifiedByValue?: string;
  createdBy?: string;
  modReason?: string;
  pl4?: string;
}
