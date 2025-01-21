package net.nimbus.commons.database.query;

import lombok.ToString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ToString
public class Row {
    private final List<Column> columns;

    public String getString(String column) {
        return String.valueOf(columns.stream().filter(c -> c.getIdentifier().equals(column) && c.getValue() != null).map(Column::getValue).findFirst().orElse("null"));
    }

    public Integer getInteger(String column) {
        return Integer.valueOf(String.valueOf(columns.stream().filter(c -> c.getIdentifier().equals(column) && c.getValue() != null).map(Column::getValue).findFirst().orElse(null)));
    }

    public Long getLong(String column) {
        return Long.valueOf(String.valueOf(columns.stream().filter(c -> c.getIdentifier().equals(column) && c.getValue() != null).map(Column::getValue).findFirst().orElse(null)));
    }

    public Boolean getBoolean(String column) {
        return "1".equals(String.valueOf(columns.stream().filter(c -> c.getIdentifier().equals(column) && c.getValue() != null).map(Column::getValue).findFirst().orElse("false")));
    }

    public Date getDate(String column) {
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String dateString = String.valueOf(columns.stream().filter(c -> c.getIdentifier().equals(column) && c.getValue() != null).map(Column::getValue).findFirst().orElse(null));

        dateString = dateString.replace("T", " ");
        if (dateString.length() < 19) dateString += ":00";

        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Boolean isValuePresent(String column) {
        return columns.stream().filter(c -> c.getIdentifier().equals(column)).findFirst().orElse(new Column(column, null)).getValue() != null;
    }

    public Row() {
        this.columns = new ArrayList<>();
    }

    public void addColumn(Column column) {
        columns.add(column);
    }
}
