export interface SelectInterface {
  readonly name?: string;
  readonly value: string | number;
}

// While value is required, name is optional. When name is not present, value is used in place of name in <app-form-field>.
