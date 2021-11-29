import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { filter, take } from 'rxjs/operators';
import { AuthService } from 'src/app/modules/auth/services/auth.service';
import { NavMenu } from './nav-menu';

@Component({
  selector: '[app-nav]',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss'],
})
export class NavComponent implements OnInit {
  public dbEnvironment: string = null;
  public envColor: string = null;

  // The nav bar displays the environment if not in production
  private environments = [
    {
      name: 'TRIDEV',
      color: 'darkviolet',
      description: 'Trident Development Environment',
    },
    {
      name: 'DNRTRI',
      color: 'cornflowerblue',
      description: 'Trident QA/UAT Environment',
    },
    {
      name: 'DNRDEV',
      color: 'goldenrod',
      description: 'DNRC Development Environment',
    },
    {
      name: 'DNRTST',
      color: 'orangered',
      description: 'DNRC Test Environment',
    },
    {
      name: 'DNRPRD',
      color: '#0d2063',
      description: 'WRIS Production',
    },
  ];

  public menu = new NavMenu([
    {
      name: 'Applications',
      path: 'applications',
    },
    {
      name: 'Water Rights',
      menu: new NavMenu([
        { name: 'Water Rights', path: 'water-rights' },
        {
          name: 'Water Right Version Details',
          path: 'water-rights/versions',
        },
        {
          name: 'Purpose/Place of Use (POU) Details',
          path: 'water-rights/versions/purposes',
        },
      ]),
    },
    {
      name: 'Contacts',
      path: 'contacts',
    },
    {
      name: 'Ownership Updates',
      path: 'ownership-updates',
    },
    {
      name: 'Adj/Water Court',
      menu: new NavMenu([
        { name: 'Cases/Hearings', path: 'water-court/case-hearings' },
        { name: 'Decrees', path: 'water-court/decrees' },
        {
          name: 'Enforcement Projects',
          path: 'water-court/enforcement-projects',
        },
        { name: 'Examination Details', path: 'water-court/examinations' },
        {
          name: 'Objections/Counter Objections',
          path: 'water-court/objections',
        },
      ]),
    },
    {
      name: 'More...',
      menu: new NavMenu([
        { name: 'Basins/Compacts', path: 'basins-compacts' },
        { name: 'Cases/Hearings', path: 'water-court/case-hearings' },
        { name: 'Closures', path: 'closures' },
        {
          name: 'Code Table Updates...',
          menu: new NavMenu([
            {
              name: 'Case Assignment Types',
              path: 'code-tables/case-assignment-types',
            },
            { name: 'Case Status', path: 'code-tables/case-status' },
            { name: 'Case Types', path: 'code-tables/case-types' },
            { name: 'City / Zip Codes', path: 'code-tables/city-zipcodes' },
            { name: 'Event Types', path: 'code-tables/event-types' },
            {
              name: 'Subdivision Codes',
              path: 'code-tables/subdivision-codes',
            },
          ]),
        },
        { name: 'Compacts', path: 'compacts' },
        { name: 'Consolidate Contact Record', path: 'consolidate-contact' },
        { name: 'Formatted Remarks', path: 'formatted-remarks' },
        { name: 'Mailing Jobs', path: 'mailing-jobs' },
        {
          name: 'Objections/Counter Objections',
          path: 'water-court/objections',
        },
        { name: 'Related Rights', path: 'related-rights' },
        { name: 'Water Reservations', path: 'water-reservations' },
      ]),
    },
  ]);

  constructor(
    private router: Router,
    private authService: AuthService,
    private titleService: Title
  ) {}

  public ngOnInit(): void {
    // Get the current route
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .pipe(take(1))
      .subscribe((event: NavigationEnd) => {
        this.menu.setUrl(event.url);
        this.setTitle();
      });

    this.menu.setUrl(this.router.url);
    this.setTitle();

    // Get the database environment the user is accessing
    const db = this.authService.getEnvironment();
    const env = this.environments.find(({ name }) => name === db);
    this.envColor = env?.color ?? 'red';
    this.dbEnvironment = `${db} - ${
      env?.description ?? 'Unknown Database Environment'
    }`;
  }

  private setTitle(): void {
    const route = this.menu.flatten().find((rt) => rt.active);
    if (route) {
      this.titleService.setTitle(`WRIS - ${route.name ?? 'NOT FOUND'}`);
    }
  }
}
