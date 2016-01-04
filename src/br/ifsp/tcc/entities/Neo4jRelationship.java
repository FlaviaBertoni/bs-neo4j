package br.ifsp.tcc.entities;

public class Neo4jRelationship {

	// A classe Relationship tem o propósito de armazenar os dados de referência
	// para serem utilizados na etapa de criação dos relacionamentos. Um objeto
	// Relationship armazenará a URI do "start node" e também a URI do "end
	// node", bem como o nome do relacionamento. Esses dados são utilizados para
	// gerar o código Cypher que criará os relacionamentos especificados.

	private String startNode;
	private String endNode;
	private String relationshipName;

	public String getStartNode() {
		return startNode;
	}

	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

	public String getEndNode() {
		return endNode;
	}

	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}

	public String getRelationshipName() {
		return relationshipName;
	}

	public void setRelationshipName(String relationshipName) {
		this.relationshipName = relationshipName;
	}
}
