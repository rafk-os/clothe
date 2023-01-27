import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Order} from "../model/Order";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class OrdersService {
  private totalPrice = new BehaviorSubject<any>({});
  currentPrice = this.totalPrice.asObservable();

  setPrice(price: any) {
    this.totalPrice.next(price);
  }

  constructor(private http: HttpClient) {}

  changeStatus(id: number, status: string) {
    return this.http.put(`http://localhost:8080/api/orders/${id}/status`, {"status":status});
  }
  getALL() {
    return this.http.get<Order[]>(`http://localhost:8080/api/orders`);
  }
  create(order :Order){
    return this.http.post<Order>(`http://localhost:8080/api/orders/`, order);

  }

  makePayment(stripeToken : any, id : number){
    const headers = new HttpHeaders({'token': stripeToken.id });
    return this.http.post(`http://localhost:8080/api/orders/${id}/charge`, {} , {headers:headers});

  }
}
