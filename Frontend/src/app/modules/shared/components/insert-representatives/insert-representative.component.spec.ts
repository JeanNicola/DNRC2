import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertRepresentativeComponent } from './insert-representative.component';

describe('InsertRepresentativeComponent', () => {
  let component: InsertRepresentativeComponent;
  let fixture: ComponentFixture<InsertRepresentativeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InsertRepresentativeComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertRepresentativeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
