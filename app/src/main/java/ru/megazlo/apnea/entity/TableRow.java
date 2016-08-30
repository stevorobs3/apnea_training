package ru.megazlo.apnea.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "table_row")
public class TableRow {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false, columnName = "row_order")
    private short order;

    @DatabaseField(canBeNull = false, columnName = "breathe_sec")
    private int breathe;

    @DatabaseField(canBeNull = false, columnName = "hold_sec")
    private int hold;

    @DatabaseField(foreign = true, canBeNull = false, columnName = "table_id")
    private TableApnea table;
}
