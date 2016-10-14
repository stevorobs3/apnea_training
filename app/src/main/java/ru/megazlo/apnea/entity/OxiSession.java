package ru.megazlo.apnea.entity;

import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lombok.*;

/** Created by iGurkin on 14.10.2016. */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "table_apnea")
public class OxiSession extends AbstractEntity {

	private Integer total = 360;

	private Integer count = 0;

	private Date date = new Date();
}
