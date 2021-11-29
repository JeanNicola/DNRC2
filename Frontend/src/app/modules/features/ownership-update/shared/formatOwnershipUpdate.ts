import { OwnershipUpdate } from '../interfaces/ownership-update';

export function formatOwnershipUpdateForDisplay(
  ownershipUpdate: OwnershipUpdate
) {
  if (!ownershipUpdate) return;
  return {
    ...ownershipUpdate,
    isPendingDor: ownershipUpdate.pendingDor === 'Y',
    isReceivedAs608: ownershipUpdate.receivedAs608 === 'Y',
  };
}
