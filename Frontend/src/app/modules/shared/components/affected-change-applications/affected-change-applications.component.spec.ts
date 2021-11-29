import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AffectedChangeApplicationsComponent } from './affected-change-applications.component';

describe('AffectedChangeApplicationsComponent', () => {
  let component: AffectedChangeApplicationsComponent;
  let fixture: ComponentFixture<AffectedChangeApplicationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AffectedChangeApplicationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AffectedChangeApplicationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
