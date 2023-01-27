import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {LoginService} from "../services/login.service";
import {AlertService} from "../services/alert.service";
import {first} from "rxjs";
import {UserService} from "../services/user.service";
import {User} from "../model/User";
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  // @ts-ignore
  registerForm: FormGroup;
  loading = false;
  submitted = false;


  constructor(
    private titleService:Title,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private loginService: LoginService,
    private alertService: AlertService
  ) {
    if (this.loginService.currentUserValue.token != null) {
      this.router.navigate(['/items']);
    }
    this.titleService.setTitle("Register");
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', Validators.required, Validators.email]
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    this.alertService.clear();

    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.userService.register(new User(0, this.f['username'].value, this.f['password'].value,
      this.f['firstName'].value, this.f['lastName'].value, this.f['email'].value, "token")).pipe(first())
      .subscribe({
        next: data => {
          this.alertService.success('Registration successful', true);
          this.router.navigate(['/'])
        },
        error: error => {
          this.loading = false;
          if (error.status == 401)
            this.alertService.error("Nie prawidłowe hasło lub nazwa użytkownika!");
          else
            this.alertService.error("Nieznany błąd logowania!");
        }
      })

  }

}
