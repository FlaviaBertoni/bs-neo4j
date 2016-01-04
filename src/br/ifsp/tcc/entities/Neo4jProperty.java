package br.ifsp.tcc.entities;

import java.util.ArrayList;

public class Neo4jProperty {

	// A classe Property tem o propósito de armazenar uma propriedade e seus N
	// valores.

	private String propertyName;
	private ArrayList<String> propertyValue;
	private String allPropertyValues;

	public Neo4jProperty() {
		propertyValue = new ArrayList<String>();
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue(int i) {
		return propertyValue.get(i);
	}

	public ArrayList<String> getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue.add(propertyValue);
	}

	public String getAllPropertyValues() {
		return allPropertyValues;
	}

	public void setAllPropertyValues(String allPropertyValues) {
		this.allPropertyValues = allPropertyValues;
	}

}
