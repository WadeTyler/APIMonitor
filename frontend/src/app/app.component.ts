import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {UserService} from './services/user.service';
import {NavbarComponent} from './components/navbar/navbar/navbar.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';

  constructor(private userService: UserService) {
    this.userService.isAuthenticated();
  }

}
