import { PageInterface } from 'src/app/modules/shared/models/page.interface';
import { StateCodesRowInterface } from './state-codes-row-interface';
export interface StateCodesPageInterface extends PageInterface {
  readonly results: StateCodesRowInterface[];
}
