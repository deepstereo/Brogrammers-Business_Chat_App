import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatSideNavComponent } from './chat-side-nav.component';

describe('ChatSideNavComponent', () => {
  let component: ChatSideNavComponent;
  let fixture: ComponentFixture<ChatSideNavComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChatSideNavComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChatSideNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
