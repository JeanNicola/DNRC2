import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedElementsComponent } from './shared-elements.component';

describe('SharedElementsComponent', () => {
  let component: SharedElementsComponent;
  let fixture: ComponentFixture<SharedElementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SharedElementsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SharedElementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
