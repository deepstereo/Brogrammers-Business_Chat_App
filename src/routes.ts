import { Routes} from '@angular/router';
import { RegistrationComponent } from './app/registration/registration.component';
import { ChatComponent } from './app/chat/chat.component';

export const appRoutes: Routes = [
    { path: '', component: RegistrationComponent},    
    { path: 'chat', component: ChatComponent},
    { path: '', redirectTo: '/login', pathMatch: 'full'},
];