import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-trident-forms-main',
  templateUrl: './trident-forms-main.component.html',
  styleUrls: ['./trident-forms-main.component.scss'],
})
export class TridentFormsMainComponent implements OnInit {
  public constructor(
    private titleService: Title,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.titleService.setTitle('WRIS - Home');
  }

  public ngOnInit(): void {
    if (!this.route.snapshot.data.canLoad) {
      void this.router.navigate(['/error']);
    }
  }
}
