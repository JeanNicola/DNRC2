import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ContactApplicationsService } from '../../services/contact-applications.service';

@Component({
  selector: 'app-applications',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './applications.component.scss',
  ],
  providers: [ContactApplicationsService],
})
export class ApplicationsComponent extends BaseCodeTableComponent {
  constructor(
    public service: ContactApplicationsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() containerStyles = {};

  public title = '';
  public hideInsert = true;
  public hideEdit = true;
  public hideDelete = true;
  public searchable = false;
  public hideActions = true;
  protected clickableRow = false;
  protected dblClickableRow = true;

  public primarySortColumn = 'applicationId';
  public sortDirection = 'asc';

  initFunction(): void {
    this.dataMessage = 'No data found';
    this.idArray = [this.route.snapshot.params.id];
    this._get();
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationTypeCode',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationTypeDescription',
      title: 'Application Type Description',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dateTimeReceived',
      title: 'Received Date',
      type: FormFieldTypeEnum.DateTime,
    },
    {
      columnId: 'objection',
      title: 'Objection?',
      type: FormFieldTypeEnum.Input,
      noSort: true,
    },
  ];

  // Handle the onRowDoubleClick event
  public onRowDoubleClick(data: any): void {
    this.router.navigate(['wris', 'applications', data.applicationId]);
  }

  /*
   * Get the data using the data service
   */
  protected _get(): void {
    this.dataMessage = 'Loading...';
    // Data is subscribed here so page does not "flicker" to "Loading" each time new page is requested
    forkJoin({
      get: this.service.get(this.queryParameters, ...this.idArray),
      ...this.observables,
    }).subscribe(
      (data) => {
        this.data = this._getHelperFunction(data);
        this.rows = this.data.results;

        if (data?.get?.results?.length) {
          this.dataMessage = null;
        } else {
          this.dataMessage = 'No data found';
        }
      },
      () => {
        this.dataMessage = 'No data found';
      }
    );
  }
}
