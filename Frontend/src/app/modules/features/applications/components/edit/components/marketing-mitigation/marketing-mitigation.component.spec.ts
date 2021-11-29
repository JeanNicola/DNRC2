import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MarketingMitigationComponent } from './marketing-mitigation.component';

describe('MarketingMitigationComponent', () => {
  let component: MarketingMitigationComponent;
  let fixture: ComponentFixture<MarketingMitigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MarketingMitigationComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketingMitigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
