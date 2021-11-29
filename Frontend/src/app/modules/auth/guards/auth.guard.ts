import { CanActivate } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Injectable } from '@angular/core';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService) {}

  public canActivate(): boolean {
    if (this.authService.hasToken()) {
      return true;
    }
    this.authService.logout();
  }
}
