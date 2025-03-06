import {Injectable, NgZone} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UserService} from '../../services/user.service';

/*
* This guard is used on routes that the user does not need to visit if they are already logged in.
* Ex: /login and /signup
*
* */


@Injectable({
  providedIn: 'root'
})
export class UnauthGuard implements CanActivate {

  constructor(private userService: UserService, private router: Router, private ngZone: NgZone) {
  }

  async canActivate(): Promise<boolean> {
    try {
      const isAuthenticated = await this.userService.isAuthenticated();
      if (!isAuthenticated) return true;

      this.router.navigate(['/dashboard']);
      return false;
    } catch (error) {
      this.router.navigate(['/dashboard']);
      return false;
    }


  }

}
