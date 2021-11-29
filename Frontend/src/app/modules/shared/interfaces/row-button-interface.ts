export enum ButtonPositions {
  TOP_RIGHT_CORNER = 'TOP_RIGHT_CORNER',
}

export interface RowButtonDefinition {
  title?: string;
  tooltip?: string;
  position?: any;
  onClick?: Function;
  disabled?: boolean;
}
