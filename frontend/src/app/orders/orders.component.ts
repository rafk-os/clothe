import { Component, OnInit } from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Title} from "@angular/platform-browser";
import {ActivatedRoute, Router} from "@angular/router";
import {LoginService} from "../services/login.service";
import {UserService} from "../services/user.service";
import {AlertService} from "../services/alert.service";
import {Order} from "../model/Order";
import {first} from "rxjs";
import {OrdersService} from "../services/orders.service";

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {
  newOrderStatusForm!: FormGroup;
  orders : Order[] = [];
  submitted = false;
  loading = false;
  modalReference!: NgbModalRef;
  nrSelect = "OPEN";
  currentId!: number;

  constructor(private modalService: NgbModal,
              private formBuilder: FormBuilder,
              private titleService:Title,
              private route: ActivatedRoute,
              private router: Router,
              private orderService: OrdersService,
              private loginService: LoginService,
              private userService: UserService,
              private alertService: AlertService
  ) {
    this.titleService.setTitle("Orders");
    this.orderService.getALL().pipe(first())
      .subscribe({
        next: data => {
          data.forEach((value: Order) => this.orders.push(Object.assign({},value)))
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy pobieraniu zamowien z bazy danych!");
        }
      })



  }

  open(content : any, id:number ){
    this.currentId = id;
    this.modalReference = this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  get f() {
    return this.newOrderStatusForm.controls;
  }

  ngOnInit(): void {
    this.newOrderStatusForm = this.formBuilder.group({
      status: ['', Validators.required],

    });
  }

  onSubmit() {

    this.orderService.changeStatus(this.currentId,this.f['status'].value).pipe(first())
      .subscribe({
        next: data => {
          window.location.reload();
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy zmianie statusu zamowienie!");
        }

      })


  }

  goToOrders() {
    this.router.navigate(['items']);
  }
}
