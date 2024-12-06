package net.nimbus.commons.database.query;

import lombok.Getter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ToString
public class Result {

    @Getter
    private final List<Row> rows = new ArrayList<>();

    public Result(ResultSet resultSet) {
        try {

            while (resultSet.next()) {
                Row row = new Row();

                ResultSetMetaData meta = resultSet.getMetaData();
                int columnCount = meta.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = meta.getColumnName(i);
                    row.addColumn(new Column(columnName, resultSet.getObject(i)));
                }

                rows.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public Row getFirstRow() {
        return rows.get(0);
    }

}
