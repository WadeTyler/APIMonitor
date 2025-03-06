import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import User from '../../types/User';
import APIResponse from '../../types/APIResponse';
import {Router} from '@angular/router';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl: string = environment.apiUrl;


  private authUserSubject = new BehaviorSubject<User | undefined>(undefined);
  authUser$ = this.authUserSubject.asObservable();

  constructor(private router: Router) {
  }

  setAuthUser(newValue: User | undefined) {
    this.authUserSubject.next(newValue);
  }

  get authUser(): User | undefined {
    return this.authUserSubject.value;
  }

  async isAuthenticated(): Promise<boolean> {

    if (this.authUser) return true;

    try {
      const response = await fetch (this.apiUrl + "/user", {
        method: "GET",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include"
      });

      const data: APIResponse<User> = await response.json();
      if (!response.ok || !data.success) {
        throw new Error(data.message);
      }

      this.setAuthUser(data.data);
      console.log(data);
      return true;
    } catch (error: any) {
      console.log((error as Error).message || "Failed to authenticate.");
    }
    return false;
  }


  /// SIGNUP SECTION

  signupError: string = "";
  isSigningUp: boolean = false;

  async signup(user: User): Promise<void> {
    if (this.isSigningUp) return;

    this.isSigningUp = true;
    this.signupError = "";

    try {
      const response = await fetch(this.apiUrl + '/user/signup', {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(user)
      });

      const data: APIResponse<User> = await response.json();

      if (!response.ok || !data.success) {
        throw new Error(data.message || "Signup failed");
      }

      this.setAuthUser(data.data);
      this.router.navigate(["/dashboard"]);
    } catch (error: any) {
      console.log(error);
      this.signupError = error.message || "An error occurred during signup";
    } finally {
      this.isSigningUp = false;
    }
  }

  /// LOGIN SECTION

  loginError: string = "";
  isLoggingIn: boolean = false;
  async login(user: User): Promise<void> {
    this.loginError = "";
    this.isLoggingIn = false;

    try {
      const response = await fetch(this.apiUrl + "/user/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(user),
        credentials: "include"
      });

      const data: APIResponse<User> = await response.json();

      // Check for error
      if (!response.ok || !data.success) {
        throw new Error(data.message);
      }

      // User Logged in
      this.setAuthUser(data.data);
      // Navigate to dashboard
      this.router.navigate(["dashboard"]);
    } catch (error) {
      this.loginError = (error as Error).message || "Failed to login";
    } finally {
      this.isLoggingIn = false;
    }
  }

  /// LOGOUT SECTION

  logoutError: string = "";
  isLoggingOut: boolean = false;
  async logout(): Promise<void> {
    this.logoutError = "";
    this.isLoggingOut = true;

    console.log("Logging out")

    try {
      const response = await fetch(this.apiUrl + "/user/logout", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include"
      });

      const data: APIResponse<any> = await response.json();
      if (!response.ok || !data.success) {
        throw new Error(data.message);
      }

      // remove auth user
      this.setAuthUser(undefined);
      // reload page
      location.reload();
    } catch (e) {
      console.error((e as Error).message || "Failed to logout");
    } finally {
      this.isLoggingOut = false;
    }
  }

}
