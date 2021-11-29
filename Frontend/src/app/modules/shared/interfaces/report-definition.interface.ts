export interface ReportDefinition {
  title: string;
  reportId?: string;
  setParams?: (report: ReportDefinition, data: any) => void;
  isAvailable?: (data: any) => boolean;
  url?: string;
  params?: Record<string, string | number>;
  type?: ReportTypes;
}

export enum ReportTypes {
  REPORT = 'report',
  SCANNED = 'scanned',
}
