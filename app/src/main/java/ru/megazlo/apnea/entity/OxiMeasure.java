package ru.megazlo.apnea.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/** Created by iGurkin on 10.10.2016. */
@Getter
@Setter
@DatabaseTable(tableName = "table_apnea")
public class OxiMeasure implements Serializable {
	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField(canBeNull = false, columnName = "series")
	private int seriesId;

	@DatabaseField(canBeNull = false, columnName = "spo")
	private int spo;

	@DatabaseField(canBeNull = false, columnName = "heart")
	private int heart;
}
