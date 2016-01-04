package br.ifsp.tcc.entities;

import java.util.ArrayList;

public class Neo4jNode {

	// A classe Node tem o prop�sito de armazenar n�o somente os dados de um n�
	// (key-values[]), mas tamb�m os relacionamentos entre esses n�s.

	private String uRI;
	private ArrayList<Neo4jProperty> properties;
	public static ArrayList<Neo4jRelationship> relationships = new ArrayList<Neo4jRelationship>();

	public String getURI() {
		return uRI;
	}

	public void setURI(String uRI) {
		this.uRI = uRI;
	}

	public ArrayList<Neo4jProperty> getProperties() {
		return properties;
	}

	public void setProperties(ArrayList<Neo4jProperty> properties) {
		this.properties = properties;
	}

	public String getPropertyValue2(String propertyName) {
		for (int i = 0; i < properties.size(); i++) {
			if (propertyName.equals(properties.get(i).getPropertyName())) {
				return properties.get(i).getPropertyValue(0);
			}
		}
		return "";
	}

	public ArrayList<String> getPropertyValue(String propertyName) {
		for (int i = 0; i < properties.size(); i++) {
			if (propertyName.equals(properties.get(i).getPropertyName())) {
				return properties.get(i).getPropertyValue();
			}
		}
		return null;
	}

	// M�todo para fun��es futuras
	public boolean findPropertyCollision(String propertyName) {
		for (int i = 0; i < properties.size(); i++) {
			if (properties.get(i).getPropertyName().equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

}
