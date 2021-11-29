import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { BaseCodeTableComponent } from '../../../../../shared/components/templates/code-table/code-table.template';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { CaseTypesService } from '../services/case-types.service';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { tap } from 'rxjs/operators';
import { ReplaySubject } from 'rxjs';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { Validators } from '@angular/forms';

@Component({
  selector: 'code-table-case-types',
  templateUrl:
    '../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [CaseTypesService],
})
export class CaseTypesComponent extends BaseCodeTableComponent {
  protected url = '/case-types';
  public title = 'Case Types';
  protected searchable = false;
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'code',
      title: 'Code',
      type: FormFieldTypeEnum.Input,
      editable: false,
      validators: [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(4),
      ],
    },
    {
      columnId: 'description',
      title: 'Description',
      type: FormFieldTypeEnum.TextArea,
      validators: [
        Validators.required,
        Validators.maxLength(35),
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
    public service: CaseTypesService,
    public endpointsService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointsService, dialog, snackBar);
  }
}
