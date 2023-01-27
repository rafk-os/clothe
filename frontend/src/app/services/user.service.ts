import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../model/User";
import {Role} from "../model/Role";
import {Item} from "../model/Item";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }


  register(user: User) {
    return this.http.post(`http://localhost:8080/api/users`, user);
  }

  delete(id: number) {
    return this.http.delete(`http://localhost:8080/api/users/${id}`);
  }

  getUserRole(id: number) {
    return this.http.get<Role[]>(`http://localhost:8080/api/users/${id}/role`);
  }

  getUserCart(id: number) {
    return this.http.get<Item[]>(`http://localhost:8080/api/users/cart/${id}`);
  }
}
