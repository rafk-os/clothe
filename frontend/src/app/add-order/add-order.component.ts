import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Title} from "@angular/platform-browser";
import {ActivatedRoute, Router} from "@angular/router";
import {LoginService} from "../services/login.service";
import {AlertService} from "../services/alert.service";
import {first} from "rxjs";
import {OrdersService} from "../services/orders.service";
import {Order} from "../model/Order";

@Component({
  selector: 'app-add-order',
  templateUrl: './add-order.component.html',
  styleUrls: ['./add-order.component.css']
})
export class AddOrderComponent implements OnInit {

  // @ts-ignore
  newOrderForm: FormGroup;
  loading = false;
  submitted = false;
  paymentHandler: any = null;
  orderId: number = 0;
  currentPrice: number = 0;

  constructor(
    private titleService:Title,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrdersService,
    private loginService: LoginService,
    private alertService: AlertService
  ) {
    this.titleService.setTitle("Add Order");
  }

  ngOnInit() {
    this.newOrderForm = this.formBuilder.group({
      description: ['', Validators.required],
      address: ['', Validators.required],
      postalCode: ['', Validators.required],
      city: ['', Validators.required],
    });
    this.orderService.currentPrice.subscribe((value) => {
      this.currentPrice = value;
    });
    this.invokeStripe();
  }

  get f() {
    return this.newOrderForm.controls;
  }

  submitData() : boolean {
    this.submitted = true;

    this.alertService.clear();

    if (this.newOrderForm.invalid) {
      return false;
    }

    this.loading = true;
    this.orderService.create(new Order(0, this.f['address'].value, this.f['postalCode'].value,
      this.f['description'].value, this.f['city'].value,"OPEN")).pipe(first())
      .subscribe({
        next: data => {
          this.orderId = data.id;
        },
        error: error => {
          this.loading = false;
          this.alertService.error("Błąd przy dodawaniu nowego zamówienia!");
          return false;
        }
      })

    return true

  }

  makePayment(amount: number) {
    if (!this.submitData())
      return;
    const paymentHandler = (<any>window).StripeCheckout.configure({
      key: 'pk_test_51LIypGLygKylQIQC6llDocDHS6wd1Wv251ySMpnow9SJzuBcPQlk02DptBf1UppsdlvnUwn702Pz5lPOgdqIjz2q00O5T2cbEA',
      locale: 'auto',
      token: function (stripeToken: any) {
        paymentstripe(stripeToken);
      },
    });

    const paymentstripe = (stripeToken: any) => {
      this.orderService.makePayment(stripeToken,this.orderId).subscribe((data: any) => {
        if (data.chargeStatus === "succeeded") {
          this.loading = false;
          this.alertService.success("Twoje zamówienie zostało opłacone oraz przekazane " +
            "do realizacji! Nastąpi przekierowanie za 5 sekund");
          setTimeout(() => {
            this.router.navigate(['items']);
          }, 5000);
        }
        else {
          this.loading = false;
          this.alertService.error("Błąd przy platnosci zamówienia!");
          setTimeout(() => {
            this.router.navigate(['items']);
          }, 5000);
        }
      });
    };

    paymentHandler.open({
      name: 'Clothies',
      description: 'Proszę wprowadzic dane płatności!',
      amount: this.currentPrice*100,
      currency: 'pln'
    });
  }

  invokeStripe() {
    if (!window.document.getElementById('stripe-script')) {
      const script = window.document.createElement('script');
      script.id = 'stripe-script';
      script.type = 'text/javascript';
      script.src = 'https://checkout.stripe.com/checkout.js';
      script.onload = () => {
        this.paymentHandler = (<any>window).StripeCheckout.configure({
          key: 'pk_test_51LIypGLygKylQIQC6llDocDHS6wd1Wv251ySMpnow9SJzuBcPQlk02DptBf1UppsdlvnUwn702Pz5lPOgdqIjz2q00O5T2cbEA',
          locale: 'auto',
          token: function (stripeToken: any) {},
        });
      };

      window.document.body.appendChild(script);
    }
  }
}
