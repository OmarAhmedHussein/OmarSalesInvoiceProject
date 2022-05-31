
package myfirstproject.model;

import java.util.ArrayList;

public class Invoice {
    private int num;
    private String date;
    private String customer;
    private ArrayList<Item> Items;
    
    public Invoice() {
    }

    public Invoice(int num, String date, String customer) {
        this.num = num;
        this.date = date;
        this.customer = customer;
    }

    public double getInvoiceTotal() {
        double total = 0.0;
        for (Item item : getItems()) {
            total += item.getItemTotal();
        }
        return total;
    }
    
    public ArrayList<Item> getItems() {
        if (Items == null) {
            Items = new ArrayList<>();
        }
        return Items;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Invoice{" + "num=" + num + ", date=" + date + ", customer=" + customer + '}';
    }
    
    public String fileCSV() {
        return num + "," + date + "," + customer;
    }
    
}
