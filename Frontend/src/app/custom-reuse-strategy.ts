import { BaseRouteReuseStrategy } from '@angular/router';

export class CustomReuseStrategy extends BaseRouteReuseStrategy {
  // this way, we always reload the components when the url changes
  // this is useful for the navigating to a different record within the same screen
  shouldReuseRoute(): boolean {
    return false;
  }
}
