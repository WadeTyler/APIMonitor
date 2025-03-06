import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingSmallComponent } from './loading-small.component';

describe('LoadingSmallComponent', () => {
  let component: LoadingSmallComponent;
  let fixture: ComponentFixture<LoadingSmallComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingSmallComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoadingSmallComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
