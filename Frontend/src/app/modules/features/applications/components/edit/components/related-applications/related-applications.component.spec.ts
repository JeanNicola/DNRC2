import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedApplicationsComponent } from './related-applications.component';

describe('RelatedApplicationsComponent', () => {
  let component: RelatedApplicationsComponent;
  let fixture: ComponentFixture<RelatedApplicationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RelatedApplicationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedApplicationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
