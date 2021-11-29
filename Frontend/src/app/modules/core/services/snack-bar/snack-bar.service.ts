import { Injectable } from '@angular/core';
import {
  MatSnackBar,
  MatSnackBarConfig,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { take } from 'rxjs/operators';

/*
 * SnackBarService
 *
 * Wrapper for Anagular Material Snackbar component.
 * This wrapper allows multiple messages to be queued and displayed in order of receipt.
 * The original Snackbar will only show the most recent message which may result in dropping messages if they
 * arrive too fast.
 */
@Injectable({
  providedIn: 'root',
})
export class SnackBarService {
  private messageQueue: SnackBarServiceInterface[] = [];
  private isProcessing = false;

  constructor(private snackBar: MatSnackBar) {}

  /*
   * Opens the snakbar using the message and paramters passed in. If a snackbar is already open, this
   * will queue the new message up and display it once the open snackbar closes.
   * '
   * Input paremters:
   * - message: string to be displayed
   * - action: what text to display on teh snackbar action button
   * - snackDuration: length of time in milliseconds to display the snackbar (defaults to 5 seconds)
   * - hPosition: horizontal position on the screen (defaults to center)
   * - vPosition: vertical position on the screen (defaults to bottom)
   *
   * Additional styling is provided in the styles.css using the class 'snackbar' (applied via panelClass)
   */
  public open(
    message: string,
    action?: string,
    snackDuration: number = 10000,
    hPosition: MatSnackBarHorizontalPosition = 'center',
    vPosition: MatSnackBarVerticalPosition = 'bottom',
    panelClass: string[] = []
  ): void {
    const configs: MatSnackBarConfig = {
      duration: snackDuration,
      horizontalPosition: hPosition,
      verticalPosition: vPosition,
      panelClass: ['snackbar', ...panelClass],
    };

    // Queue the message
    this._putMessage(message, action, configs);

    // If the snackbar is not currently being displayed, then display it, otherwise leave and the queue will be processed
    if (!this.isProcessing) {
      this._displaySnackbar();
    }
  }

  /*
   * Displays the actual snackbar component
   *
   * Get the next message in the queue. If no message is available, resets the isProcessing flag and exits
   * If a messsage exists,
   * - set the isProcessing flag to true then open the snackbar
   * - once the snackbar closes, call this function again to process any additional messages that may have come it
   */
  private _displaySnackbar(): void {
    const nextMessage: SnackBarServiceInterface = this._getNextMessage();

    if (!nextMessage) {
      this.isProcessing = false;
      return;
    }

    // Set the isProcessing flag to true to prevent additional calls to this service from displaying their message
    // until the service is ready
    this.isProcessing = true;
    this.snackBar
      .open(
        nextMessage.message,
        nextMessage.action ? nextMessage.action : null,
        nextMessage.config
      )
      .afterDismissed()
      .pipe(take(1))
      .subscribe(() => {
        this._displaySnackbar();
      });

    if (document.querySelector('.snackbar')?.parentElement) {
      document
        .querySelector('.snackbar')
        .parentElement.classList.add('snackbar-container');
    }
  }

  // Returns the next message from the message queue
  private _getNextMessage(): SnackBarServiceInterface | undefined {
    return this.messageQueue.length ? this.messageQueue.shift() : undefined;
  }

  // Puts the message into the message queue. The message includes the actual string
  // message with the snackbar action and any config parameters
  private _putMessage(
    msg: string,
    action: string,
    cfg: MatSnackBarConfig
  ): void {
    const newMsg: SnackBarServiceInterface = {
      message: msg,
      action,
      config: cfg,
    };
    this.messageQueue.push(newMsg);
  }
}

export interface SnackBarServiceInterface {
  message: string;
  action: string;
  config: MatSnackBarConfig;
}
