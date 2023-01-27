import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Item} from "../model/Item";

@Injectable({
  providedIn: 'root'
})
export class ItemsService {

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Item[]>(`http://localhost:8080/api/items`);
  }

  create(item: Item) {
    return this.http.post(`http://localhost:8080/api/items`, item);
  }

  addToCart(id :number) {
    // @ts-ignore
    return this.http.post(`http://localhost:8080/api/items/${id}/cart`);
  }
  deleteFromCart(id:number){
    return this.http.delete(`http://localhost:8080/api/items/${id}/cart`);
  }
  delete(id: number) {
    return this.http.delete(`http://localhost:8080/api/items/${id}`);
  }

}
