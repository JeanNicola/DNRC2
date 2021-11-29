import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectionsSummaryComponent } from './objections-summary.component';

describe('ObjectionsSummaryComponent', () => {
  let component: ObjectionsSummaryComponent;
  let fixture: ComponentFixture<ObjectionsSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ObjectionsSummaryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectionsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
