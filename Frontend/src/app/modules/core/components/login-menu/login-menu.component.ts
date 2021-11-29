import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/modules/auth/services/auth.service';

@Component({
  selector: '[app-login-menu]',
  templateUrl: './login-menu.component.html',
  styleUrls: ['./login-menu.component.scss'],
})
export class LoginMenuComponent implements OnInit {
  public userFullName: string;

  constructor(private authService: AuthService) {}

  public ngOnInit(): void {
    this.userFullName = this.authService.getUserFullName();
  }

  public logout(): void {
    this.authService.logout();
  }

  public notImplemented(): void {
    alert('Not implemented yet');
  }
}
