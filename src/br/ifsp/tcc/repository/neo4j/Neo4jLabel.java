package br.ifsp.tcc.repository.neo4j;

import org.neo4j.graphdb.Label;

public class Neo4jLabel implements Label {

	// A classe Neo4jLabel tem o objetivo de permitir a utilização do método
	// findNode() que recebe um objeto que implementa a interface Label, e duas
	// Strings, que são o nome da propriedade e seu valor respectivamente.
	// Como todos os valores literais são armazenados no banco Neo4j como um
	// "array" (para que possam armazenar mais de um valor numa mesma
	// propriedade), até onde foi pesquisado, o método findNode() não
	// possibilita a busca por esses dados como "array", o que inviabiliza seu
	// uso para o propósito desse projeto e acabou por não ser usado, sendo esta
	// classe não utilizada no momento.

	private String labelName;

	public Neo4jLabel(String labelName) {
		this.labelName = labelName;
	}

	@Override
	public String name() {
		return labelName;
	}
}
