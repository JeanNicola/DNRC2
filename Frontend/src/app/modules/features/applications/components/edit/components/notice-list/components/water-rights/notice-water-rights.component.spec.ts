import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeWaterRightsComponent } from './notice-water-rights.component';

describe('NoticeWaterRightsComponent', () => {
  let component: NoticeWaterRightsComponent;
  let fixture: ComponentFixture<NoticeWaterRightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NoticeWaterRightsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeWaterRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
