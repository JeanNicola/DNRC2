import { ColumnDefinitionInterface } from '../interfaces/column-definition.interface';

export const getColumn = (
  columnId: string,
  columns: ColumnDefinitionInterface[]
): ColumnDefinitionInterface =>
  columns.filter((item) => item.columnId === columnId)[0];
