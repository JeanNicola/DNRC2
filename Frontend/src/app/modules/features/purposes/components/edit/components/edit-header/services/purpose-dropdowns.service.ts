import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Resolve,
  RouterStateSnapshot,
} from '@angular/router';
import { forkJoin, Observable, of, Subject } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { OwnerOriginsService } from 'src/app/modules/features/water-rights/services/owner-origins.service';
import { YesNoValuesService } from 'src/app/modules/shared/services/yes-no-values.service';
import { LeaseYearValuesService } from '../components/purpose-insert-dialog/services/lease-year-values.service';
import { ClimaticAreasService } from './climatic-areas.service';
import { IrrigationTypesService } from './irrigation-types.service';
import { PurposeTypesService } from './purpose-types.service';

type SelectionOption = { name: string; value: string };
@Injectable()
export class PurposeDropdownsService implements Resolve<boolean> {
  private _purposeCodes: SelectionOption[] = [];
  public get purposeCodes(): SelectionOption[] {
    return this._purposeCodes;
  }

  private _ownerOrigins: SelectionOption[] = [];
  public get ownerOrigins(): SelectionOption[] {
    return this._ownerOrigins;
  }

  private _yesNoValues: SelectionOption[] = [];
  public get yesNoValues(): SelectionOption[] {
    return this._yesNoValues;
  }

  private _irrigationTypes: SelectionOption[] = [];
  public get irrigationTypes(): SelectionOption[] {
    return this._irrigationTypes;
  }

  private _climaticAreas: SelectionOption[] = [];
  public get climaticAreas(): SelectionOption[] {
    return this._climaticAreas;
  }

  private _leaseYearValues: SelectionOption[] = [];
  public get leaseYearValues(): SelectionOption[] {
    return this._leaseYearValues;
  }

  private observables: { [key: string]: Subject<boolean> } = {};

  constructor(
    private purposeTypesService: PurposeTypesService,
    private ownerOriginsService: OwnerOriginsService,
    private yesNoValuesService: YesNoValuesService,
    private irrigationTypesService: IrrigationTypesService,
    private climaticAreasService: ClimaticAreasService,
    private leaseYearValuesService: LeaseYearValuesService,
    private snackBarService: SnackBarService
  ) {
    // load the dropdowns
    this.loadDropdowns();
  }

  // This is here so service can be used in routing guard to ensure data is loaded before
  // components using the service are started
  private _loading = new Subject<boolean>();
  public resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | boolean {
    return (
      this._loadingState === PurposeDropdownState.LOADED ||
      this._loading.asObservable().pipe(catchError((err) => of(false)))
    );
  }

  private _loadingState: PurposeDropdownState = PurposeDropdownState.LOADING;

  private loadDropdowns(): void {
    // Purpose Codes
    this.observables.purposeCode = new Subject<boolean>();
    this.purposeTypesService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._purposeCodes = data.results.map(
            (row: { value: string; description: string }) => ({
              name: `${row.description} (${row.value})`,
              value: row.value,
            })
          );
        }
        this.observables.purposeCode.next(true);
        this.observables.purposeCode.complete();
      },
      error: () => {
        this.observables.purposeCode.error(false);
      },
    });

    // Origins
    this.observables.origins = new Subject();
    this.ownerOriginsService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._ownerOrigins = data.results.map(
            (row: { value: string; description: string }) => ({
              name: row.description,
              value: row.value,
            })
          );
        }
        this.observables.origins.next(true);
        this.observables.origins.complete();
      },
      error: () => {
        this.observables.origins.error(false);
      },
    });

    // YES/NO
    this.observables.yesNoOptions = new Subject();
    this.yesNoValuesService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._yesNoValues = data.results.map(
            (row: { value: string; description: string }) => ({
              name: row.description,
              value: row.value,
            })
          );
        }
        // Add an option to clear out Yes/No value
        this._yesNoValues.unshift({
          name: '',
          value: '',
        });
        this.observables.yesNoOptions.next(true);
        this.observables.yesNoOptions.complete();
      },
      error: () => {
        this.observables.yesNoOptions.error(false);
      },
    });

    // Irrigation Types
    this.observables.irrigationCode = new Subject();
    this.irrigationTypesService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._irrigationTypes = data.results.map(
            (row: { value: string; description: string }) => ({
              name: row.description,
              value: row.value,
            })
          );
        }

        this.observables.irrigationCode.next(true);
        this.observables.irrigationCode.complete();
      },
      error: () => {
        this.observables.irrigationCode.error(false);
      },
    });

    // Climatic Areas
    this.observables.climaticCode = new Subject();
    this.climaticAreasService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._climaticAreas = data.results.map(
            (row: { value: string; description: string }) => ({
              name: row.description,
              value: row.value,
            })
          );
        }

        // Add an option to clear out Climatic Area value
        this._climaticAreas.unshift({
          name: null,
          value: null,
        });
        this.observables.climaticCode.next(true);
        this.observables.climaticCode.complete();
      },
      error: () => {
        this.observables.climaticCode.error(false);
      },
    });

    this.observables.leaseYear = new Subject();
    this.leaseYearValuesService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._leaseYearValues = data.results.map(
            (row: { value: string; description: string }) => ({
              name: row.description,
              value: row.value,
            })
          );
        }

        // Add an option to clear out Lease Year value
        this._leaseYearValues.unshift({
          name: null,
          value: null,
        });

        this.observables.leaseYear.next(true);
        this.observables.leaseYear.complete();
      },
      error: () => {
        this.observables.leaseYear.error(false);
      },
    });

    // Get the values for the dropdowns
    forkJoin({
      ...this.observables,
    }).subscribe({
      next: () => {
        this._loadingState = PurposeDropdownState.LOADED;
        this._loading.next(true);
        this._loading.complete();
      },
      error: () => {
        this._loadingState = PurposeDropdownState.ERROR;
        const errMsg =
          'The system is experiencing issues. Data for Purposes dropdown selections cannot be loaded. ' +
          'Please open a ticket with the Help Desk to get this resolved.';
        this.snackBarService.open(errMsg, 'Dismiss', 0);
        this._loading.error(false);
      },
    });
  }
}

export enum PurposeDropdownState {
  LOADING,
  LOADED,
  ERROR,
}
