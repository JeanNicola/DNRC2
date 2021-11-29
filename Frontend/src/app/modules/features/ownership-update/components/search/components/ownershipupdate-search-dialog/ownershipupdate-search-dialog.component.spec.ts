import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnershipupdateSearchDialogComponent } from './ownershipupdate-search-dialog.component';

describe('OwnershipupdateSearchDialogComponent', () => {
  let component: OwnershipupdateSearchDialogComponent;
  let fixture: ComponentFixture<OwnershipupdateSearchDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OwnershipupdateSearchDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnershipupdateSearchDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
