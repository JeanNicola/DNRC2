import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepresentativesTableComponent } from './representatives-table.component';

describe('RepresentativesTableComponent', () => {
  let component: RepresentativesTableComponent;
  let fixture: ComponentFixture<RepresentativesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RepresentativesTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepresentativesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
