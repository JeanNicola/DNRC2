import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectionsForVersionsComponent } from './objections-for-versions.component';

describe('ObjectionsForVersionsComponent', () => {
  let component: ObjectionsForVersionsComponent;
  let fixture: ComponentFixture<ObjectionsForVersionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ObjectionsForVersionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectionsForVersionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
