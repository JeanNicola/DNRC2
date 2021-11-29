import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WrApplsComponent } from './wr-appls.component';

describe('WrApplsComponent', () => {
  let component: WrApplsComponent;
  let fixture: ComponentFixture<WrApplsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WrApplsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WrApplsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
