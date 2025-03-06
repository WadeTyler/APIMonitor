import { Routes } from '@angular/router';
import {PageSignupComponent} from './pages/page-signup/page-signup.component';
import {PageDashboardComponent} from './pages/page-dashboard/page-dashboard.component';
import {AuthGuard} from './guards/AuthGuard/auth.guard';
import {PageLoginComponent} from './pages/page-login/page-login.component';
import {UnauthGuard} from './guards/UnauthGuard/unauth.guard';
import {PageDocsComponent} from './pages/page-docs/page-docs.component';

export const routes: Routes = [
  {
    path: "signup",
    component: PageSignupComponent,
    title: "Signup | API Monitor",
    canActivate: [UnauthGuard]
  },
  {
    path: "login",
    component: PageLoginComponent,
    title: "Login | API Monitor",
    canActivate: [UnauthGuard]
  },
  {
    path: "dashboard",
    component: PageDashboardComponent,
    title: "Dashboard | API Monitor",
    canActivate: [AuthGuard]
  },
  {
    path: "docs",
    component: PageDocsComponent,
    title: "Documentation | API Monitor",
  }
];
