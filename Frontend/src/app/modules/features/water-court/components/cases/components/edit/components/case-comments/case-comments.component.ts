import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { CaseProgramTypes } from '../../../create/enums/caseProgramTypes';
import { CaseCommentsService } from './services/case-comments.service';

@Component({
  selector: 'app-case-comments',
  templateUrl: './case-comments.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './case-comments.component.scss',
  ],
  providers: [CaseCommentsService],
})
export class CaseCommentsComponent extends DataRowComponent {
  constructor(
    public service: CaseCommentsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  private _programType = null;
  public disableEdit = false;
  @Input() hasCaseAdminRole = null;
  @Input() set programType(value: string) {
    this._programType = value;
    if (
      this.programType === CaseProgramTypes.NA_PROGRAM &&
      !this.hasCaseAdminRole
    ) {
      this.disableEdit = true;
    } else {
      this.disableEdit = false;
    }
  }
  get programType(): string {
    return this._programType;
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'comments',
      title: '',
      type: FormFieldTypeEnum.TextArea,
      validators: [Validators.maxLength(4000)],
    },
  ];

  protected initFunction() {
    this.idArray = [this.route.snapshot.params.caseId];
    this._get();
  }

  protected setPermissions(): void {
    this.permissions = {
      canGET: this.endpointService.canGET(this.service.url),
      canPOST: this.endpointService.canPOST(this.service.url),
      canDELETE: this.endpointService.canDELETE(this.service.url),
      canPUT: this.endpointService.canPUT(this.service.url, 0),
    };
  }

  public dialogWidth = '600px';
}
