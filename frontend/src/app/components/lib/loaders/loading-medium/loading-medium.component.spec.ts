import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingMediumComponent } from './loading-medium.component';

describe('LoadingMediumComponent', () => {
  let component: LoadingMediumComponent;
  let fixture: ComponentFixture<LoadingMediumComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingMediumComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoadingMediumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
