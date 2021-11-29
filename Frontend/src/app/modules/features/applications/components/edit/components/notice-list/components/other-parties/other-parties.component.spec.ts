import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtherPartiesComponent } from './other-parties.component';

describe('OtherPartiesComponent', () => {
  let component: OtherPartiesComponent;
  let fixture: ComponentFixture<OtherPartiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OtherPartiesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OtherPartiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
