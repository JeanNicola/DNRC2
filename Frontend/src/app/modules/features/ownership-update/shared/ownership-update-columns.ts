import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

const ownershipUpdateColumns: ColumnDefinitionInterface[] = [
  {
    columnId: 'ownershipUpdateId',
    title: 'Ownership Update ID',
    type: FormFieldTypeEnum.Input,
    width: 150,
  },
  {
    columnId: 'ownershipUpdateType',
    title: 'Ownership Update Type',
    type: FormFieldTypeEnum.Select,
    displayInTable: false,
  },
  {
    columnId: 'ownershipUpdateTypeValue',
    title: 'Ownership Update Type',
    type: FormFieldTypeEnum.Select,
    width: 250,
  },
  {
    columnId: 'receivedDate',
    title: 'Received or Sale Date',
    type: FormFieldTypeEnum.Date,
    width: 140,
  },
];

export function getOwnershipUpdateColumns() {
  return [...ownershipUpdateColumns];
}
