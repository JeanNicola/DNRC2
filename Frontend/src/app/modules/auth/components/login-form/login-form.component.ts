import {
  FormGroup,
  FormBuilder,
  Validators,
  FormGroupDirective,
} from '@angular/forms';
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { MatDialog } from '@angular/material/dialog';
import { HttpErrorResponse } from '@angular/common/http';
import { take } from 'rxjs/operators';

/*
 * LoginFormComponent
 * Manages the login screen. Accepts the userid and password then calls the AuthService
 * to log the user in. If the login is successful the user is sent to the main screen.
 * Otherwise an erro message is sshown.
 */
@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss'],
})
export class LoginFormComponent implements OnInit {
  public loginForm: FormGroup;

  @ViewChild('loginDirective') private formGroupDirective: FormGroupDirective;
  @ViewChild('username', { static: true }) public username: ElementRef;
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBarService: SnackBarService,
    private dialog: MatDialog
  ) {}

  public ngOnInit(): void {
    // Clear out any previous login credentials / data
    this.authService.clearSession();

    // Close any open dialogs
    this.dialog.closeAll();

    // Initialize the login form
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });

    this.username.nativeElement.focus();
  }

  public login(form: FormGroup): void {
    const username: string = form.controls.username.value;
    const password: string = form.controls.password.value;

    // Call the login service. If successful, go to the main screen.
    this.authService
      .login(username, password)
      .pipe(take(1))
      .subscribe(
        () => {
          void this.router.navigate(['wris'], { replaceUrl: false }); // Do not replace the URL so browser back returns to login
        },
        (err: HttpErrorResponse) => {
          let errMsg = 'Login failed.\n';
          if (err.status === 0) {
            errMsg =
              errMsg +
              'Cannot contact backend services.\nMore information: ' +
              err.message;
          } else {
            errMsg =
              errMsg +
              (err.error.userMessage ? err.error.userMessage : 'Unknown error');
          }
          if (
            err.status === 401 &&
            err.error.exceptionName === 'HelpDeskNeededException'
          ) {
            this.snackBarService.open(errMsg, 'Dismiss', 0);
          } else {
            this.snackBarService.open(errMsg);
          }

          this.username.nativeElement.focus();
        }
      );
  }

  // Allows the Rest button to clear the form back to it's initial state
  public resetForm(): void {
    // This is necessary to reset the Material validations
    if (this.formGroupDirective) {
      this.formGroupDirective.resetForm();
    }

    this.loginForm.reset({});
    this.username.nativeElement.focus();
  }
}
