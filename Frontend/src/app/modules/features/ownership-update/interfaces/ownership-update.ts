export interface OwnershipUpdate {
  ownershipUpdateId: number;
  ownershipUpdateType: string;
  ownershipUpdateTypeVal?: string;
  receivedDate?: Date | string;
  saleDate?: Date | string;
  processedDate?: Date | string;
  terminatedDate?: Date | string;
  pendingDor?: string;
  isPendingDor?: boolean;
  receivedAs608?: string;
  isReceivedAs608?: boolean;
  canTransfer?: boolean;
}

export enum OwnershipUpdateTypes {
  SELLER = 'seller',
  BUYER = 'buyer',
}

export enum SearchTypes {
  OWNERSHIPUPDATE = 'ownershipupdate',
  SELLER = 'seller',
  BUYER = 'buyer',
}
