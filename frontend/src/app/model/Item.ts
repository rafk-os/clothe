export class Item {
  id: number
  name: string
  type: string
  description: string
  price: number
  addedToCart: boolean
  constructor(id :number, name:string,type:string,description:string, price:number, added : boolean) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.description = description;
    this.price = price;
    this.addedToCart = added;
  }
}
