import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AngularFireModule } from 'angularfire2';
import { AngularFireDatabaseModule } from 'angularfire2/database';
import { AngularFireAuthModule } from 'angularfire2/auth';

import { environment } from '../environments/environment';

import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms'; 

import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { ChatSideNavComponent } from './chat-side-nav/chat-side-nav.component';
import { ChatListComponent } from './chat-list/chat-list.component';
import { ChatItemComponent } from './chat-item/chat-item.component';
import { ChatHeaderComponent } from './chat-header/chat-header.component';
import { ChatFeedComponent } from './chat-feed/chat-feed.component';
import { ChatFormComponent } from './chat-form/chat-form.component';
import { ChatMessageComponent } from './chat-message/chat-message.component';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { ChatAppComponent } from './chat-app/chat-app.component';

import { AuthService } from './services/auth.service';
import { ChatService } from './services/chat.service';

import { appRoutes } from '../routes';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    ChatSideNavComponent,
    ChatListComponent,
    ChatItemComponent,
    ChatHeaderComponent,
    ChatFeedComponent,
    ChatFormComponent,
    ChatMessageComponent,
    LoginComponent,
    SignupComponent,
    ChatAppComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(appRoutes),
    FormsModule,
    AngularFireModule,
    AngularFireDatabaseModule,
    AngularFireAuthModule,
    AngularFireModule.initializeApp(environment.firebase)
  ],
  providers: [AuthService, ChatService],
  bootstrap: [AppComponent]
})
export class AppModule { }
