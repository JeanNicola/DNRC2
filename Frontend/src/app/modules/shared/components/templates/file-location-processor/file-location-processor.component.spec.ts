import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileLocationProcessorComponent } from './file-location-processor.component';

describe('FileLocationProcessorComponent', () => {
  let component: FileLocationProcessorComponent;
  let fixture: ComponentFixture<FileLocationProcessorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FileLocationProcessorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileLocationProcessorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
