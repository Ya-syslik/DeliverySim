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