package br.ifsp.tcc.repository.neo4j;

import java.util.ArrayList;

import br.ifsp.tcc.entities.Neo4jProperty;
import br.ifsp.tcc.entities.Neo4jRelationship;
import br.ifsp.tcc.entities.SPARQLPredicate;

public class CypherParser {

	// A classe Parser tem como propósito receber estruturas que representam nós
	// a serem criados ou atualizados no banco Neoj4. O retorno sempre será uma
	// String (ou String[] no caso dos relacionamentos) de código Cypher para
	// ser executado no banco Neo4j.

	public static String cypherSimpleSingleSearchStructureBuilder(String propertyName, String search,
			String endpointURI) {
		try {
			String query = "MATCH (n:URI)-[belongsTo]->(m:ENDPOINT) WHERE n." + propertyName + " =~ \".*(?i)" + search
					+ ".*\" AND m.uri = \"" + endpointURI + "\"RETURN n.uri AS uri, n." + propertyName + " AS "
					+ propertyName;

			// MATCH (n:URI)-[belongsTo]->(m:ENDPOINT)
			// WHERE n.label = "Morphine"
			// AND m.uri =
			// "http://wifo5-04.informatik.uni-mannheim.de/drugbank/sparql"
			// RETURN n.uri AS uri, n.label AS label

			// MATCH (n:URI)
			// WHERE n.label =~ '.*(?i)OrPh.*'
			// RETURN n
			System.err.println(query);
			System.out.println("[SUCESS   ] [br.ifsp.parser.repository.cypherSimpleSingleSearchStructureBuilder");
			return query;
		} catch (Exception e) {
			System.out.println("[EXCEPTION] [br.ifsp.parser.repository.cypherSimpleSingleSearchStructureBuilder");
			return null;
		}
	}
	
	public static String cypherSimpleSingleSearchStructureBuilder(String propertyName, String search) {
		try {
			String query = "MATCH (n:URI) WHERE n." + propertyName + " =~ \".*(?i)" + search
					+ ".*\" RETURN n.uri AS uri, n." + propertyName + " AS " + propertyName;

			// MATCH (n:URI)
			// WHERE n.label =~ '.*(?i)OrPh.*'
			// RETURN n
			System.err.println(query);
			System.out.println("[SUCESS   ] [br.ifsp.parser.repository.cypherSimpleSingleSearchStructureBuilder");
			return query;
		} catch (Exception e) {
			System.out.println("[EXCEPTION] [br.ifsp.parser.repository.cypherSimpleSingleSearchStructureBuilder");
			return null;
		}
	}

	public static String cypherSimpleMultiSearchStructureBuilder(String propertyName, String search,
			String endpointURI) {
		try {
			String query = "MATCH (n:URI)-[belongsTo]->(m:ENDPOINT) WHERE ANY(" + propertyName + " IN n." + propertyName
					+ " WHERE " + propertyName + " =~ \".*(?i)" + search + ".*\") AND m.uri=\"" + endpointURI
					+ "\" RETURN n.uri AS uri, n." + propertyName + " AS " + propertyName;

			// MATCH (n:URI)-[belongsTo]->(m:ENDPOINT)
			// WHERE ANY(brandName
			// IN n.brandName
			// WHERE brandName =~ '(?i).*Morphium.*') AND
			// m.uri="http://wifo5-04.informatik.uni-mannheim.de/drugbank/sparql"
			// RETURN n.uri, n.brandName

			System.err.println(query);
			System.out.println("[SUCESS   ] [br.ifsp.parser.repository.cypherSimpleMultiSearchStructureBuilder");
			return query;
		} catch (Exception e) {
			System.out.println("[EXCEPTION] [br.ifsp.parser.repository.cypherSimpleMultiSearchStructureBuilder");
			return null;
		}
	}
	
