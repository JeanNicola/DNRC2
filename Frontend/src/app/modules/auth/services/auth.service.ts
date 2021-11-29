import { UserInfoInterface } from '../interface/user-info.interface';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { UserCredentialsInterface } from '../../core/interfaces/user-credentials.interface';
import { Router } from '@angular/router';
import { SessionStorageService } from '../../core/services/session-storage/session-storage.service';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private router: Router,
    private sessionStorage: SessionStorageService
  ) {}

  public login(
    username: string,
    password: string
  ): Observable<UserInfoInterface> {
    const userCrendentials: UserCredentialsInterface =
      this._createUserCrendtials(username, password);

    const url = `${environment.api.baseUrl}/api/auth/login`;
    const body: string = window.btoa(JSON.stringify(userCrendentials));

    // Clear out an existing authentication data
    this.sessionStorage.clear();

    // Call the Login API and return the results to the caller
    return this.http.post<UserInfoInterface>(url, body, {
      responseType: 'json',
      headers: {
        'Content-Type': 'application/json',
      },
    }).pipe(
      tap(
        (user: UserInfoInterface) => {
          // Save the access token and the username in session storage
          this.sessionStorage.authToken = user.accessToken;
          this.sessionStorage.username = username;
          this.sessionStorage.expiration = user.expirationDate;
          if (user.userData) {
            this.sessionStorage.officeId = user.userData.officeId;
            this.sessionStorage.dbEnvironment = user.userData.databaseEnv;
            if (user.userData.midInitial) {
              this.sessionStorage.userFullName = user.userData.firstName
                .concat(' ' + user.userData.midInitial)
                .concat(' ' + user.userData.lastName);
            } else {
              this.sessionStorage.userFullName = user.userData.firstName.concat(
                ' ' + user.userData.lastName
              );
            }
          } else {
            this.sessionStorage.dbEnvironment = 'UNKNOWN';
            this.sessionStorage.userFullName = username;
          }

          return user;
        },
        (err) =>
          // Return the error back to the caller
          err
      )
    );
  }

  // Clears out authentication data and sends user to login screen
  public logout(): void {
    // At logout go to the default page which is the login screen
    // This reloads the application which clears out memory and any outstanding objects
    window.location.href = '/';

    // this.router
    //   .navigate([''], { replaceUrl: true })
    //   .then((isSwitching: boolean) => {
    //     // If the user is offered a chance to stop logging out and acepts it,
    //     // do not clear the authentication data
    //     if (isSwitching) {
    //       // Clear out authentication data
    //       // this.clearSession();
    //     }
    //   });
  }

  // Clear any session information
  public clearSession(): void {
    this.sessionStorage.clear();
  }

  // Returns true is a token available in stored in session storage
  public hasToken(): boolean {
    return this.sessionStorage.authToken ? true : false;
  }

  public getUsername(): string {
    return this.sessionStorage.username;
  }

  public getUserFullName(): string {
    return this.sessionStorage.userFullName;
  }

  public getExpirationDate(): number {
    return this.sessionStorage.expiration;
  }

  public getToken(): string {
    return this.sessionStorage.authToken;
  }

  public getEnvironment(): string {
    return this.sessionStorage.dbEnvironment;
  }

  private _createUserCrendtials(
    username: string,
    password: string
  ): UserCredentialsInterface {
    return {
      user: username,
      password,
    };
  }

  public updateToken(): Observable<UserInfoInterface> {
    const url = `${environment.api.baseUrl}/api/auth/update-token`;
    // Call the Login API and return the results to the caller
    return this.http.get<UserInfoInterface>(url, {
      headers: {'Content-Type': 'application/json'}
    }).pipe(
      tap(
        (user: UserInfoInterface) => {
          // Save the access token and the username in session storage
          this.sessionStorage.authToken = user.accessToken;
          this.sessionStorage.expiration = user.expirationDate;
          return user;
        },
        (err: HttpErrorResponse) =>
          // Return the error back to the caller
          err
      )
    );
  }
}
