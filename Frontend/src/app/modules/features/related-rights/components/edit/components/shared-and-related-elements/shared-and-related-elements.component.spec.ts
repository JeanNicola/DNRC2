import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedAndRelatedElementsComponent } from './shared-and-related-elements.component';

describe('SharedAndRelatedElementsComponent', () => {
  let component: SharedAndRelatedElementsComponent;
  let fixture: ComponentFixture<SharedAndRelatedElementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SharedAndRelatedElementsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SharedAndRelatedElementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
