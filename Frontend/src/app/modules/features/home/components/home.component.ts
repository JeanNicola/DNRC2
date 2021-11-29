import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from 'src/app/modules/auth/services/auth.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-home-component',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  public isProduction = environment.production;
  public dbEnv: string;
  public showError = false;

  constructor(private authService: AuthService, private route: ActivatedRoute) {
    this.dbEnv = this.authService.getEnvironment();
    this.showError = this.route.snapshot.routeConfig.path === 'error';
  }

  onErrorButtonClick(): void {
    this.authService.logout();
  }
}
