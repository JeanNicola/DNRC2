import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { RelatedRightsService } from 'src/app/modules/features/related-rights/services/related-rights.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { UnitService } from './services/unit.service';

@Component({
  selector: 'app-shared-elements',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    './shared-elements.component.scss',
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [RelatedRightsService],
})
export class SharedElementsComponent extends DataRowComponent {
  constructor(
    public service: RelatedRightsService,
    public unitService: UnitService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() relatedRightId;

  public showLoading = false;
  public title = 'Shared Elements';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'maxFlowRate',
      title: 'Max Flow Rate',
      type: FormFieldTypeEnum.Input,
      validators: [
        WRISValidators.integer,
        WRISValidators.requireOtherFieldIfNonNull('flowRateUnit'),
        WRISValidators.integer,
        WRISValidators.isNumber(10, 0),
      ],
    },
    {
      columnId: 'flowRateUnitVal',
      title: 'Unit',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'flowRateUnit',
      title: 'Unit',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [WRISValidators.requireOtherFieldIfNonNull('maxFlowRate')],
    },
    {
      columnId: 'maxAcres',
      title: 'Max Acres',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
    },
    {
      columnId: 'maxVolume',
      title: 'Max Volume',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
    },
  ];

  public initFunction(): void {
    this.idArray = [this.relatedRightId];
    this._get();
  }

  public populateDropdowns(): void {
    this.observables.ownershipUpdateType = new ReplaySubject(1);

    this.unitService.get(this.queryParameters).subscribe((unitTypes) => {
      unitTypes.results.unshift({
        value: null,
        description: null,
      });
      this._getColumn('flowRateUnit').selectArr = unitTypes.results.map(
        (unitType: { value: string; description: string }) => ({
          name: unitType.description,
          value: unitType.value,
        })
      );
      this.observables.ownershipUpdateType.next(unitTypes);
      this.observables.ownershipUpdateType.complete();
    });
  }
}
