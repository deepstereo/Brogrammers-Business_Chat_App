import { Routes} from '@angular/router';
import { LoginComponent } from './app/login/login.component';
import { SignupComponent } from './app/signup/signup.component';
import { ChatAppComponent } from './app/chat-app/chat-app.component';

export const appRoutes: Routes = [
    { path: 'login', component: LoginComponent},    
    { path: 'signup', component: SignupComponent},
    { path: 'chat', component: ChatAppComponent},
    { path: '', redirectTo: '/login', pathMatch: 'full'},
];