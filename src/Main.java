import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class  main{
    public static void main(String[] args){

    }
}



class Client{
    private String name;
    private String number;
    private String address;

    Client(String name,String number,String address){
        this.address=address;
        this.name=name;
        this.number=number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }
}

enum CourierStatus{
    FREE,
    ON_DELIVERY;
}

class Courier{

    private String name;
    private String number;
    private CourierStatus status;


    Courier(String name,String number,String status){
        this.status=CourierStatus.FREE;
        this.name=name;
        this.number=number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public CourierStatus getStatus() {
        return status;
    }
    public void changeStatus(CourierStatus next){
        this.status=next;
    }
}

class Item {
        private String name;
        private String price;
        private int amount;

        Item(String name,String price,int amount){
            this.name=name;
            this.price=price;
            this.amount=amount;
        }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }
}

enum OrderStatus{
    CANCELED(makeHashFromArray(new OrderStatus[] {})),
    DELIVERED(makeHashFromArray(new OrderStatus[] {})),
    INTRANSIT(makeHashFromArray(new OrderStatus[] {DELIVERED,CANCELED})),
    PREPARING(makeHashFromArray(new OrderStatus[] {INTRANSIT,CANCELED})),
    CREATED(makeHashFromArray(new OrderStatus[] {PREPARING,CANCELED}));

    private Set<OrderStatus> StatusesAllowedToChangeIn= new HashSet<>();

    OrderStatus(Set<OrderStatus> StatusesAllowedToChangeIn){
        this.StatusesAllowedToChangeIn=StatusesAllowedToChangeIn;
    }

    public boolean canChangeStatusOn(OrderStatus next){
        return StatusesAllowedToChangeIn.contains(next);
    }

    static private Set<OrderStatus> makeHashFromArray(OrderStatus[] allowedTransitionsArray){
        Set<OrderStatus> resultSet=new HashSet<>();
        for (OrderStatus i:allowedTransitionsArray){
            resultSet.add(i);
        }
        return resultSet;
    }
}

class Order{
    private Client receiver;
    private Courier deliveryPerson;
    private OrderStatus currentStatus;

    private int id;
    private String deliveryAddress;
    private List<Item> orderItems=new ArrayList<>();

    Order(Client receiver,Courier deliveryPerson,int id){
        this.currentStatus=OrderStatus.CREATED;

        this.receiver=receiver;
        this.deliveryAddress=receiver.getAddress();
        this.deliveryPerson=deliveryPerson;
        this.id=id;
    }

    public void changeStatus(OrderStatus next){
        if(!currentStatus.canChangeStatusOn(next)){
            throw new IllegalStateException("Can't change status from: "+ currentStatus + " to " + next);
        }
        currentStatus=next;

        boolean courierIsFree=(currentStatus==OrderStatus.CANCELED)||(currentStatus==OrderStatus.DELIVERED);
        if (courierIsFree){
            deliveryPerson.changeStatus(CourierStatus.FREE);
        }
        boolean courierOnDelivery=(currentStatus==OrderStatus.INTRANSIT);
        if(courierOnDelivery){
            deliveryPerson.changeStatus(CourierStatus.ON_DELIVERY);
        }
    }


    public int getId() {
        return id;
    }


    public Courier getDeliveryPerson() {
        return deliveryPerson;
    }


    public Client getReceiver() {
        return receiver;
    }
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void addItem(Item i){
        orderItems.add(i);
    }
    public List<Item> getOrderItems(){
        List<Item> items=new ArrayList<>();
        for (Item i:this.orderItems){
            items.add(i);
        }
        return items;
    }
}