	public static String cypherSimpleMultiSearchStructureBuilder(String propertyName, String search) {
		try {
			String query = "MATCH (n:URI) WHERE ANY(" + propertyName + " IN n." + propertyName + " WHERE "
					+ propertyName + " =~ \".*(?i)" + search + ".*\") RETURN n.uri AS uri, n." + propertyName + " AS "
					+ propertyName;

			// MATCH (n:URI)
			// WHERE ANY(brandName
			// IN n.brandName
			// WHERE brandName =~ '(?i).*Morphium.*')
			// RETURN n.uri, n.brandName
			System.err.println(query);
			System.out.println("[SUCESS   ] [br.ifsp.parser.repository.cypherSimpleMultiSearchStructureBuilder");
			return query;
		} catch (Exception e) {
			System.out.println("[EXCEPTION] [br.ifsp.parser.repository.cypherSimpleMultiSearchStructureBuilder");
			return null;
		}
	}

	public static String cypherIndexEndpointStructureBuilder(ArrayList<SPARQLPredicate> predicates,
			String endpointURL) {
		try {
			String predicateURLFragment = "";
			String localNameFragment = "";

			// Para cada Predicate:
			for (int i = 0; i < predicates.size(); i++) {

				// Se o Predicate não for o último da lista, coloque vírgula no
				// final:
				if (i < predicates.size() - 1) {
					predicateURLFragment += "\"" + predicates.get(i).getUri() + "\", ";
					localNameFragment += "\"" + predicates.get(i).getLocalName() + "\", ";

				} else {
					// Se o Predicate for o último da lista, não coloque vírgula
					// no final:
					predicateURLFragment += "\"" + predicates.get(i).getUri() + "\"";
					localNameFragment += "\"" + predicates.get(i).getLocalName() + "\"";
				}
			}
			String cypherQuery = " CREATE(:ENDPOINT{uri:\"" + endpointURL + "\", predicateURL: [" + predicateURLFragment
					+ "], localName: [" + localNameFragment + "]})";

			System.err.println("[INFO     ] " + cypherQuery);
			System.out.println("[SUCESS   ] [br.ifsp.parser.repository.cypherCreateStructureBuilder");
			return cypherQuery;
		} catch (Exception e) {
			System.out.println("[EXCEPTION] [br.ifsp.parser.repository.cypherCreateStructureBuilder");
			return null;
		}

	}

	public static String[] cypherCreateRelationshipStructureBuilder(ArrayList<Neo4jRelationship> relationships) {
		try {
			String[] query = new String[relationships.size()];

			for (int i = 0; i < relationships.size(); i++) {
				// Impressão da representação do relacionamento que será
				// montado, utilizada apenas para controle durante o
				// desenvolvimento.
				System.out.println("[INFO     ] Creating the relationship: (" + relationships.get(i).getStartNode()
						+ ") -[r:" + relationships.get(i).getRelationshipName() + "]-> ("
						+ relationships.get(i).getEndNode() + ").");

				// Construção do código Cypher utilizando os parâmetros
				// recebidos pela variável do laço for each.
				query[i] = " MATCH (a), (b) WHERE a.uri = '" + relationships.get(i).getStartNode() + "' AND b.uri = '"
						+ relationships.get(i).getEndNode() + "' " + " MERGE (a)-[r:"
						+ relationships.get(i).getRelationshipName() + "]->(b)";
			}

			for (@SuppressWarnings("unused") Neo4jRelationship relationship : relationships) {
			}
			System.out.println("[SUCESS   ] [br.ifsp.parser.repository.cypherRelationshipCreateStructureBuilder");
			return query;
		} catch (Exception e) {
			System.out.println("[EXCEPTION] [br.ifsp.parser.repository.cypherRelationshipCreateStructureBuilder");
		}
		return null;

	}

