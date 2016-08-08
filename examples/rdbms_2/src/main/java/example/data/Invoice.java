package example.data;

import java.util.List;

public class Invoice {
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public float getTotal() {
        return total;
    }
    
    public void setTotal(float total) {
        this.total = total;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> productList) {
        this.products = productList;
    }
        
    private int id;
    private String customerId;
    private int quantity;
    private float total;
    private Customer customer;
    private List<Product> products;
}
