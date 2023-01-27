export class Order {
  id: number
  description: string
  adress: string
  postalCode: string
  city: number
  status: string
  constructor(id :number, address:string,postalCode:string,description:string, city:number, status:string) {
    this.id = id;
    this.description = description;
    this.adress = address;
    this.postalCode = postalCode;
    this.city = city;
    this.status = status;
  }
}
