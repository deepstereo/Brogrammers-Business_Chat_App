import { Routes} from '@angular/router';
import { ContentComponent } from './app/content/content.component';
import { ChatComponent } from './app/chat/chat.component';

export const appRoutes: Routes = [
    { path: '', redirectTo: '/home', pathMatch: 'full'},  
    { path: 'home', component: ContentComponent},
    { path: 'chat', component: ChatComponent}
];