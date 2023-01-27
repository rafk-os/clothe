import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AlertService} from "../services/alert.service";
import {LoginService} from "../services/login.service";
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup ;
  loading = false;
  submitted = false;
  returnUrl: string = "/" ;

  constructor(
    private titleService:Title,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private loginService: LoginService,
    private alertService: AlertService
  ) {



    if (loginService.currentUserValue.token != null)
      this.router.navigate(['items']);

    this.titleService.setTitle("Login");

  }

  ngOnInit() {

    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/items';
  }


  get f() { return this.loginForm.controls; }

  onSubmit() {
    this.submitted = true;

   // this.alertService.clear();

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.loginService.login(this.f['username'].value, this.f['password'].value)
      .pipe()
      .subscribe({
        next: data => {
          this.router.navigate([this.returnUrl])
        },
        error: error => {
          this.loading = false;
          if(error.status == 401)
            this.alertService.error("Nie prawidłowe hasło lub nazwa użytkownika!");
          else
            this.alertService.error("Nieznany błąd logowania!");
        }})

  }




}
