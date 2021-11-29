type NavRoutePath = {
  readonly path: string;
};

type NavRouteMenu = {
  readonly menu: NavMenu;
};

export type NavRouteDefinition = {
  readonly name: string;
} & (NavRoutePath | NavRouteMenu);

export class NavRoute {
  private activePath?: string;
  private _siblings?: NavRoute[];
  public readonly parent: NavMenu;
  public readonly name: string;
  public readonly path?: string;
  public readonly menu?: NavMenu;

  constructor(parent: NavMenu, name: string, path?: string, menu?: NavMenu) {
    this.parent = parent;
    this.name = name;
    this.path = path;
    this.menu = menu;
  }

  private get siblings(): NavRoute[] {
    if (this.menu) {
      return [];
    }
    if (this._siblings) {
      return this._siblings;
    }

    return (this._siblings = this.parent
      .flatten()
      .filter((route) => route !== this && route.path.includes(this.path)));
  }

  get active(): boolean {
    if (this.menu) {
      return this.menu.active;
    }
    if (!this.activePath?.includes(this.path)) {
      return false;
    }

    return !this.siblings.some((sibling) =>
      this.activePath?.includes(sibling.path)
    );
  }

  public setActivePath(url: string): void {
    if (this.menu) {
      this.menu.setUrl(url);
    }

    // It is important here that we assume that the only
    // segments in the URL that intercut between the route's
    // path segments are going to be numeric ids.
    this.activePath = url.replace(/\/[0-9]+/g, '');
  }
}

export class NavMenu {
  public readonly routes: NavRoute[];

  constructor(routes: NavRouteDefinition[]) {
    this.routes = (routes as Omit<NavRoute, 'parent'>[]).map(
      ({ name, path, menu }) => new NavRoute(this, name, path, menu)
    );
  }

  get active(): boolean {
    return this.routes.some((route) => route.active);
  }

  public flatten(): NavRoute[] {
    return this.routes.reduce(
      (acc, route) =>
        route.menu ? [...acc, ...route.menu.flatten()] : [...acc, route],
      []
    );
  }

  public setUrl(url: string): void {
    this.routes.forEach((route) => route.setActivePath(url));
  }
}
