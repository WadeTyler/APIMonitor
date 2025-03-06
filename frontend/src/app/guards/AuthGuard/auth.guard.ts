import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UserService} from '../../services/user.service';

/*
* This guard is used on routes that the user should not be able to view if they are not signed in.
* Ex: /dashboard
* */

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private userService: UserService, private router: Router) {
  }

  async canActivate(): Promise<boolean> {
    try {
      const isAuthenticated = await this.userService.isAuthenticated();
      if (isAuthenticated) return true;

      this.router.navigate(['/login']);
      return false;
    } catch (error) {
      this.router.navigate(['/login']);
      return false;
    }
  }

}
