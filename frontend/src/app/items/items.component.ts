import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AlertService} from "../services/alert.service";
import { ItemsService} from "../services/items.service";
import {first} from "rxjs";
import {LoginService} from "../services/login.service";
import {UserService} from "../services/user.service";
import {NgbModal, ModalDismissReasons, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Title} from "@angular/platform-browser";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {OrdersService} from "../services/orders.service";
import {Item} from "../model/Item";

@Component({
  selector: 'app-items',
  templateUrl: './items.component.html',
  styleUrls: ['./items.component.css']
})
export class ItemsComponent implements OnInit {
   newItemForm!: FormGroup;
   items : Item[] = []
   itemInCart : Item[] = []
   isAdmin : boolean = false
   submitted = false;
   loading = false;
   modalReference!: NgbModalRef;
   sum : number = 0.0;


  constructor(

    private modalService: NgbModal,
    private formBuilder: FormBuilder,
    private titleService:Title,
    private route: ActivatedRoute,
    private router: Router,
    private itemsService: ItemsService,
    private orderService: OrdersService,
    private loginService: LoginService,
    private userService: UserService,
    private alertService: AlertService
  ) {
    this.titleService.setTitle("Items");
    this.itemsService.getAll().pipe(first())
      .subscribe({
        next: data => {
          data.forEach((value: Item) => this.items.push(Object.assign({},value)))
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy pobieraniu ubrań z bazy danych!");
        }

      })

    this.userService.getUserCart(loginService.currentUserValue.id).pipe(first())
      .subscribe({
        next: data => {
          data.forEach(
            (value: Item) => {
              this.itemInCart.push(Object.assign({},value))
              this.sum += value.price;
              const itemIndex = this.items.findIndex((item => item.id == value.id));
              if(itemIndex != -1)
                this.items[itemIndex].addedToCart = true;
            }

          )
          orderService.setPrice(this.sum);
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy pobieraniu koszyka zakupu użytkownika!");
        }

      })
    // @ts-ignore
    this.userService.getUserRole(loginService.currentUserValue.id).pipe(first())
      .subscribe({
        next: data => {
          if (data.find(role => role.name == "ADMIN"))
            this.isAdmin = true;
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy pobieraniu roli użytkownika!");
        }

      })




  }

  onSubmit() {
    this.submitted = true;

    this.alertService.clear();

    if (this.newItemForm.invalid) {
      return;
    }
    this.loading = true;

    this.itemsService.create( new Item(0,this.f['name'].value,this.f['type'].value,this.f['description'].value, this.f['price'].value, false)).pipe(first())
      .subscribe({
        next: data => {
          window.location.reload();
          this.modalReference.close()
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy dodawaniu ubrania do bazy danych!");
        }

      })
  }

  get f() {
    return this.newItemForm.controls;
  }

  ngOnInit(): void {
    this.newItemForm = this.formBuilder.group({
      name: ['', Validators.required],
      type: ['', Validators.required],
      description: ['', Validators.required],
      price: ['', Validators.required]
    });

  }

  reloadComponent() {
    let currentUrl = this.router.url;
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.router.navigate([currentUrl]);
  }

  deleteItem(id : number){

    this.itemsService.delete(id).pipe(first())
      .subscribe({
        next: data => {
          window.location.reload();
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy usuwaniu ubrania z bazy danych!");
        }

      })

  }

  addItemToCart(id : number){
    this.itemsService.addToCart(id).pipe(first())
      .subscribe({
        next: data => {
          window.location.reload();
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy dodawaniu ubrania do koszyka!");
        }

      })
  }

  deleteItemFromCart(id : number){

    this.itemsService.deleteFromCart(id).pipe(first())
      .subscribe({
        next: data => {
          window.location.reload();
        },
        error: error => {
          this.alertService.error("Wystąpił błąd przy usuwaniu ubrania z koszyka!");
        }

      })

  }



  open(content: any) {
   this.modalReference = this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  open_cart(content_cart: any) {
   this.modalReference = this.modalService.open(content_cart, { size: 'lg', centered : true, ariaLabelledBy: 'modal-basic-title'});
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  goToOrders() {
    this.router.navigate(['orders']);
  }
  goToAddOrders() {
    this.modalReference.close()
    this.router.navigate(['add-order']);
  }
}
