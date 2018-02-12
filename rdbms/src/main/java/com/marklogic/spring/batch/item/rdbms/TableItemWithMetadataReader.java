package com.marklogic.spring.batch.item.rdbms;

import com.marklogic.spring.batch.item.rdbms.support.MetadataReader;
import com.marklogic.spring.batch.item.rdbms.support.MetadataReaderImpl;
import com.marklogic.spring.batch.item.rdbms.support.RowToColumnMapWithMetadataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Derived from the AllTablesItemReader but this is metadata aware
 * because the column datatype are needed to generate the triples
 */
public class TableItemWithMetadataReader extends AllTablesItemReader {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private String tableName;
    private Map<String, Map<String, Object>> metadata;

    // For using a custom SQL query for a given table name
    // private Map<String, String> tableQueries;
    public TableItemWithMetadataReader(DataSource dataSource, String databaseVendor) {
        super(dataSource, databaseVendor);

        MetadataReader metadataReader = new MetadataReaderImpl(dataSource, databaseVendor);
        this.metadata = metadataReader.getMetadata();
    }

    public TableItemWithMetadataReader(DataSource dataSource, String databaseVendor, String tableName) {
        super(dataSource, databaseVendor);
        this.tableName = tableName;

        MetadataReaderImpl metadataReaderImpl = new MetadataReaderImpl(dataSource, databaseVendor, tableName);
        this.metadata = metadataReaderImpl.getMetadata();
    }

    /**
     * @return a list of the table names, retrieved via the connection metadata object. The excludeTableNames
     * property can be used to ignore certain table names.
     */
    @Override
    protected List<String> getTableNames() {
        if (!StringUtils.isEmpty(this.tableName)) {
            super.getTableNames().add(this.tableName);
        }
        return super.getTableNames();
    }

    /**
     * @param tableName
     * @param executionContext
     * @return a JdbcCursorItemReader for the given table name. Override this method to alert the SQL statement that's
     * used for a particular table.
     */
    @Override
    protected JdbcCursorItemReader<Map<String, Object>> buildTableReader(String tableName, ExecutionContext executionContext) {
        Map<String, Object> tableMetadata = this.metadata.get(tableName);
        JdbcCursorItemReader<Map<String, Object>> reader = super.buildTableReader(tableName, executionContext, new RowToColumnMapWithMetadataMapper(tableMetadata));
        reader.setVerifyCursorPosition(false);
        return reader;
    }
}