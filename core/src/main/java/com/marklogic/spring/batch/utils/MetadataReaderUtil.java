package com.marklogic.spring.batch.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataReaderUtil {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static String PK_MAP_KEY = "%";
    public static String ORDER_MAP_KEY = "$";
    public static String TABLE_NAME_MAP_KEY = "*";
    public static String META_MAP_KEY = "^";

    private Map<String, Map<String, Object>> METADATA = new HashMap<>();
    private String tableName;
    private DataSource dataSource;
    private String databaseVendor;

    /* assumes all tables */
    public MetadataReaderUtil(DataSource dataSource, String databaseVendor) {
        this.databaseVendor = databaseVendor;
        this.dataSource = dataSource;
        this.load();
    }

    public MetadataReaderUtil(DataSource dataSource, String databaseVendor, String tableName) {
        this.databaseVendor = databaseVendor;
        this.dataSource = dataSource;
        this.tableName = tableName;
        this.load();
    }

    private List<String> getTableNames() throws ItemStreamException {
        List<String> tables = new ArrayList<>();
        try (Connection conn = this.dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
        return tables;
    }

    private Map<String, Object> getTableMetadata(String tableName) throws ItemStreamException {
        Map<String, Object> metadata = new HashMap<>();
        String sql = getSqlQueryForTable(tableName);
        try (Connection conn = this.dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
        ) {
            ResultSetMetaData mtrs = rs.getMetaData();
            int columnCount = mtrs.getColumnCount();
            Map<Integer, String> index = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String key = JdbcUtils.lookupColumnName(mtrs, i);
                metadata.put(key, mtrs.getColumnTypeName(i));
                index.put(i, key);
            }
            metadata.put(ORDER_MAP_KEY, index);
            metadata.put(TABLE_NAME_MAP_KEY, tableName);
            String pk = getPrimaryKey(tableName);
            if (null == pk || "".equals(pk)) {
                metadata.put(PK_MAP_KEY, index.get(1));
            } else {
                metadata.put(PK_MAP_KEY, getPrimaryKey(tableName));
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
        return metadata;
    }

    private void load() {
        if (null == this.tableName || "".equals(this.tableName)) {
            List<String> tables = getTableNames();
            for (String tableName : tables) {
                save(tableName);
            }
        } else {
            save(this.tableName);
        }
    }

    private void save(String tableName) {
        METADATA.put(tableName, getTableMetadata(tableName));
    }

    public Map<String, Map<String, Object>> getMetadata() {
        return METADATA;
    }

    private String getSqlQueryForTable(String tableName) {
        String sql = null;
        if (tableName.contains(" ") && "MICROSOFT".equals(databaseVendor.toUpperCase())) {
            tableName = "[" + tableName + "]";
        }
        return sql != null ? sql : "SELECT * FROM " + tableName;
    }

    /**
     * @return primary key of the tableName.
     */
    protected String getPrimaryKey(String tableName) {
        String pk = "";
        try (Connection conn = this.dataSource.getConnection();
             ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, tableName);
        ) {
            if (rs.next()) {
                pk = rs.getString("COLUMN_NAME");
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
        return pk;
    }

}
