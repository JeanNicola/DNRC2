import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectionsCategoriesComponent } from './objections-categories.component';

describe('ObjectionsCategoriesComponent', () => {
  let component: ObjectionsCategoriesComponent;
  let fixture: ComponentFixture<ObjectionsCategoriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ObjectionsCategoriesComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectionsCategoriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
