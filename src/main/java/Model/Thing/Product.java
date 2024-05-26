package Model.Thing;

public class Product implements IProduct {
    private String ID;
    private String Category;
    private String name;
    private String CPU;
    private String Ram;
    private String Storage;
    private String Display;
    private int Year;
    private int Quantity;
    private String Seller;
    private int Price;
    private int ONumber;
    private byte[] image;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Product(String ID, String category, String name, String CPU, String ram, String storage, String display, int year, int quantity, int price, String seller, byte[] image) {
        this.ID = ID;
        Category = category;
        this.name = name;
        this.CPU = CPU;
        Ram = ram;
        Storage = storage;
        Display = display;
        Year = year;
        Quantity = quantity;
        Seller = seller;
        Price = price;
        this.image = image;
    }

    public Product(String ID, String category, String name, String CPU, String ram, String storage, String display,  int year, int quantity, int price, String seller) {
        this.ID = ID;
        this.Category = category;
        this.name = name;
        this.CPU = CPU;
        this.Ram = ram;
        this.Storage = storage;
        this.Display = display;
        this.Year = year;
        this.Quantity = quantity;
        this.Price = price;
        this.Seller = seller;
    }

    public Product(String ID, String category, String name, String CPU, String ram, String storage, String display, int year, int quantity, int price, String seller, int onumber) {
        this.ID = ID;
        this.Category = category;
        this.name = name;
        this.CPU = CPU;
        this.Ram = ram;
        this.Storage = storage;
        this.Display = display;
        this.Year = year;
        this.Quantity = quantity;
        this.Price = price;
        this.Seller = seller;
        this.ONumber = onumber;
    }

    public Product(String ID, String category, String name, String CPU, String ram, String storage, String display,  int year, int quantity, int price) {
        this.ID = ID;
        this.Category = category;
        this.name = name;
        this.CPU = CPU;
        this.Ram = ram;
        this.Storage = storage;
        this.Display = display;
        this.Year = year;
        this.Quantity = quantity;
        this.Price = price;
    }

    public Product(String ID, String name, String CPU, String ram, String storage, String display, int quantity, int price, String Seller) {
        this.ID = ID;
        this.name = name;
        this.CPU = CPU;
        this.Ram = ram;
        this.Storage = storage;
        this.Display = display;
        this.Quantity = quantity;
        this.Price = price;
        this.Seller = Seller;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCPU() {
        return CPU;
    }

    public void setCPU(String CPU) {
        this.CPU = CPU;
    }

    public String getRam() {
        return Ram;
    }

    public void setRam(String ram) {
        Ram = ram;
    }

    public String getStorage() {
        return Storage;
    }

    public void setStorage(String storage) {
        Storage = storage;
    }

    public String getDisplay() {
        return Display;
    }

    public void setDisplay(String display) {
        Display = display;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getSeller() {
        return Seller;
    }

    public void setSeller(String seller) {
        Seller = seller;
    }
}
