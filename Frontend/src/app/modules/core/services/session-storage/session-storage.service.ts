import { Injectable } from '@angular/core';
import { SessionStorageKeyEnums } from '../../enums/session-key.enums';

@Injectable()
export class SessionStorageService {
  public constructor() {}

  public get authToken(): string | null {
    return sessionStorage.getItem(SessionStorageKeyEnums.TOKEN)
      ? sessionStorage.getItem(SessionStorageKeyEnums.TOKEN)
      : null;
  }

  public set authToken(authToken: string) {
    sessionStorage.setItem(SessionStorageKeyEnums.TOKEN, authToken);
  }

  public get username(): string | null {
    return sessionStorage.getItem(SessionStorageKeyEnums.USERNAME)
      ? sessionStorage.getItem(SessionStorageKeyEnums.USERNAME)
      : null;
  }

  public set username(username: string) {
    sessionStorage.setItem(SessionStorageKeyEnums.USERNAME, username);
  }

  public get userFullName(): string | null {
    return sessionStorage.getItem(SessionStorageKeyEnums.USERFULLNAME)
      ? sessionStorage.getItem(SessionStorageKeyEnums.USERFULLNAME)
      : null;
  }

  public set userFullName(userFullName: string) {
    sessionStorage.setItem(SessionStorageKeyEnums.USERFULLNAME, userFullName);
  }

  public get officeId(): number | null {
    return sessionStorage.getItem(SessionStorageKeyEnums.OFFICEID)
      ? +sessionStorage.getItem(SessionStorageKeyEnums.OFFICEID)
      : null;
  }

  public set officeId(officeId: number) {
    sessionStorage.setItem(
      SessionStorageKeyEnums.OFFICEID,
      officeId.toString()
    );
  }

  public set expiration(expiration: number) {
    sessionStorage.setItem(
      SessionStorageKeyEnums.EXPIRATION,
      expiration.toString()
    );
  }

  public get expiration(): number | null {
    return sessionStorage.getItem(SessionStorageKeyEnums.EXPIRATION)
      ? Date.parse(sessionStorage.getItem(SessionStorageKeyEnums.EXPIRATION))
      : null;
  }

  public get dbEnvironment(): string | null {
    return sessionStorage.getItem(SessionStorageKeyEnums.ENVIRONMENT)
      ? sessionStorage.getItem(SessionStorageKeyEnums.ENVIRONMENT)
      : null;
  }

  public set dbEnvironment(env: string) {
    sessionStorage.setItem(SessionStorageKeyEnums.ENVIRONMENT, env);
  }

  public remove(key: string): void {
    sessionStorage.removeItem(key);
  }

  public clear(): void {
    sessionStorage.clear();
  }
}
