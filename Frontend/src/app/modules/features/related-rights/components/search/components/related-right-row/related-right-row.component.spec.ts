import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedRightRowComponent } from './related-right-row.component';

describe('RelatedRightRowComponent', () => {
  let component: RelatedRightRowComponent;
  let fixture: ComponentFixture<RelatedRightRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RelatedRightRowComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedRightRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
