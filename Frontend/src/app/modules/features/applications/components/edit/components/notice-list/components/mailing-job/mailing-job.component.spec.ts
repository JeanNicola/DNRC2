import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MailingJobComponent } from './mailing-job.component';

describe('MailingJobComponent', () => {
  let component: MailingJobComponent;
  let fixture: ComponentFixture<MailingJobComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MailingJobComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MailingJobComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
