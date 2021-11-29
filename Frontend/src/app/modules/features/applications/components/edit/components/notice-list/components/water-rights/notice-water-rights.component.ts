import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { WaterRightInterface } from '../../interfaces/water-right-interface';
import { NoticeWaterRightsService } from '../../services/notice-water-rights.service';

@Component({
  selector: 'app-notice-water-rights',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [NoticeWaterRightsService],
})
export class NoticeWaterRightsComponent
  extends BaseCodeTableComponent
  implements OnChanges
{
  @Input() mailingJobSelectedId: string;

  public data: DataPageInterface<WaterRightInterface>;
  public rows: WaterRightInterface[];

  constructor(
    public service: NoticeWaterRightsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = 'Water Rights Notified';
  protected searchable = false;
  public hideInsert = true;
  public hideActions = true;
  protected clickableRow = true;
  protected dblClickableRow = true;
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
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'waterRightNumber',
      title: 'WR #',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'ext',
      title: 'Ext',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'typeDescription',
      title: 'WR Type',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'status',
      title: 'WR Status',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
  ];

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

  public _getHelperFunction(data: any): any {
    if (data?.get?.results) {
      return data.get;
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

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.queryParameters.pageSize = pagingOptions.pageSize;
      this.queryParameters.pageNumber = pagingOptions.pageIndex + 1;
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
    void this.router.navigate(['wris', 'water-rights', data.id]);
  }
}
