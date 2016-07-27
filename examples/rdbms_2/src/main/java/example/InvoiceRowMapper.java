package example;

import example.data.Customer;
import example.data.Invoice;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InvoiceRowMapper implements RowMapper<Invoice> {
    
    @Override
    public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("invoice.ID"));
        invoice.setTotal(rs.getFloat("TOTAL"));
        
        Customer customer = new Customer();
        customer.setId(rs.getInt("CUSTOMERID"));
        
        invoice.setCustomer(customer);
        
        return invoice;
    }
}
