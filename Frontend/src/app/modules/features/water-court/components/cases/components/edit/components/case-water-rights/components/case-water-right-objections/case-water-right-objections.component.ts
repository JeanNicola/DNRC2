import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ObjectionStatusesService } from 'src/app/modules/features/water-court/components/objections/components/search/services/objection-statuses.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { CaseWaterRightObjectionsService } from './services/case-water-right-objections.service';
import { UpdateCaseObjectionsService } from './services/update-case-objections.service';

@Component({
  selector: 'app-case-water-right-objections',
  templateUrl:
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    CaseWaterRightObjectionsService,
    UpdateCaseObjectionsService,
    ObjectionStatusesService,
  ],
})
export class CaseWaterRightObjectionsComponent extends BaseCodeTableComponent {
  constructor(
    public service: CaseWaterRightObjectionsService,
    public updateCaseObjectionsService: UpdateCaseObjectionsService,
    public objectionStatusesService: ObjectionStatusesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined) && this.service) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  public clickableRow = true;
  public hideInsert = true;
  public hideDelete = true;
  public dialogWidth = '500px';
  public isInMain = false;
  public title = 'Objections';
  public primarySortColumn = 'id';
  public searchable = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'id',
      title: 'Objection #',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'objectionTypeDescription',
      title: 'Objection Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'dateReceived',
      title: 'Filed Date',
      type: FormFieldTypeEnum.Date,
      displayInEdit: false,
    },
    {
      columnId: 'late',
      title: 'Late',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'objectionStatusDescription',
      title: 'Objection Status',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'status',
      title: 'Objection Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
  ];

  protected populateDropdowns(): void {
    this.observables.statuses = new ReplaySubject(1);

    this.objectionStatusesService.get().subscribe((statuses) => {
      const statusesArr: { value: string; name: string }[] =
        statuses.results.map(
          (status: { value: string; description: string }) => ({
            name: status.description,
            value: status.value,
          })
        );
      statusesArr.unshift({
        name: null,
        value: null,
      });
      this._getColumn('status').selectArr = statusesArr;

      this.observables.statuses.next(statusesArr);
      this.observables.statuses.complete();
    });
  }

  private redirectToObjections(id: number) {
    this.router.navigate(['wris', 'water-court', 'objections', id]);
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToObjections(data.id);
  }

  protected getEditDialogTitle() {
    return 'Update Objection Record';
  }

  protected _getUpdateService(): BaseDataService {
    return this.updateCaseObjectionsService;
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [originalData.id];
  }

  protected setPermissions(): void {
    this.permissions = {
      canGET: this.endpointService.canGET(this.service.url),
      canPOST: this.endpointService.canPOST(this.service.url),
      canDELETE: this.endpointService.canDELETE(this.service.url),
      canPUT: this.endpointService.canPUT(this.updateCaseObjectionsService.url),
    };
  }

  protected setTableFocus(): void {}
  protected setInitialFocus(): void {}
}
