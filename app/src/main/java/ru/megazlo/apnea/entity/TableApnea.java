package ru.megazlo.apnea.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "table_apnoe")
public class TableApnea {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false, columnName = "allow_edit")
    private boolean allowEdit;

    @DatabaseField(canBeNull = false, columnName = "color")
    private int color;

    @DatabaseField(canBeNull = false, columnName = "title")
    private String title;

    @ForeignCollectionField
    private ForeignCollection<TableRow> rows;
}
