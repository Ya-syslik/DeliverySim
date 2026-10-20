import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class  main{
    static public void main(String[] args){
        ConsoleOutService consoleOutService=ConsoleOutService.getInstance();
        consoleOutService.startWorkCycle();
    }
}

class Client{
    private String name;
    private String number;
    private String address;

    private Client(){}

    private static class  Holder{
        static final Client INSTANCE=new Client();
    }

    public static Client getInstance(){
        return Holder.INSTANCE;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNumber(String number) {
        this.number = number;
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

        Item(String name,int price){
            this.name=name;
            this.price=price;
        }

    public void setAmount(int amount) {
        this.amount = amount;
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
        calculateTotalPrice();
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
        calculateTotalPrice();
    }
    public List<Item> getOrderItems(){
        List<Item> items=new ArrayList<>();
        for (Item i:this.orderItems) {
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

    public int getDistance() {
        return distance;
    }
}

class OrderControlService {
    private List<Order> orders=new ArrayList<>();
    private List<Courier> couriers=new ArrayList<>();
    static private int idCounter=0;

    private Order currentOrder;


    private OrderControlService(){
        couriers.add(new Courier("Vasya","13"));
        couriers.add(new Courier("Isaac","42"));
        couriers.add(new Courier("JGun","228"));
    }

    private static class Holder{
        private static final OrderControlService INSTANCE=new OrderControlService();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public static OrderControlService getInstance(){
        return Holder.INSTANCE;
    }

    public void createNewOrder(Client client){
        idCounter++;
        orders.add(new Order(client,idCounter));
    }

    public void chooseDeliveryMethod(DeliveryMethod method){//с назначением курьера
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

    public Order getCurrentOrder() {
        return currentOrder;
    }
}



class ConsoleOutService{

    private ConsoleOutService(){}

    private static class Holder{
        private static final ConsoleOutService INSTANCE=new ConsoleOutService();
    }

    public static ConsoleOutService getInstance(){
        return ConsoleOutService.Holder.INSTANCE;
    }

    private Client clientInfo=Client.getInstance();
    private String inputMenuOption;
    private Scanner scanner=new Scanner(System.in);

    private OrderControlService orderControlService=OrderControlService.getInstance();

    private Menu menu=Menu.getInstance();
    private Map<Integer,Item> menuInfo= menu.loadMenuFromSomewhere();

    private boolean exitFlag=false;

    private void signUp(){
        System.out.println("Введите свое имя:");
        clientInfo.setName(scanner.nextLine().trim());
        System.out.println("Введите адрес для доставки:");
        clientInfo.setAddress(scanner.nextLine().trim());
        System.out.println("Введите свой номер телефона:");
        clientInfo.setNumber(scanner.nextLine().trim());
    }

    private void customerEditOrder(){
        System.out.println("Введите опцию редактирования: ");
        System.out.println("1. Добавить позицию в заказ");
        System.out.println("2. Выбрать способ доставки.");
        System.out.println("3. Показать параметры заказа");
        System.out.println("4. Вернуться в меню.");

        switch (scanner.nextLine().trim()){
            case "1":
                showItemsMenu(menuInfo);
                if(!addMenuItemByID(scanner.nextLine().trim(),menuInfo)){
                    System.out.println("Нет товара с таким ID");
                };
                break;
            case "2":
                System.out.println("1. Курьером");
                System.out.println("2. Дроном");
                System.out.println("3. Самовывоз");
                switch (scanner.nextLine().trim()){
                    case "1":
                        orderControlService.chooseDeliveryMethod(new StandardDelivery());
                        break;
                    case "2":
                        try{
                        orderControlService.chooseDeliveryMethod(new ExpressDelivery());}
                        catch (IllegalStateException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "3":
                        orderControlService.chooseDeliveryMethod(new SelfPickup());
                        break;
                    default:
                        System.out.println("Некорректный номер выбора способа доставки");
                        break;
                }
                break;
            case "3":
                showOrderStats();
                break;
            case "4":
                break;
            default:
                System.out.println("Неккоректное значение номера опции!");
                break;
        }

    }
    private void changeStatusOnInput(OrderStatus changeStatus){
        try{
            orderControlService.changeOrderStatus(changeStatus);
        }catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
    private void adminEditOrder() {
        System.out.println("Текущий статус: " + orderControlService.getCurrentOrder().getCurrentStatus());
        System.out.println("Изменить на: ");
        System.out.println("1.PREPARING ");
        System.out.println("2.INTRANSIT ");
        System.out.println("3.DELIVERED ");
        System.out.println("4.CANCELED ");

        switch (scanner.nextLine().trim()) {
            case "1":
                changeStatusOnInput(OrderStatus.PREPARING);
                break;
            case "2":
                changeStatusOnInput(OrderStatus.INTRANSIT);
                break;
            case "3":
                changeStatusOnInput(OrderStatus.DELIVERED);
                break;
            case "4":
                changeStatusOnInput(OrderStatus.CANCELED);
                break;
            default:
                System.out.println("Введи правильный номер,ты же админ!!!");
                break;
        }
       showOrderStats();
    }


    public void startWorkCycle(){
        signUp();
        while(!exitFlag){
            showGeneralMenu();
            switch (setMenuOption()){
                case "1":
                    orderControlService.createNewOrder(clientInfo);
                    break;
                case "2":
                    if (orderControlService.getOrders().isEmpty()){
                        System.out.println("Сначала создайте заказ!");
                        break;
                    }
                    System.out.println("Выберите заказ (введите его ID):");
                    showAllOrders();
                    if(findOrderByID(scanner.nextLine().trim())) {
                        customerEditOrder();
                    }
                    else{
                        System.out.println("Нет заказа с таким ID");
                    }
                    break;
                case "3":
                    System.out.println("Выберите заказ:");
                    showAllOrders();
                    if(findOrderByID(scanner.nextLine().trim())) {
                        adminEditOrder();
                    }
                    else{
                        System.out.println("Нет заказа с таким ID");
                    }
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
        inputMenuOption=scanner.nextLine().trim();
        return inputMenuOption;
    }


    private void showGeneralMenu(){
        System.out.println("Главное меню");
        System.out.println("1.Создать новый заказ");
        System.out.println("2.Показать мои заказы");
        System.out.println("3.(Admin) Выбрать заказ для изменения статуса");
        System.out.println("4.Завершить работу");
    }

    private void showItemsMenu(Map<Integer,Item> menuInfo){
        System.out.println("Выберете позиции:");
        for (int itemID: menuInfo.keySet()){
            Item itemInfo =menuInfo.get(itemID);
            System.out.println(itemID+": ");
            System.out.println(itemInfo.getName());
            System.out.println(itemInfo.getPrice()+" тенге");
        }
        System.out.println("0.Вернуться в меню");
    }



    //сразу устанавливает orderControlService.currentOrder при нахождении
    private boolean findOrderByID(String inputId){
        int id= Integer.parseInt(inputId);
        boolean correctOrderIDInput=false;
        for (Order i:orderControlService.getOrders()){
            if(i.getId()==id){
                orderControlService.setCurrentOrder(i.getId());
                correctOrderIDInput=true;
            }
        }
        return correctOrderIDInput;
    }
    private boolean addMenuItemByID(String inputId,Map<Integer,Item> menuInfo){

        int id= Integer.parseInt(inputId);
        Item foundItem=null;

        boolean correctOrderIDInput=false;
        for (Integer i:menuInfo.keySet()){
            if(id==i){
                foundItem=menuInfo.get(i);
                orderControlService.getCurrentOrder().addItem(foundItem);
                correctOrderIDInput=true;
            }
        }
        return correctOrderIDInput;
    }

    public void showOrderStats(){
        System.out.println("---------------------------------------------");
        System.out.println("ID: "+orderControlService.getCurrentOrder().getId());
        System.out.println("Receiver: "+orderControlService.getCurrentOrder().getReceiver().getName());
        System.out.println("Delivery address: "+orderControlService.getCurrentOrder().getDeliveryAddress());
        System.out.println("Distance: "+orderControlService.getCurrentOrder().getDistance());
        System.out.println("Current status: "+orderControlService.getCurrentOrder().getCurrentStatus());
        System.out.println("Order Positions: "+orderControlService.getCurrentOrder().getOrderItems());//***

        if(orderControlService.getCurrentOrder().getDeliveryMethod()!=null) {
            System.out.println("Courier: " + orderControlService.getCurrentOrder().getDeliveryPerson().getName());
            System.out.println("Price: " + orderControlService.getCurrentOrder().getTotalPrice());
            System.out.println("Delivery Method: " + orderControlService.getCurrentOrder().getDeliveryMethod());//***
            System.out.println("Approximate delivery time: " + orderControlService.getCurrentOrder().getDeliveryTime());
        }
        System.out.println("---------------------------------------------");
    }

    public void showAllOrders(){
        System.out.println("---------------------------------------------");
        for (Order i:orderControlService.getOrders()){
            System.out.println("ID: "+i.getId());
            System.out.println("Current status: "+i.getCurrentStatus());
            System.out.println("---------------------------------------------"+'\n');
        }
    }
//    public void clearConsole() {
//        System.out.print("\033[H\033[2J");
//        System.out.flush();
//    }

}
class Menu{
    private Menu(){}

    private static class Holder{
        private static final Menu INSTANCE=new Menu();
    }

    public static Menu getInstance(){
        return Menu.Holder.INSTANCE;
    }
    public Map<Integer,Item> loadMenuFromSomewhere() {

        //договоримся что существовала логика по которой,
        // ID создвались только в натуральных числах и не повторялись

        Map<Integer, Item> menu = new HashMap<>();
        menu.put(1, new Item("банан", 600));
        menu.put(2, new Item("пакет гречки", 900));
        menu.put(3, new Item("батон", 400));
        return menu;
    }
}

//настроить вывод позиций и метода доставки
//продолжить дебаг

