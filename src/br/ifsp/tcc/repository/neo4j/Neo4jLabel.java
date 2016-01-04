package br.ifsp.tcc.repository.neo4j;

import org.neo4j.graphdb.Label;

public class Neo4jLabel implements Label {

	// A classe Neo4jLabel tem o objetivo de permitir a utiliza��o do m�todo
	// findNode() que recebe um objeto que implementa a interface Label, e duas
	// Strings, que s�o o nome da propriedade e seu valor respectivamente.
	// Como todos os valores literais s�o armazenados no banco Neo4j como um
	// "array" (para que possam armazenar mais de um valor numa mesma
	// propriedade), at� onde foi pesquisado, o m�todo findNode() n�o
	// possibilita a busca por esses dados como "array", o que inviabiliza seu
	// uso para o prop�sito desse projeto e acabou por n�o ser usado, sendo esta
	// classe n�o utilizada no momento.

	private String labelName;

	public Neo4jLabel(String labelName) {
		this.labelName = labelName;
	}

	@Override
	public String name() {
		return labelName;
	}
}