	public static String cypherCreateStructureBuilder(ArrayList<Neo4jProperty> properties) {
		try {
			String cypherQueryFragment = " CREATE(:URI{";

			// Para cada Property:
			for (int i = 0; i < properties.size(); i++) {

				// e.g. "uri: "
				cypherQueryFragment += properties.get(i).getPropertyName() + ": ";

				// Se o valor da propriedade for único (não multivalorado):
				if (properties.get(i).getPropertyValue().size() == 1) {
					// Se essa não for a última propriedade, coloque virgula:
					if (i < properties.size() - 1) {
						cypherQueryFragment += "\"" + properties.get(i).getPropertyValue(0) + "\",";
					} else {
						// Se for a última propriedade, não coloque virgula,
						// feche as chaves e parenteses:
						cypherQueryFragment += "\"" + properties.get(i).getPropertyValue(0) + "\"})";
					}

				} else {

					// Se o valor da propriedade for multiplo:
					if (properties.get(i).getPropertyValue().size() > 1) {

						// Adiciona o caracter de sequência de multi-valores:
						cypherQueryFragment += "[";

						// Para cada valor do array de valores:
						for (int j = 0; j < properties.get(i).getPropertyValue().size(); j++) {

							// Se esse valor não for o último, coloque vírgula
							// no final:
							if (j < properties.get(i).getPropertyValue().size() - 1) {
								cypherQueryFragment += "\"" + properties.get(i).getPropertyValue(j) + "\", ";
							} else {
								// Se esse valor for o último, coloque colchetes
								// no final:

								// Se essa propriedade não for a última, coloque
								// vírgula no final, mas não feche as chaves e
								// parenteses:
								if (i < properties.size() - 1) {
									cypherQueryFragment += "\"" + properties.get(i).getPropertyValue(j) + "\"],";
								} else {
									// Se esse valor for o último, não coloque a
									// vírgula no final, mas feche as chaves e
									// parentes:
									cypherQueryFragment += "\"" + properties.get(i).getPropertyValue(j) + "\"]})";
								}
							}
						}
					}
				}
			}
			System.err.println(cypherQueryFragment);
			return cypherQueryFragment;
		} catch (Exception e) {
			System.out.println("[EXCEPTION] [br.ifsp.parser.repository.cypherCreateStructureBuilder");
			return null;
		}
	}

	public static String cypherUpdateStructureBuilder(String uRI, ArrayList<Neo4jProperty> properties) {

		// [MÉTODO NÃO FINALIZADO]
		// O método cypherUpdateStructureBuilder recebe uma URI para realizar a
		// identificação do nó, e um array de Property, que conterá somente as
		// propriedades que devem ser atualizadas. O retorno é uma String
		// contendo o código Cypher necessário para realizar o "update" do nó.
		try {
			String cypherQueryFragment = " MATCH (n:URI {uri: '" + uRI + "'}) SET";
			for (int i = 0; i < properties.size(); i++) {

				if (i < properties.size() - 1) {
					cypherQueryFragment += " n." + properties.get(i).getPropertyName() + " = [";

					for (int j = 0; j < properties.get(i).getPropertyValue().size(); j++) {
						if (j < properties.get(i).getPropertyValue().size() - 1) {
							cypherQueryFragment += " '" + properties.get(i).getPropertyValue() + "', ";
						} else
							cypherQueryFragment += properties.get(i).getPropertyValue() + "'], ";
					}

				} else {
					cypherQueryFragment += " n." + properties.get(i).getPropertyName() + " = [";
					for (int j = 0; j < properties.get(i).getPropertyValue().size(); j++) {
						if (j < properties.get(i).getPropertyValue().size() - 1) {
							cypherQueryFragment += " '" + properties.get(i).getPropertyValue() + "', ";
						} else
							cypherQueryFragment += properties.get(i).getPropertyValue() + "'] ";
					}

					cypherQueryFragment += " n." + properties.get(i).getPropertyName() + " = '"
							+ properties.get(i).getPropertyValue() + "'";
				}
			}
			System.out.println("\n");
			System.out.println("[SUCESS   ] [br.ifsp.parser.repository.cypherUpdateStructureBuilder");
			return cypherQueryFragment + " }) ";
		} catch (Exception e) {
			System.out.println("[EXCEPTION] [br.ifsp.parser.repository.cypherUpdateStructureBuilder");
			return null;
		}
	}
}
