import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { BaseCodeTableComponent } from '../../../../../shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { CaseAssignmentTypesService } from '../services/case-assignment-types.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { ReplaySubject } from 'rxjs';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { Validators } from '@angular/forms';

@Component({
  selector: 'code-table-case-assignment-types',
  templateUrl:
    '../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CaseAssignmentTypesService],
})
export class CaseAssignmentTypesComponent extends BaseCodeTableComponent {
  protected url = '/case-assignment-types';
  public title = 'Case Assignment Types';
  protected searchable = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'code',
      title: 'Code',
      type: FormFieldTypeEnum.Input,
      validators: [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(4),
      ],
      editable: false,
    },
    {
      columnId: 'assignmentType',
      title: 'Assignment Type',
      type: FormFieldTypeEnum.TextArea,
      validators: [
        Validators.required,
        Validators.maxLength(30),
        WRISValidators.preventNewLineCharacter,
      ],
    },
    {
      columnId: 'program',
      title: 'Program',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'programDescription',
      title: 'Program',
      type: FormFieldTypeEnum.Select,
      displayInInsert: false,
      displayInEdit: false,
      displayInSearch: false,
    },
  ];

  public populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the getPrograms http request and the selectArr
    // population only happens once
    this.observables.programs = new ReplaySubject(1);
    this.service.getPrograms().subscribe((programs: any) => {
      this._getColumn('program').selectArr = programs.results.map(
        (program) => ({
          name: program.description,
          value: program.value,
        })
      );

      this.observables.programs.next(programs);
      this.observables.programs.complete();
    });
  }

  initFunction(): void {
    this._get();
  }

  constructor(
    public service: CaseAssignmentTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }
}
