package ru.megazlo.apnea.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "table_apnea_row")
public class TableApneaRow {

    private RowState state = RowState.NONE;

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false, columnName = "row_order")
    private Integer order;

    @DatabaseField(canBeNull = false, columnName = "breathe_sec")
    private int breathe;

    @DatabaseField(canBeNull = false, columnName = "hold_sec")
    private int hold;

    @DatabaseField(canBeNull = false, columnName = "table_id")
    private Integer table;
}
