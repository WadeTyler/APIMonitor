import { Component } from '@angular/core';
import {LoadingMediumComponent} from '../../components/lib/loaders/loading-medium/loading-medium.component';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'page-login',
  imports: [
    LoadingMediumComponent,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './page-login.component.html',
  styleUrl: './page-login.component.css'
})
export class PageLoginComponent {

  constructor(protected userService: UserService) {
  }

  loginForm: FormGroup = new FormGroup({
    email: new FormControl("", [
      Validators.required,
      Validators.max(100)
    ]),
    password: new FormControl("", [
      Validators.required,
      Validators.max(50)
    ])
  });

  handleSubmitLoginForm(): void {
    this.userService.login(this.loginForm.getRawValue());
  }

}
