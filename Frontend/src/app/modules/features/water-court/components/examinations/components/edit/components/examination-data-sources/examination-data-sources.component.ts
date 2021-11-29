import { Component } from '@angular/core';
import { Subject } from 'rxjs';
import { DataSourceTypes } from './components/constants/DataSourceTypes';

@Component({
  selector: 'app-examination-data-sources',
  templateUrl: './examination-data-sources.component.html',
  styleUrls: ['./examination-data-sources.component.scss'],
})
export class ExaminationDataSourcesComponent {
  public reloadParcels = new Subject();
  public reloadDataSources = new Subject();
  public dataSourceSelected = null;
  public dataSourceTypes = DataSourceTypes;
}
