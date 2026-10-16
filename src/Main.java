import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

class  main{
    static public void main(String[] args){

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


    Courier(String name,String number){
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
        private int price;
        private int amount;

        Item(String name,int price,int amount){
            this.name=name;
            this.price=price;
            this.amount=amount;
        }

    public String getName() {
        return name;
    }

    public int getPrice() {
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

interface DeliveryMethod{
    public int calculateDeliveryPrice(int distance);
    public int calculateDeliveryTime(int distance);
    public boolean courierIsNecessary();
}

class StandardDelivery implements DeliveryMethod{

    @Override
    public int calculateDeliveryPrice(int distance){
        return (distance * 5);
    }

    @Override
    public int calculateDeliveryTime(int distance) {
        return (distance * 5);
    }

    @Override
    public boolean courierIsNecessary(){return true;};
}

class ExpressDelivery implements DeliveryMethod{
    @Override
    public int calculateDeliveryPrice(int distance){
        return (distance * 8);
    }

    @Override
    public int calculateDeliveryTime(int distance) {
        return (distance * 3);
    }

    @Override
    public boolean courierIsNecessary(){return true;};
}

class SelfPickup implements DeliveryMethod{
    @Override
    public int calculateDeliveryPrice(int distance){
        return 0;
    }

    @Override
    public int calculateDeliveryTime(int distance) {
        return 0;
    }

    @Override
    public boolean courierIsNecessary(){return false;};
}


class Order{
    private DeliveryMethod deliveryMethod;
    private int totalPrice;
    private int deliveryPrice;
    private int deliveryTime;
    private int distance;

    private Client receiver;
    private Courier deliveryPerson;
    private OrderStatus currentStatus;

    private int id;
    private String deliveryAddress;
    private List<Item> orderItems=new ArrayList<>();

    Order(Client receiver,int id){
        this.currentStatus=OrderStatus.CREATED;
        this.receiver=receiver;
        this.deliveryAddress=receiver.getAddress();
        this.id=id;

        distance = ThreadLocalRandom.current().nextInt(1, 21);//random int [1,21]
    }

    public void setDeliveryPerson(Courier deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
        deliveryPrice=this.deliveryMethod.calculateDeliveryPrice(distance);
        deliveryTime=this.deliveryMethod.calculateDeliveryTime(distance);
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

    public void calculateTotalPrice(){
        totalPrice=deliveryPrice;
        for (Item i:orderItems){
            deliveryPrice+=i.getPrice();
        }
    }
}

class OrderControlService {
    private List<Order> orders=new ArrayList<>();
    private List<Courier> couriers=new ArrayList<>();
    static private int idCounter=0;
    private Order currentOrder;

    OrderControlService(){
        couriers.add(new Courier("Vasya","13"));
        couriers.add(new Courier("Isaac","42"));
        couriers.add(new Courier("JGun","228"));
    }

    public void createNewOrder(Client client){
        idCounter++;
        orders.add(new Order(client,idCounter));
    }

    public void chooseDeliveryMethod(DeliveryMethod method){//с назнчаением курьера
        currentOrder.setDeliveryMethod(method);
        if (method.courierIsNecessary()){
            //назначение курьера из доступных
        }
    }

    public void showOrderStats(){

    }

    public void changeOrderStatus(Order orderForChange){

    }

    public void showOrderList(){

    }

    public void setCurrentOrder(int id) {
        boolean foundFlag=false;
        for(Order i:orders){
            if(id==i.getId()){
                currentOrder=i;
                foundFlag=true;
            }
        };
        if(!foundFlag){
            throw new IllegalMonitorStateException("No such order ID");
        }
    }
}



