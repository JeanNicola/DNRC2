export interface DataQueryParametersInterface {
  sortDirection?: string;
  sortColumn?: string;
  pageSize?: number;
  pageNumber?: number;
  filters?: { [key: string]: string };
}
