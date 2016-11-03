package ru.megazlo.apnea.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "table_apnea_row")
public class TableApneaRow  extends AbstractEntity {

	private RowState state = RowState.NONE;

	@DatabaseField(canBeNull = false, columnName = "row_order")
	private Integer order;

	@DatabaseField(canBeNull = false, columnName = "breathe_sec")
	private int breathe = 0;

	@DatabaseField(canBeNull = false, columnName = "hold_sec")
	private int hold = 0;

	@DatabaseField(canBeNull = false, columnName = "table_id")
	private Integer table;

	private int extHold = 0;

	private int extBreathe = 0;

	public void reset() {
		extHold = 0;
		extBreathe = 0;
		state = RowState.NONE;
	}
}
