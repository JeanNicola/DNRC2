import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Resolve,
  RouterStateSnapshot,
} from '@angular/router';
import { forkJoin, Observable, of, ReplaySubject, Subject } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { Reference } from 'src/app/modules/shared/interfaces/reference.interface';
import { AliquotsService } from 'src/app/modules/shared/services/aliquots.service';
import {
  CountiesService,
  County,
} from 'src/app/modules/shared/services/counties.service';
import { RangeDirectionsService } from 'src/app/modules/shared/services/range-directions.service';
import { TownshipDirectionsService } from 'src/app/modules/shared/services/township-directions.service';
import { DiversionTypesService } from './diversion-types.service';
import { MajorTypeService } from './major-type.service';
import { MeansOfDiversionService } from './means-of-diversion.service';
import { MinorTypesService } from './minor-types.service';
import { PodOriginsService } from './pod-origins.service';
import { PodTypeService } from './pod-type.service';
import { SourceOriginsService } from './source-origins.service';

// This can only be used in the Water Rights Module out of the box
// otherwise, all the dependent services need to be provided somewhere
type SelectionOption = { name: string; value: string };
@Injectable()
export class PodDropdownService implements Resolve<boolean> {
  private _counties: SelectionOption[] = [];
  public get counties(): SelectionOption[] {
    return this._counties;
  }

  private _aliquots: SelectionOption[] = [];
  public get aliquots(): SelectionOption[] {
    return this._aliquots;
  }

  private _townshipDirections: SelectionOption[] = [];
  public get townshipDirections(): SelectionOption[] {
    return this._townshipDirections;
  }

  private _rangeDirections: SelectionOption[] = [];
  public get rangeDirections(): SelectionOption[] {
    return this._rangeDirections;
  }

  private _podOriginCode: SelectionOption[] = [];
  public get podOriginCode(): SelectionOption[] {
    return this._podOriginCode;
  }

  private _sourceOriginCode: SelectionOption[] = [];
  public get sourceOriginCode(): SelectionOption[] {
    return this._sourceOriginCode;
  }

  private _majorTypeCode: SelectionOption[] = [];
  public get majorTypeCode(): SelectionOption[] {
    return this._majorTypeCode;
  }

  private _minorTypeCode: SelectionOption[] = [];
  public get minorTypeCode(): SelectionOption[] {
    return this._minorTypeCode;
  }

  private _meansOfDiversionCode: SelectionOption[] = [];
  public get meansOfDiversionCode(): SelectionOption[] {
    return this._meansOfDiversionCode;
  }

  private _podTypeCode: SelectionOption[] = [];
  public get podTypeCode(): SelectionOption[] {
    return this._podTypeCode;
  }

  private _ditchTypeCode: SelectionOption[] = [];
  public get ditchTypeCode(): SelectionOption[] {
    return this._ditchTypeCode;
  }

  private observables: { [key: string]: Subject<boolean> } = {};

