import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { RelatedRightsService } from '../../services/related-rights.service';
import { RelatedRightCreationDto } from '../interfaces/related-right.interface';
import { InsertWaterRightComponent } from '../search/components/water-rights/components/insert-water-right/insert-water-right.component';
import { RelationshipTypesService } from '../search/services/relationship-types.service';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss'],
  providers: [RelatedRightsService, RelationshipTypesService],
})
export class CreateComponent
  extends DataManagementDialogComponent
  implements AfterViewInit
{
  @ViewChild('createButtonFocus', { static: false })
  createButtonFocus: MatButton;

  constructor(
    public contactStatusService: RelatedRightsService,
    public relationshipTypesService: RelationshipTypesService,
    public dialogRef: MatDialogRef<CreateComponent>,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router
  ) {
    super(dialogRef);
  }

  public basicInformationFormGroup: FormGroup = new FormGroup({});
  public title = 'Create New Related Right';
  public observables: { [key: string]: ReplaySubject<unknown> } = {};

  // Water Right Info
  public waterRights = [];
  public waterRightsDataSource = new MatTableDataSource(this.waterRights);
  get waterRightColumns() {
    const columns: ColumnDefinitionInterface[] = [
      {
        columnId: 'completeWaterRightNumber',
        title: 'Water Right #',
        type: FormFieldTypeEnum.Input,
        displayInSearch: false,
      },
      {
        columnId: 'typeDescription',
        title: 'Water Right Type',
        type: FormFieldTypeEnum.Input,
        displayInSearch: false,
      },
      {
        columnId: 'status',
        title: 'Water Right Status',
        type: FormFieldTypeEnum.Input,
        displayInSearch: false,
      },
      {
        columnId: 'version',
        title: 'Version',
        type: FormFieldTypeEnum.Input,
        displayInSearch: false,
      },
      {
        columnId: 'waterRightNumber',
        title: 'Water Right #',
        type: FormFieldTypeEnum.Input,
        displayInTable: false,
        displayInEdit: false,
      },
      {
        columnId: 'basin',
        title: 'Basin',
        type: FormFieldTypeEnum.Input,
        displayInTable: false,
        displayInEdit: false,
      },
      {
        columnId: 'ext',
        title: 'Ext',
        type: FormFieldTypeEnum.Input,
        displayInTable: false,
        displayInEdit: false,
      },
    ];

    return [...columns.map((obj) => Object.assign({}, obj))];
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'relationshipType',
      title: 'Relationship Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
  ];

  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public permissions = {
    canGET: false,
    // canDELETE is being manually set to true since we perform the delete locally
    canDELETE: true,
    canPUT: false,
  };

  public ngAfterViewInit() {
    const firstFormField = document.querySelector('mat-select') as HTMLElement;
    // setTimeout used to avoid ExpressionChangedAfterItHasBeenCheckedError
    if (firstFormField) {
      setTimeout(() => {
        firstFormField.focus();
      });
    }
  }

  protected initFunction(): void {
    this.populateDropdowns();
  }

  public populateDropdowns(): void {
    this.observables.relationshipType = new ReplaySubject(1);

    this.relationshipTypesService
      .get(this.queryParameters)
      .subscribe((relationshipTypes) => {
        this.columns[0].selectArr = relationshipTypes.results.map(
          (type: { value: string; description: string }) => ({
            name: type.description,
            value: type.value,
          })
        );
        this.observables.relationshipType.next(relationshipTypes);
        this.observables.relationshipType.complete();
      });
  }

  private sortWaterRights(waterRights) {
    return waterRights.sort((a, b) => {
      if (a.completeWaterRightNumber > b.completeWaterRightNumber) {
        return 1;
      }
      if (a.completeWaterRightNumber < b.completeWaterRightNumber) {
        return -1;
      }
      return 0;
    });
  }

  public onInsertWaterRight() {
    const dialogRef = this.dialog.open(InsertWaterRightComponent, {
      data: {
        title: 'Add New Water Right',
        values: {},
        columns: this.waterRightColumns,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // We use waterRightId and versionId to determine if two rows are the same record
        const waterRightKeys = this.waterRights.map(
          (waterRight) => `${waterRight.waterRightId}-${waterRight.versionId}`
        );
        // Filter duplicate records
        const resultsToInsert = result.filter((wr) => {
          return (
            waterRightKeys.indexOf(`${wr.waterRightId}-${wr.versionId}`) === -1
          );
        });
        const duplicates = result.length - resultsToInsert.length;
        if (duplicates) {
          this.snackBar.open(
            duplicates === 1
              ? `${duplicates} Duplicate water right was ignored.`
              : `${duplicates} Duplicate water rights were ignored.`,
            null,
            2000
          );
        }
        // Locally insert the water rights
        if (resultsToInsert.length) {
          this.waterRights.push(...resultsToInsert);
          this.waterRights = this.sortWaterRights(this.waterRights);
          this.waterRightsDataSource.data = this.waterRights;
        }
      }
      // Reset focus
      this.createButtonFocus.focus();
    });
  }

  public onDeleteWaterRight(row: number) {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this.waterRights.splice(row, 1);
        this.waterRightsDataSource._updateChangeSubscription();
      }
    });
  }

  public save() {
    // Collect data
    const data = { ...this.basicInformationFormGroup.getRawValue() };
    data.waterRights = this.waterRights.map((waterRight) => {
      return {
        waterRightId: waterRight.waterRightId,
        versionId: waterRight.versionId,
      };
    });
    // Create dto
    const dto: RelatedRightCreationDto = {
      relationshipType: data.relationshipType,
      waterRights: data.waterRights,
    };
    // Return dto
    this.dialogRef.close(dto);
  }
}
