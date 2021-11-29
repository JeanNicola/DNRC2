export interface PageInterface {
  readonly pageSize: number;
  readonly pageNumber?: number;
  readonly currentPage: number;
  readonly totalPages: number;
  readonly totalElements: number;
  readonly filters?: {
    [key: string]: string;
  };
  readonly sortDirection: 'ASC' | 'DESC';
  readonly sortColumn: string;
}
