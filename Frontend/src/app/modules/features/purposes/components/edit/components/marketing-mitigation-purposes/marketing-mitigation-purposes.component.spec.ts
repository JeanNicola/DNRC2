import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MarketingMitigationPurposesComponent } from './marketing-mitigation-purposes.component';

describe('MarketingMitigationPurposesComponent', () => {
  let component: MarketingMitigationPurposesComponent;
  let fixture: ComponentFixture<MarketingMitigationPurposesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MarketingMitigationPurposesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketingMitigationPurposesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
