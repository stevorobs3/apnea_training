package ru.megazlo.apnea.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "table_apnea")
public class TableApnea implements Serializable {

	private boolean running = false;

	private TableType type = TableType.USER;

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField(canBeNull = false, columnName = "allow_edit")
	private boolean allowEdit;

	@DatabaseField(canBeNull = false, columnName = "color")
	private int color = 0xff0e71ae;

	@DatabaseField(canBeNull = false, columnName = "title")
	private String title;

	@DatabaseField(columnName = "description")
	private String description;

	public TableApnea() {
	}

	public TableApnea(Integer id) {
		this.id = id;
	}
}
