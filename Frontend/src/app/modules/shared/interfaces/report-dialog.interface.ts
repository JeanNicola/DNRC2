import { ReportDefinition } from './report-definition.interface';

export interface ReportDialogInterface {
  title: string;
  reports: ReportDefinition[];
  headerData: any;
}
