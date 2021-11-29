import { ValidatorFn } from '@angular/forms';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataManagementDialogModes } from './data-management-dialog.enum';

/*
 * Interface to call the data management dialog
 */
export interface DataManagementDialogInterface {
  mode: DataManagementDialogModes;
  title: string;
  columns: ColumnDefinitionInterface[];
  values: any;
  validators: ValidatorFn[];
}
