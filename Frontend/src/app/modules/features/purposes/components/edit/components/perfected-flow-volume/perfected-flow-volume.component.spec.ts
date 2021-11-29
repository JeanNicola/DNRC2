import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerfectedFlowVolumeComponent } from './perfected-flow-volume.component';

describe('PerfectedFlowVolumeComponent', () => {
  let component: PerfectedFlowVolumeComponent;
  let fixture: ComponentFixture<PerfectedFlowVolumeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerfectedFlowVolumeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PerfectedFlowVolumeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
