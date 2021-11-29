import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Sort } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { OtherParties } from '../../interfaces/other-parties-interface';
import { NoticeOtherPartiesService } from '../../services/notice-other-parties.service';

@Component({
  selector: 'app-other-parties',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [NoticeOtherPartiesService],
})
export class OtherPartiesComponent
  extends BaseCodeTableComponent
  implements OnChanges
{
  @Input() mailingJobSelectedId: string;

  public data: DataPageInterface<OtherParties>;
  public rows: OtherParties[];

  constructor(
    public service: NoticeOtherPartiesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Other Parties Notified';
  protected searchable = false;
  protected clickableRow = true;
  protected dblClickableRow = true;
  public hideInsert = true;
  public hideActions = true;
  public containerStyles = {
    width: '100%',
    height: 'fit-content',
    margin: '20px 0',
    background: 'rgb(250, 250, 250)',
  };

  public titleStyles = {
    fontSize: '16px',
  };

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'name',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
  ];

  public sortDirection = 'asc';
  public primarySortColumn = 'name';

  initFunction() {
    this.dataMessage = 'No data found';
  }

  ngOnChanges(changes: SimpleChanges) {
    this.data = { results: [] } as any;
    this.rows = [];

    if (changes.mailingJobSelectedId?.currentValue) {
      this.setRequestParams();
      this.idArray = [
        this.route.snapshot.params.id,
        changes.mailingJobSelectedId.currentValue,
      ];
      this._get();
    }
  }

  public _get(): void {
    this.dataMessage = 'Loading...';
    const currentMailingJobId = this.mailingJobSelectedId;
    // Data is subscribed here so page does not "flicker" to "Loading" each time new page is requested
    forkJoin({
      get: this.service.get(this.queryParameters, ...this.idArray),
      ...this.observables,
    }).subscribe(
      (data) => {
        if (currentMailingJobId !== this.mailingJobSelectedId) {
          return;
        }

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

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.queryParameters.sortColumn = sort.active;
      this.queryParameters.sortDirection = sort.direction;
      this._get();
    }
  }

  setRequestParams() {
    this.queryParameters = {
      sortDirection: '',
      sortColumn: '',
      pageSize: 25,
      pageNumber: 1,
      filters: {},
    };
  }

  public onRowDoubleClick(data: any): void {
    void this.router.navigate(['wris', 'contacts', data.contactId]);
  }
}
