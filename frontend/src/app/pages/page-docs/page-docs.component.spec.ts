import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageDocsComponent } from './page-docs.component';

describe('PageDocsComponent', () => {
  let component: PageDocsComponent;
  let fixture: ComponentFixture<PageDocsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PageDocsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PageDocsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
