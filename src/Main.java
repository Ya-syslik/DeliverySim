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
        boolean listHaveThisItem=false;
        for(Item j:orderItems){
            if(i.getName().equals(j.getName())){
                listHaveThisItem=true;
                j.setAmount(j.getAmount()+1);
            }
        }
        if(!listHaveThisItem) {
            i.setAmount(1);
            orderItems.add(i);
        }
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
            totalPrice+=i.getPrice()*i.getAmount();
        }
    }

    public int getDistance() {
        return distance;
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
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

        if (currentOrder.getDeliveryMethod() != null && currentOrder.getDeliveryPerson() != null) {
            currentOrder.getDeliveryPerson().changeStatus(CourierStatus.FREE);
        }

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

        boolean OrderIsFinished =(nextStatus==OrderStatus.CANCELED)||(nextStatus==OrderStatus.DELIVERED);

        if (OrderIsFinished && currentOrder.getDeliveryPerson()!=null) {
            currentOrder.getDeliveryPerson().changeStatus(CourierStatus.FREE);
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

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public boolean findOrderByID(int id) {
        boolean correctOrderIDInput = false;
        for (Order i : orders) {
            if (i.getId() == id) {
                this.setCurrentOrder(i.getId());
                correctOrderIDInput = true;
            }
        }
        return correctOrderIDInput;
    }

    public boolean addMenuItemByID(int id, Map<Integer, Item> menuInfo) {
        Item foundItem = null;
        boolean correctOrderIDInput = false;
        for (Integer i : menuInfo.keySet()) {
            if (id == i) {
                foundItem = menuInfo.get(i);
                currentOrder.addItem(foundItem);
                correctOrderIDInput = true;
            }
        }
        return correctOrderIDInput;
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
                try {
                    // Парсим ввод пользователя в число
                    int itemId = Integer.parseInt(scanner.nextLine().trim());

                    if (!orderControlService.addMenuItemByID(itemId, menuInfo)) {
                        System.out.println("Нет товара с таким ID");
                    }
                } catch (NumberFormatException e) {
                    // Если вместо ID ввели буквы
                    System.out.println("Нет товара с таким ID");
                }
                showOrderStats();
                break;
            case "2":
                System.out.println("1. Курьером");
                System.out.println("2. Дроном");
                System.out.println("3. Самовывоз");
                switch (scanner.nextLine().trim()){
                    case "1":
                        try { orderControlService.chooseDeliveryMethod(new StandardDelivery()); }
                        catch (IllegalStateException e) { System.out.println(e.getMessage()); }
                        break;
                    case "2":
                        try { orderControlService.chooseDeliveryMethod(new ExpressDelivery()); }
                        catch (IllegalStateException e) { System.out.println(e.getMessage()); }
                        break;
                    case "3":
                        orderControlService.chooseDeliveryMethod(new SelfPickup());
                        break;
                    default:
                        System.out.println("Некорректный номер выбора способа доставки");
                        break;
                }
                showOrderStats();
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
            showOrderStats();
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
                    try {
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        if (orderControlService.findOrderByID(id)) {
                            customerEditOrder();
                        } else {
                            System.out.println("Нет заказа с таким ID");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Это даже не цифра,попадай по кнопкам пожалуйста!");
                    }
                    break;
                case "3":
                    System.out.println("Выберите заказ:");
                    showAllOrders();
                    try {
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        if (orderControlService.findOrderByID(id)) {
                            adminEditOrder();
                        } else {
                            System.out.println("Нет заказа с таким ID");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Это даже не цифра,попадай по кнопкам пожалуйста!");
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
            System.out.print(itemID+": ");
            System.out.println(itemInfo.getName());
            System.out.println(itemInfo.getPrice()+" тенге");
        }
        System.out.println("0.Вернуться в меню");
    }


    public void showOrderStats(){
        System.out.println("---------------------------------------------");
        System.out.println("ID: "+orderControlService.getCurrentOrder().getId());
        System.out.println("Receiver: "+orderControlService.getCurrentOrder().getReceiver().getName());
        System.out.println("Delivery address: "+orderControlService.getCurrentOrder().getDeliveryAddress());
        System.out.println("Distance: "+orderControlService.getCurrentOrder().getDistance()+" км");
        System.out.println("Current status: "+orderControlService.getCurrentOrder().getCurrentStatus());

        System.out.println("Order Positions: ");
        System.out.println("-^-");
       for(Item i :orderControlService.getCurrentOrder().getOrderItems()){
           System.out.println(i.getName());
           System.out.println("Price: " + i.getPrice() + " тг.");
           System.out.println("Amount: " + i.getAmount());
       }
        System.out.println("-^-");

        if(orderControlService.getCurrentOrder().getDeliveryMethod()!=null) {
            if(!(orderControlService.getCurrentOrder().getDeliveryMethod() instanceof SelfPickup)) {
                System.out.println("Courier: " + orderControlService.getCurrentOrder().getDeliveryPerson().getName());
                System.out.println("Courier number: " + orderControlService.getCurrentOrder().getDeliveryPerson().getNumber());
            }
            System.out.println("Delivery price: " + orderControlService.getCurrentOrder().getDeliveryPrice() + " тг.");
            System.out.println("Delivery Method: " + orderControlService.getCurrentOrder().getDeliveryMethod().getClass().getSimpleName());
            System.out.println("Approximate delivery time: " + orderControlService.getCurrentOrder().getDeliveryTime() + " сек.");
            System.out.println("Total price: " + orderControlService.getCurrentOrder().getTotalPrice() + " тг.");
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