  constructor(
    private snackBarService: SnackBarService,
    private countyService: CountiesService,
    private aliquotService: AliquotsService,
    private townshipDirectionService: TownshipDirectionsService,
    private rangeDirectionService: RangeDirectionsService,
    private podOriginService: PodOriginsService,
    private sourceOriginService: SourceOriginsService,
    private majorTypeService: MajorTypeService,
    private meansService: MeansOfDiversionService,
    private podTypeService: PodTypeService,
    private diversionTypeService: DiversionTypesService,
    private minorTypeService: MinorTypesService
  ) {
    // load the dropsdowns
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
      this._loadingState === PodDropdownState.LOADED ||
      this._loading.asObservable().pipe(catchError((err) => of(false)))
    );
  }

  private _loadingState: PodDropdownState = PodDropdownState.LOADING;

  private loadDropdowns(): void {
    this.observables.countyId = new Subject();
    this.countyService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._counties = data.results.map((row: County) => ({
            name: `${row.name}, ${row.stateCode}`,
            value: row.id,
          }));
        }
        this.observables.countyId.next(true);
        this.observables.countyId.complete();
      },
      error: () => {
        this.observables.countyId.error(false);
      },
    });

    this.observables.aliquots = new Subject();
    this.aliquotService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._aliquots = data.results.map((row: Reference) => ({
            name: row.value,
            value: row.value,
          }));
        }

        // Add an option to clear out Aliquots value
        this._aliquots.unshift({ name: '', value: null });
        this.observables.aliquots.next(true);
        this.observables.aliquots.complete();
      },
      error: () => {
        this.observables.aliquots.error(false);
      },
    });

    this.observables.townshipDirection = new Subject();
    this.townshipDirectionService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._townshipDirections = data.results.map((row: Reference) => ({
            name: row.value,
            value: row.value,
          }));
          this.observables.townshipDirection.next(true);
          this.observables.townshipDirection.complete();
        }
      },
      error: () => {
        this.observables.townshipDirection.error(false);
      },
    });

    this.observables.rangeDirection = new Subject();
    this.rangeDirectionService.get({}).subscribe({
      next: (data: any) => {
        if (data.results) {
          this._rangeDirections = data.results.map((row: Reference) => ({
            name: row.value,
            value: row.value,
          }));
        }

        this.observables.rangeDirection.next(true);
        this.observables.rangeDirection.complete();
      },
      error: () => {
        this.observables.rangeDirection.error(false);
      },
    });

    this.observables.podOriginCode = new Subject();
    this.podOriginService.get({}).subscribe({
      next: (data: { results: Reference[] }) => {
        if (data.results) {
          this._podOriginCode = data.results.map((row: Reference) => ({
            name: row.description,
            value: row.value,
          }));
        }
        this.observables.podOriginCode.next(true);
        this.observables.podOriginCode.complete();
      },
      error: () => {
        this.observables.podOriginCode.error(false);
      },
    });

    this.observables.sourceOriginCode = new ReplaySubject(1);
    this.sourceOriginService.get({}).subscribe({
      next: (data: { results: Reference[] }) => {
        if (data.results) {
          this._sourceOriginCode = data.results.map((row: Reference) => ({
            name: row.description,
            value: row.value,
          }));
        }
        this.observables.sourceOriginCode.next(true);
        this.observables.sourceOriginCode.complete();
      },
      error: () => {
        this.observables.sourceOriginCode.error(false);
      },
    });

    // Major Type Codes
    this.observables.majorTypeCode = new ReplaySubject(1);
    this.majorTypeService.get({}).subscribe({
      next: (data: { results: Reference[] }) => {
        if (data.results) {
          this._majorTypeCode = data.results.map((row: Reference) => ({
            name: row.description,
            value: row.value,
          }));
        }
        this.observables.majorTypeCode.next(true);
        this.observables.majorTypeCode.complete();
      },
      error: () => {
        this.observables.majorTypeCode.error(false);
      },
    });

    // Minor Type Codes
    this.observables.minorTypeCode = new ReplaySubject(1);
    this.minorTypeService.get({}).subscribe({
      next: (data: { results: Reference[] }) => {
        if (data.results) {
          this._minorTypeCode = data.results.map((row: Reference) => ({
            name: row.description,
            value: row.value,
          }));
        }
        this.observables.minorTypeCode.next(true);
        this.observables.minorTypeCode.complete();
      },
      error: () => {
        this.observables.minorTypeCode.error(false);
      },
    });

    // Means of Diversion
    this.observables.meansOfDiversionCode = new ReplaySubject(1);
    this.meansService.get({}).subscribe({
      next: (data: { results: Reference[] }) => {
        if (data.results) {
          this._meansOfDiversionCode = data.results.map((row: Reference) => ({
            name: row.description,
            value: row.value,
          }));
        }
        this.observables.meansOfDiversionCode.next(true);
        this.observables.meansOfDiversionCode.complete();
      },
      error: () => {
        this.observables.meansOfDiversionCode.error(false);
      },
    });

    this.observables.podTypeCode = new ReplaySubject(1);
    this.podTypeService.get({}).subscribe({
      next: (data: { results: Reference[] }) => {
        if (data.results) {
          this._podTypeCode = data.results.map((row: Reference) => ({
            name: row.description,
            value: row.value,
          }));
        }
        this.observables.podTypeCode.next(true);
        this.observables.podTypeCode.complete();
      },
      error: () => {
        this.observables.podTypeCode.error(false);
      },
    });

    this.observables.ditchTypeCode = new ReplaySubject(1);
    this.diversionTypeService.get({}).subscribe({
      next: (data: { results: Reference[] }) => {
        if (data.results) {
          this._ditchTypeCode = data.results.map((row: Reference) => ({
            name: row.description,
            value: row.value,
          }));
        }
        this.observables.ditchTypeCode.next(true);
        this.observables.ditchTypeCode.complete();
      },
      error: () => {
        this.observables.ditchTypeCode.error(false);
      },
    });

    forkJoin({
      ...this.observables,
    }).subscribe({
      next: (d) => {
        this._loadingState = PodDropdownState.LOADED;
        this._loading.next(true);
        this._loading.complete();
      },
      error: (err) => {
        this._loadingState = PodDropdownState.ERROR;
        const errMsg =
          'The system is experiencing issues. Data for POD dropdown selections cannot be loaded. ' +
          'Please open a ticket with the Help Desk to get this resolved.';
        this.snackBarService.open(errMsg, 'Dismiss', 0);
        this._loading.error(false);
      },
    });
  }
}

export enum PodDropdownState {
  LOADING,
  LOADED,
  ERROR,
}
