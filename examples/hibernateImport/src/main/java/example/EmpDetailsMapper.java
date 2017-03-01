package example;

import example.data.EmpDetails;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;


public class EmpDetailsMapper implements RowMapper<EmpDetails> {
    
    @Override
    public EmpDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmpDetails empDetails = new EmpDetails();
        empDetails.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
        empDetails.setJobId(rs.getString("JOB_ID"));
        empDetails.setManagerId(rs.getInt("MANAGER_ID"));
        empDetails.setDepartmentId(rs.getInt("DEPARTMENT_ID"));
        empDetails.setLocationId(rs.getInt("LOCATION_ID"));
        empDetails.setCountryId(rs.getString("COUNTRY_ID"));
        empDetails.setFirstName(rs.getString("FIRST_NAME"));
        empDetails.setLastName(rs.getString("LAST_NAME"));
        empDetails.setSalary(rs.getDouble("SALARY"));
        empDetails.setCommissionPct(rs.getDouble("COMMISSION_PCT"));
        empDetails.setDepartmentName(rs.getString("DEPARTMENT_NAME"));
        empDetails.setJobTitle(rs.getString("JOB_TITLE"));
        empDetails.setCity(rs.getString("CITY"));
        empDetails.setStateProvince(rs.getString("STATE_PROVINCE"));
        empDetails.setCountryName(rs.getString("COUNTRY_NAME"));
        empDetails.setRegionNamee(rs.getString("REGION_NAME"));
        
        return empDetails;
    }
}
