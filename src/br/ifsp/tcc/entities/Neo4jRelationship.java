package br.ifsp.tcc.entities;

public class Neo4jRelationship {

	// A classe Relationship tem o prop�sito de armazenar os dados de refer�ncia
	// para serem utilizados na etapa de cria��o dos relacionamentos. Um objeto
	// Relationship armazenar� a URI do "start node" e tamb�m a URI do "end
	// node", bem como o nome do relacionamento. Esses dados s�o utilizados para
	// gerar o c�digo Cypher que criar� os relacionamentos especificados.

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
