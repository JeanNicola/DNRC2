/* eslint-disable max-len */
import { MatDialog } from '@angular/material/dialog';
import { EditMessageComponent } from '../components/dialogs/edit-message/edit-message.component';

export class WaterRightsPrivileges {
  /* This function uses the decree booleans to determine if the warning message needs to be displayed.
   * The logic flows like this:
   *
   *    is        isDecreed
   * Editable    True    False
   *   True      allow   allow
   *   False     warn    allow
   *
   * If the warning is accepted or the right is not decreed, then the passed-in function
   * is executed. Since most functions rely on "this" data, "this" will need to be bound
   * to the passed-in function:
   *
   * Input parameters:
   * - isDecreed: boolean as to whether or nto the waer right is decreed
   * - isEditableIfDecreed:  boolean saying the user has rights to edit the water right *if* it is decreed
   * - dialog: instance of the MatDialog injected into the calling component
   * - f: a function to call if the user wants to proceed. This function WILL need to have "this" bound to
   *      it before calling and any requried parameters. Example:
   *  checkDecree(
   *    this.isDecreed,
   *    this.isEditableIfDecreed,
   *    this.dialog,
   *    this._runStandards.bind(this, param)
   *  );
   *
   */
  public static checkDecree(
    isDecreed: boolean,
    isEditableIfDecreed: boolean,
    dialog: MatDialog,
    f: () => any
  ): void {
    if (isDecreed) {
      const warningMessageDialog = dialog.open(EditMessageComponent, {
        width: '500px',
        data: {
          title: 'Edit Decreed Record',
          message:
            'WARNING - You are modifying a DECREED Water Right. Do you want to continue?',
        },
      });

      warningMessageDialog.afterClosed().subscribe((r) => {
        if (r === 'continue') {
          f();
        }
      });
    } else {
      f();
    }
  }

  /*
   * This method extends the previous method by adding the versionNumber. If the version is
   * the first version, then perform the regular decree check; otherwise perform the requested function
   */
  public static checkVersionDecree(
    versionNumber: number,
    isDecreed: boolean,
    isEditableIfDecreed: boolean,
    dialog: MatDialog,
    f: () => any
  ): void {
    if (versionNumber === 1) {
      WaterRightsPrivileges.checkDecree(
        isDecreed,
        isEditableIfDecreed,
        dialog,
        f
      );
    } else {
      f();
    }
  }
}
