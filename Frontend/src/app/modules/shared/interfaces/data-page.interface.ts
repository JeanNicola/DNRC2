import { PageInterface } from 'src/app/modules/shared/models/page.interface';

export interface DataPageInterface<T> extends PageInterface {
  results: T[] | any;
}
