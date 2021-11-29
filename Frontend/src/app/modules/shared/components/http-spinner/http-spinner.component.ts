import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Component, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

/*
 * SpinnerComponent
 * When an HTTP call is made, this component shows/hides a spinning circle on the screen to tell
 * users work is being performed.
 *
 * To use this, include the <app-http-spinner></app-http-spinner> in the component HTML
 */
@Component({
  selector: 'app-http-spinner',
  templateUrl: './http-spinner.component.html',
  styleUrls: ['./http-spinner.component.scss'],
})
export class SpinnerComponent {
  constructor(public httpStateService: HttpStateService) {}
}

/*
 * HTTP state service. Creates an Observable used by the Spinner component to communuicate state
 */
@Injectable({
  providedIn: 'root',
})
export class HttpStateService {
  public isLoading = new BehaviorSubject(false);

  constructor() {}
}

/*
 * SpinnerHttpInterceptorService
 * HTTP Interceptor Service that catches each HTTP Request, increments a counter for each "active" HTTP request, and turns the spinner on.
 * When a response is received, decrement the counter of "active" HTTP requests.
 * When the counter of active HTTP requests is 0, turn off the spinner.
 */
@Injectable({
  providedIn: 'root',
})
export class SpinnerHttpInterceptorService implements HttpInterceptor {
  // private requests: HttpRequest<any>[] = [];
  private totalRequests = 0;

  constructor(public httpStateService: HttpStateService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    this.totalRequests++;

    // Tell the service to enable loading
    setTimeout(() => {
      this.httpStateService.isLoading.next(true);
    }, 0);

    return next.handle(request).pipe(
      finalize(() => {
        this.totalRequests--;
        if (this.totalRequests === 0) {
          // Tell the service to disable loading
          setTimeout(() => {
            this.httpStateService.isLoading.next(false);
          }, 0);
        }
      })
    );
  }
}
