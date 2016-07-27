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
        invoice.setId(rs.getInt(0));
        invoice.setTotal(rs.getFloat(2));
        
        Customer customer = new Customer();
        customer.setId(rs.getInt(1));
        
        return invoice;
    }
}
