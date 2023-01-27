import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AppRoutingModule } from './app-routing.module';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AlertComponent } from './alert/alert.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { ItemsComponent } from './items/items.component';
import {JwtInterceptor} from "./util/JwtInterceptor";
import {ErrorInterceptor} from "./util/ErrorInterceptor";
import {NgbDatepickerModule, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import { OrdersComponent } from './orders/orders.component';
import { AddOrderComponent } from './add-order/add-order.component';
import {Alarm, allIcons, App, Bookmark} from "ng-bootstrap-icons/icons";
import {BootstrapIconsModule} from "ng-bootstrap-icons";


const icons = {
  Alarm,
  App,
  Bookmark
};

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    AlertComponent,
    ItemsComponent,
    OrdersComponent,
    AddOrderComponent,
  ],
  imports: [
    BootstrapIconsModule.pick(allIcons),
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    NgbDatepickerModule,
    NgbModule,

  ],
  exports:[
    BootstrapIconsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
