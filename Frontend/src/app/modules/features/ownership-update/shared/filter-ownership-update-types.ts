import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';

const allowedTypeInCreateOrUpdate = ['643 608', '643 COR', 'VER', 'WTC 608'];

export function filterOwnershipUpdateTypes(
  type: string,
  action: DataManagementDialogModes
) {
  if (action === DataManagementDialogModes.Search) return true;
  return !allowedTypeInCreateOrUpdate.includes(type);
}
