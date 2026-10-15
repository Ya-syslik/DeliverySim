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

class Courier{
    private String name;
    private String number;
    private String status;

    Courier(String name,String number,String status){
        this.status=status;
        this.name=name;
        this.number=number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
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


