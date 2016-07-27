package example;

import example.data.Customer;
import example.data.Invoice;
import example.data.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRowMapper implements RowMapper<Invoice> {
    
    @Override
    public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("invoice.ID"));
        invoice.setTotal(rs.getFloat("TOTAL"));
        invoice.setQuantity(rs.getInt("QUANTITY"));
        
        Customer customer = new Customer();
        customer.setId(rs.getInt("CUSTOMERID"));
        customer.setFirstName(rs.getString("FIRSTNAME"));
        customer.setLastName(rs.getString("LASTNAME"));
        invoice.setCustomer(customer);
        
        Product product = new Product();
        product.setId(rs.getInt("product.ID"));
        product.setName(rs.getString("NAME"));
        product.setPrice(rs.getFloat("PRICE"));
        
        List<Product> products = new ArrayList<Product>();
        products.add(product);
        invoice.setProducts(products);
        
        return invoice;
    }
}
