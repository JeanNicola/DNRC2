export interface OwnershipUpdatePUTInterface {
  ownershipUpdateId: string;
  ownershipUpdateType: string;
  dateReceived: string;
  dateProcessed?: string;
  dateTerminated: string;
  pendingDor: 'Y' | 'N';
  receivedAs608: 'Y' | 'N';
}
