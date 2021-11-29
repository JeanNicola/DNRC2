export interface UserInfoInterface {
  readonly roles: string[];
  readonly accessToken: string;
  readonly userData: UserData;
  readonly expirationDate: number;
}

export interface UserData {
  readonly firstName: string;
  readonly lastName: string;
  readonly midInitial: string;
  readonly officeId: number;
  readonly databaseEnv: string;
}
