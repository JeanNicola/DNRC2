export interface OwnershipUpdateInsertInterface {
  sellers: number[];
  buyers: number[];
  waterRights: { waterRightNumber: number; version: number };
  ownershipUpdateType: string;
  receivedDate: string;
  pendingDORValidation: boolean;
  receivedAs608: boolean;
}
