import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnersApplicationTableComponent } from './owners-application-table.component';

describe('OwnersApplicationTableComponent', () => {
  let component: OwnersApplicationTableComponent;
  let fixture: ComponentFixture<OwnersApplicationTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OwnersApplicationTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnersApplicationTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
