import { PageInterface } from 'src/app/modules/shared/models/page.interface';
import { CountiesRowInterface } from './counties-row.interface';
export interface CountiesPageInterface extends PageInterface {
  readonly results: CountiesRowInterface[];
}
