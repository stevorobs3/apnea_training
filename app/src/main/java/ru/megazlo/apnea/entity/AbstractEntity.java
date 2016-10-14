package ru.megazlo.apnea.entity;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/** Created by iGurkin on 14.10.2016. */
@Getter
@Setter
public abstract class AbstractEntity implements Serializable {

	@DatabaseField(generatedId = true)
	private Integer id;

}
