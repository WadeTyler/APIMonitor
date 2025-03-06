import { Component } from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {LoadingSmallComponent} from '../../components/lib/loaders/loading-small/loading-small.component';
import {LoadingMediumComponent} from '../../components/lib/loaders/loading-medium/loading-medium.component';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'page-signup',
  imports: [
    ReactiveFormsModule,
    LoadingMediumComponent,
    RouterLink
  ],
  templateUrl: './page-signup.component.html',
  styleUrl: './page-signup.component.css'
})
export class PageSignupComponent {

  constructor(protected userService: UserService) {

  }

  signupForm: FormGroup = new FormGroup({
    email: new FormControl("", [
      Validators.required,
      Validators.max(100)
    ]),
    firstName: new FormControl("", [
      Validators.required,
      Validators.max(50)
    ]),
    lastName: new FormControl("", [
      Validators.required,
      Validators.max(50)
    ]),
    password: new FormControl("", [
      Validators.required,
      Validators.max(50)
    ])
  });

  handleSubmitSignupForm(): void {
    this.userService.signup(this.signupForm.getRawValue());
  }
}
