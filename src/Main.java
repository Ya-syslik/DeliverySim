import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

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

        boolean courierBecameFree=(currentStatus==OrderStatus.CANCELED)||(currentStatus==OrderStatus.DELIVERED);
        if (courierBecameFree) {
            deliveryPerson.changeStatus(CourierStatus.FREE);
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

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
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
            boolean freeCourierHasBeenFound=false;
            for(Courier c:couriers){
                if (c.getStatus()==CourierStatus.FREE){
                    currentOrder.setDeliveryPerson(c);
                    c.changeStatus(CourierStatus.ON_DELIVERY);
                    freeCourierHasBeenFound=true;
                    break;
                }
            }
            if(!freeCourierHasBeenFound){
                throw new IllegalStateException("All couriers are busy at that moment, pls try later");
            }
        }
    }

    public void changeOrderStatus(OrderStatus nextStatus){
        currentOrder.changeStatus(nextStatus);
    }

    public void showOrderStats(){
        System.out.println("---------------------------------------------");
        System.out.println("ID: "+currentOrder.getId());
        System.out.println("Receiver: "+currentOrder.getReceiver());
        System.out.println("Delivery address: "+currentOrder.getDeliveryAddress());
        System.out.println("Courier: "+currentOrder.getDeliveryPerson());
        System.out.println("Order positions: "+currentOrder.getOrderItems());
        System.out.println("Price: "+currentOrder.getTotalPrice());
        System.out.println("Delivery Method: "+currentOrder.getDeliveryMethod());
        System.out.println("Current status: "+currentOrder.getCurrentStatus());
        System.out.println("Approximate delivery time: "+currentOrder.getDeliveryTime());
        System.out.println("---------------------------------------------");
    }

    public void showAllOrders(){
        System.out.println("---------------------------------------------");
        for (Order i:orders){
            System.out.println("ID: "+i.getId());
            System.out.println("Current status: "+i.getCurrentStatus());
            System.out.println("---------------------------------------------"+'\n');
        }
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

class ConsoleOutService{
    private Scanner scanner=new Scanner(System.in);
    private boolean exitFlag=false;
    public void startWorkCycle(){
        while(!exitFlag){
            showGeneralMenu();
            switch (setMenuOption()){
                case "1":
                    break;
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    System.out.println("Arrivederci!!");
                    exitFlag=true;
                    break;
                default:
                    System.out.println("Введено неверное значение!!");
                    break;
            };
        }
    }

    private String setMenuOption(){
        String menuOption=scanner.nextLine().trim();
        return menuOption;
    }


    private void showGeneralMenu(){
        System.out.println("Главное меню");
        System.out.println("1.Создать новый заказ");
        System.out.println("2.Показать мои заказы");
        System.out.println("3.(Admin) Выбрать заказ для изменения статуса");
        System.out.println("4.Завершить работу");
    }


}

