import { Validators } from '@angular/forms';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';

export const addressColumnsForDisplay: ColumnDefinitionInterface[] = [
  // This column is just for display purposes
  {
    columnId: 'isPrimMail',
    title: 'Primary Address',
    type: FormFieldTypeEnum.Checkbox,
    noSort: true,
    width: 100,
  },
  {
    columnId: 'completeAddress',
    title: 'Address',
    type: FormFieldTypeEnum.Input,
    noSort: true,
  },
  {
    columnId: 'foreignPostal',
    title: 'Country',
    type: FormFieldTypeEnum.Input,
    noSort: true,
  },
  {
    columnId: 'createdByValue',
    title: 'Created By',
    type: FormFieldTypeEnum.Input,
    displayInEdit: false,
    displayInInsert: false,
    displayInTable: false,
  },
  {
    columnId: 'createdDate',
    title: 'Created Date',
    type: FormFieldTypeEnum.Date,
    displayInEdit: false,
    displayInInsert: false,
    displayInTable: false,
  },
  {
    columnId: 'modReason',
    title: 'Notes',
    type: FormFieldTypeEnum.Input,
    displayInEdit: false,
    displayInInsert: false,
    displayInTable: false,
  },
];

export const addressColumnsForForm: ColumnDefinitionInterface[] = [
  {
    columnId: 'addressLine1',
    title: 'Address Line 1',
    type: FormFieldTypeEnum.Input,
    validators: [Validators.required, Validators.maxLength(50)],
  },
  {
    columnId: 'zipCode',
    title: 'Zip',
    validators: [
      WRISValidators.integer,
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(5),
    ],
    type: FormFieldTypeEnum.Input,
  },
  {
    columnId: 'pl4',
    title: 'Plus 4',
    validators: [
      WRISValidators.integer,
      Validators.maxLength(4),
      Validators.minLength(4),
    ],
    type: FormFieldTypeEnum.Input,
  },
  {
    columnId: 'cityAndState',
    title: 'City and State',
    type: FormFieldTypeEnum.Select,
    validators: [Validators.required],
  },
  {
    columnId: 'addressLine2',
    title: 'Address Line 2',
    type: FormFieldTypeEnum.Input,
    validators: [Validators.maxLength(50)],
  },
  {
    columnId: 'addressLine3',
    title: 'Address Line 3',
    type: FormFieldTypeEnum.Input,
    displayInTable: false,
    validators: [Validators.maxLength(50)],
  },
];
export const foreignAddressColumns: ColumnDefinitionInterface[] = [
  {
    columnId: 'addressLine1',
    title: 'Address Line 1',
    type: FormFieldTypeEnum.Input,
    validators: [Validators.required, Validators.maxLength(50)],
  },
  {
    columnId: 'cityName',
    title: 'Foreign City',
    type: FormFieldTypeEnum.Input,
    validators: [Validators.required],
    displayInEdit: false,
  },
  {
    columnId: 'stateName',
    title: 'Foreign Province or State',
    type: FormFieldTypeEnum.Input,
    validators: [Validators.required],
    displayInEdit: false,
  },
  {
    columnId: 'addressLine2',
    title: 'Address Line 2',
    type: FormFieldTypeEnum.Input,
    validators: [Validators.required, Validators.maxLength(50)],
    displayInInsert: false,
    displayInEdit: true,
    hint: 'Add City State Zip for foreign locations.',
  },
  {
    columnId: 'foreignPostal',
    title: 'Country',
    type: FormFieldTypeEnum.Input,
    validators: [Validators.required],
  },
  {
    columnId: 'zipCode',
    title: 'Foreign Zip',
    validators: [Validators.required],
    type: FormFieldTypeEnum.Input,
    displayInEdit: false,
  },
  {
    columnId: 'addressLine3',
    title: 'Address Line 3',
    type: FormFieldTypeEnum.Input,
    validators: [Validators.maxLength(50)],
  },
];
