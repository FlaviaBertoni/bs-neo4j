package br.ifsp.tcc.repository.neo4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.kernel.impl.core.NodeProxy;

import br.ifsp.tcc.entities.Neo4jNode;
import br.ifsp.tcc.entities.Neo4jProperty;
import br.ifsp.tcc.entities.Neo4jRelationship;
import br.ifsp.tcc.entities.SPARQLPredicate;
import br.ifsp.tcc.entities.SPARQLTriple;;

public class Neo4jRepository {

	// A classe Neo4jRepository tem o propósito de inserir dados no banco Neo4j,
	// seja processando parte deles, ou requisitando a outra(s) classe(s) que
	// processe os dados no todo ou parte.

	private static GraphDatabaseService graphDatabaseService;
	static {
		graphDatabaseService = neo4jFactory("C:\\Users\\Flavia\\Documents\\Eclipse - Projetos\\workspace_TCC\\TCC\\WebContent\\resources\\neo4j"); 
	}

	public ArrayList<SPARQLTriple> localSearch(String propertyName, String search) {
		return searchToTriple(propertyName, search, null);
	}

	public ArrayList<SPARQLTriple> localSearch(String propertyName, String search, String endpointURI) {
		return searchToTriple(propertyName, search, endpointURI);
	}

	public Neo4jNode uRIToNode(String uRI) {
		try {
			// Criação do label para que a função findNode() (da API Neo4j)
			// possa ser utilizada:
			Neo4jLabel label = new Neo4jLabel("URI");
			// Array do retorno do método:
			// ArrayList<Neo4jNode> nodeList = new ArrayList<Neo4jNode>();

			// Objeto auxiliar para popular o Array de retorno:
			Neo4jNode node = new Neo4jNode();

			// Array que armazena todas as propriedades de uma node:URI
			ArrayList<Neo4jProperty> propertyList = new ArrayList<Neo4jProperty>();

			// Objeto auxiliar para popular o Array de propriedades:
			Neo4jProperty property = new Neo4jProperty();

			try (Transaction tx = graphDatabaseService.beginTx()) {

				// Para cada URI:
				//for (int i = 0; i < uRIs.size(); i++) {

					// Garantindo que estamos manipulando uma nova referência:
					node = new Neo4jNode();

					// Garantindo que estamos manipulando uma nova referência:
					// propertyList = new ArrayList<Neo4jProperty>();

					// Buscando o nó no banco Neo4j
					org.neo4j.graphdb.Node nodeDB = graphDatabaseService.findNode(label, "uri", uRI);

					// Obtendo uma lista das Keys do nó do banco Neo4j:
					Iterable<String> keyList = nodeDB.getPropertyKeys();

					// Adicionando a URI do nó no objeto que será inserido no
					// Array de retorno:
					node.setURI(uRI);

					// Para cada key do nó do banco Neo4j:
					for (String key : keyList) {
						// Garantindo que estamos manipulando uma nova
						// referência:
						property = new Neo4jProperty();
						// Obtendo o valor da key em questão:
						Object object = nodeDB.getProperty(key);

						// Adicionando o nome da propriedade na estrutura de
						// propriedades, que por sua vez será inserido no Node,
						// que será inserido no Array de retorno do método:
						property.setPropertyName(key);

						if (object.getClass() == String.class) {
							String rowS = object.toString();
							property.setAllPropertyValues(rowS);

						} else if (object.getClass() == String[].class) {
							String[] rowS = (String[]) object;
							String allInOne = "";
							for (int j = 0; j < rowS.length; j++) {
								if (j < rowS.length - 1) {
									allInOne += rowS[j] + ", ";
								} else
									allInOne += rowS[j];
							}
							property.setAllPropertyValues(allInOne);

						} else if (object.getClass() == NodeProxy.class) {
							// Long id = ((org.neo4j.graphdb.Node)
							// object).getId();
							// System.out.println(key + ": " + id);
						}
						propertyList.add(property);
					}
					node.setProperties(propertyList);
					//nodeList.add(node);
				//}
				tx.success();
				System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.uRIToNode]\n");
				return node;
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.uRIToNode]\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.uRIToNode]\n");
		}
		return null;
	}

	public ArrayList<SPARQLTriple> searchToTriple(String propertyName, String search, String endpointURI) {

		// Array que armazena o resultado:
		ArrayList<SPARQLTriple> triples = new ArrayList<SPARQLTriple>();

		SPARQLTriple triple = new SPARQLTriple();
		try {
			System.out.println("[INFO     ] Trying to search for " + propertyName + ": %" + search
					+ "%, as a single value property.");
			
			String querySingle;
			
			if (endpointURI == null) {
				querySingle = CypherParser.cypherSimpleSingleSearchStructureBuilder(propertyName, search);
			} else {
				querySingle = CypherParser.cypherSimpleSingleSearchStructureBuilder(propertyName, search, endpointURI);
			}
			System.err.println(querySingle);
			Result result = graphDatabaseService.execute(querySingle);
			while (result.hasNext()) {
				Map<String, Object> row = result.next();
				// O sujeito sempre será a URI em questão
				triple = new SPARQLTriple();
				triple.setSujeito(row.get("uri").toString());
				for (String key : result.columns()) {

					// O predicado sempre será a key em questão, mas
					// como ela não mudará nesse caso, é utilizado o
					// propertyName informado:
					triple.setPredicado(propertyName);

					triple.setObjeto(row.get(key).toString());
					// System.out.printf("%s = %s%n\n", key, row.get(key));

				}
				triples.add(triple);

			}

		} catch (Exception e) {
			// String queryMulti = "MATCH (n:URI) WHERE ANY(brandName IN
			// n.brandName WHERE brandName =~ '(?i).*oRph.*') RETURN n.uri AS
			// uri, n.brandName AS brandName, n";
			String queryMulti;
			if (endpointURI == null) {
				queryMulti = CypherParser.cypherSimpleMultiSearchStructureBuilder(propertyName, search);
			} else {
				queryMulti = CypherParser.cypherSimpleMultiSearchStructureBuilder(propertyName, search, endpointURI);
			}
			try {
				System.out.println("[INFO     ] Seems the value is a Vector, trying to search for " + propertyName
						+ ": %" + search + "%, as a multiple value property.");
				Result result = graphDatabaseService.execute(queryMulti);
				while (result.hasNext()) {
					Map<String, Object> row = result.next();
					// O sujeito sempre será a URI em questão
					triple = new SPARQLTriple();
					triple.setSujeito(row.get("uri").toString());

					for (String key : result.columns()) {

						Object object = row.get(key);

						if (object.getClass() == String.class) {
							String rowS = object.toString();
							// System.out.println(key + ": " + rowS);

							// O predicado sempre será a key em questão
							triple.setPredicado(key);

							triple.setObjeto(rowS);
							// System.out.printf("%s = %s%n\n", key,
							// row.get(key));

						} else if (object.getClass() == String[].class) {
							String[] rowS = (String[]) object;
							String allInOne = "";
							for (int i = 0; i < rowS.length; i++) {
								if (i < rowS.length - 1) {
									allInOne += rowS[i] + ", ";
								} else
									allInOne += rowS[i];
							}

							// O predicado sempre será a key em questão, mas
							// como ela não mudará nesse caso, é utilizado o
							// propertyName informado:
							triple.setPredicado(propertyName);

							triple.setObjeto(allInOne);
							// System.out.printf("%s = %s%n\n", key,
							// row.get(key));

							// System.out.println(key + ": " + allInOne);
						} else if (object.getClass() == NodeProxy.class) {
							Long id = ((org.neo4j.graphdb.Node) object).getId();

							System.out.println(key + ": " + id);

						}
					}
					triples.add(triple);
				}
				return triples;
			} catch (Exception e2) {
				e.printStackTrace();
				System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.searchToTriple]\n");
			}
		}
		System.err.println("Número de triplas encontradas: " +triples.size());
		return triples;
	}

	public void neo4jExampleWorkingAndHandleVectors() {

		String searchPropertyNameValue = "brandName";
		String searchValue = "oRpH";

		// String querySingle = "MATCH (n:URI) WHERE n.label =~ '.*(?i)OrPh.*'
		// RETURN n.uri AS uri, n.label AS label";
		String querySingle = CypherParser.cypherSimpleSingleSearchStructureBuilder(searchPropertyNameValue, searchValue);

		try {
			Result result = graphDatabaseService.execute(querySingle);
			while (result.hasNext()) {
				Map<String, Object> row = result.next();
				for (String key : result.columns()) {

					System.out.printf("%s = %s%n\n", key, row.get(key));
				}
			}
		} catch (Exception e) {
			// String queryMulti = "MATCH (n:URI) WHERE ANY(brandName IN
			// n.brandName WHERE brandName =~ '(?i).*oRph.*') RETURN n.uri AS
			// uri, n.brandName AS brandName, n";
			String queryMulti = CypherParser.cypherSimpleMultiSearchStructureBuilder(searchPropertyNameValue, searchValue);
			try {
				Result result = graphDatabaseService.execute(queryMulti);
				while (result.hasNext()) {
					Map<String, Object> row = result.next();
					for (String key : result.columns()) {

						Object object = row.get(key);

						if (object.getClass() == String.class) {
							String rowS = object.toString();
							System.out.println(key + ": " + rowS);
						} else if (object.getClass() == String[].class) {
							String[] rowS = (String[]) object;
							String allInOne = "";
							for (int i = 0; i < rowS.length; i++) {
								if (i < rowS.length - 1) {
									allInOne += rowS[i] + ", ";
								} else
									allInOne += rowS[i];
							}
							System.out.println(key + ": " + allInOne);
						} else if (object.getClass() == NodeProxy.class) {
							Long id = ((org.neo4j.graphdb.Node) object).getId();

							System.out.println(key + ": " + id);

						}
					}
				}
			} catch (Exception e2) {
				e.printStackTrace();

				System.err.println("EXCEPTION GERAL");
			}

		}

	}

	public void neo4jExampleWorkingButDontHandleVectors() {

		String querySingle = "MATCH (n:URI) WHERE n.brandName =~ '.*(?i)OrPh.*' RETURN n.uri, n.brandName";

		try {
			Result result = graphDatabaseService.execute(querySingle);
			while (result.hasNext()) {
				Map<String, Object> row = result.next();
				for (String key : result.columns()) {

					System.out.printf("%s = %s%n\n", key, row.get(key));
				}
			}
		} catch (Exception e) {
			String queryMulti = "MATCH (n:URI) WHERE ANY(brandName IN n.brandName WHERE brandName =~ '(?i).*a.*') RETURN n.uri, n.brandName, n";

			try {
				Result result = graphDatabaseService.execute(queryMulti);
				while (result.hasNext()) {
					Map<String, Object> row = result.next();
					for (String key : result.columns()) {

						System.out.printf("%s = %s%n\n", key, row.get(key));
					}
				}
			} catch (Exception e2) {
			}

		}

	}

	public ArrayList<Neo4jNode> retrieveNodes(ArrayList<String> uRIs) {
		try (Transaction tx = graphDatabaseService.beginTx()) {

			// Array que conterá o retorno do método
			ArrayList<Neo4jNode> nodeList = new ArrayList<Neo4jNode>();

			// Para cada URI recebida:
			for (int i = 0; i < uRIs.size(); i++) {

				// Criando o label necessário para o método findNode()
				Neo4jLabel label = new Neo4jLabel("URI");

				// Buscando o respectivo nó no banco Neo4j
				org.neo4j.graphdb.Node nodeOfDB = graphDatabaseService.findNode(label, "uri", uRIs.get(i));

				// Recuperando as keys (nome das propriedades):
				Iterable<String> key = nodeOfDB.getPropertyKeys();

				// Objeto auxiliar para a população do Array de retorno do
				// método
				Neo4jNode node = new Neo4jNode();
				Neo4jProperty property = new Neo4jProperty();
				ArrayList<Neo4jProperty> properties = new ArrayList<Neo4jProperty>();

				// Para cada key do nó do banco Neo4j:
				for (String keyValue : key) {
					// Recuperando o valor ou valores da propriedade:
					String[] value = (String[]) nodeOfDB.getProperty(keyValue);

					// Definindo o nome da propriedade na estrutura:
					property.setPropertyName(keyValue);

					// Para cada valor de uma propriedade do nó do banco Neo4j:
					for (int j = 0; j < value.length; j++) {
						// Adicionando os valores do banco Neo4j na estrutura
						// Node:
						property.setPropertyValue(value[i]);
					}
					properties.add(property);
				}
				node.setURI(uRIs.get(i));
				node.setProperties(properties);
				nodeList.add(node);
			}
			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.retrieveNodes]\n");
			return nodeList;
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.retrieveNodes]\n");
		}
		return null;
	}

	public ArrayList<SPARQLPredicate> retrieveEndpointData(String endpointURL) {

		System.out.println("[INFO     ] Retrieving the node:ENDPOINT with the URI <" + endpointURL + ">.");
		try (Transaction tx = graphDatabaseService.beginTx()) {

			Neo4jLabel label = new Neo4jLabel("ENDPOINT");
			org.neo4j.graphdb.Node nodeOfDB = graphDatabaseService.findNode(label, "uri", endpointURL);
			ArrayList<SPARQLPredicate> predicateList = new ArrayList<SPARQLPredicate>();
			// String nullValue = "[^_.-~valueNull~-._^]";

			String[] predicateURI = (String[]) nodeOfDB.getProperty("predicateURL");
			String[] localName = (String[]) nodeOfDB.getProperty("localName");

			for (int i = 0; i < predicateURI.length; i++) {
				SPARQLPredicate predicate = new SPARQLPredicate();
				predicate.setUri(predicateURI[i]);
				predicate.setLocalName(localName[i]);
				predicateList.add(predicate);
			}

			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.retrieveEndpointData]\n");
			return predicateList;
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.retrieveEndpointData]\n");
		}

		return null;
	}

	public ArrayList<SPARQLPredicate> retrieveAllEndpointsData() {

		System.out.println("[INFO     ] Retrieving all the node:ENDPOINT.");
		try (Transaction tx = graphDatabaseService.beginTx()) {

			Neo4jLabel label = new Neo4jLabel("ENDPOINT");
			ResourceIterator<org.neo4j.graphdb.Node> nodesOfDB = graphDatabaseService.findNodes(label);

			ArrayList<SPARQLPredicate> predicateList = new ArrayList<SPARQLPredicate>();
			// String nullValue = "[^_.-~valueNull~-._^]";

			while (nodesOfDB.hasNext()) {
				org.neo4j.graphdb.Node node = nodesOfDB.next();
				String[] predicateURI = (String[]) node.getProperty("predicateURL");
				String[] localName = (String[]) node.getProperty("localName");

				for (int i = 0; i < predicateURI.length; i++) {
					SPARQLPredicate predicate = new SPARQLPredicate();
					predicate.setUri(predicateURI[i]);
					predicate.setLocalName(localName[i]);
					predicateList.add(predicate);
				}
			}

			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.retrieveEndpointData]\n");
			return predicateList;
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.retrieveEndpointData]\n");
		}

		return null;
	}
	
	public void indexEndpoint(ArrayList<SPARQLPredicate> predicates, String endpointURL) {
		try (Transaction tx = graphDatabaseService.beginTx()) {
			graphDatabaseService.execute(CypherParser.cypherIndexEndpointStructureBuilder(predicates, endpointURL));
			tx.success();
			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.indexEndpoint]\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.indexEndpoint]\n");
		}
	}

	public Neo4jRepository() {
		// Para inicializar um Neo4jRepository, é necessário fornecer uma
		// localização para a criação ou carregamento da base de dados Neo4j.
	}

	public void updateURIFactory(ArrayList<Neo4jNode> nodeList) {
		// Método não desenvolvido
	}

	public void nodesURIFactory(ArrayList<Neo4jNode> nodeList) {

		// O método nodesURIFactory recebe um array contendo vários nós que por
		// sua vez possuem todos suas propriedades e cada uma seu respectivo
		// valor, a cada interação do laço for, um nó é criado no banco Neo4j.

		for (int i = 0; i < nodeList.size(); i++) {

			String query = "";
			System.out.println("[INFO     ] Creating the node that has the URI <" + nodeList.get(i).getURI() + ">");
			query = CypherParser.cypherCreateStructureBuilder(nodeList.get(i).getProperties());
			try (Transaction tx = graphDatabaseService.beginTx()) {
				graphDatabaseService.execute(query);
				tx.success();
				System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.nodesURIFactory]\n");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.nodesURIFactory]\n");
			}
		}
	}

	public void relationshipFactory(ArrayList<Neo4jRelationship> relationships) {
		// O método relationshipFactory recebe uma estrutura contendo todos os
		// relacionamentos identificados no decorrer de uma consulta.

		String[] query = CypherParser.cypherCreateRelationshipStructureBuilder(relationships);
		for (int i = 0; i < relationships.size(); i++) {
			try (Transaction tx = graphDatabaseService.beginTx()) {
				graphDatabaseService.execute(query[i]);
				tx.success();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.nodesRelationshipFactory]\n");
			}
		}
		System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.nodesRelationshipFactory]\n");
	}

	private static GraphDatabaseService neo4jFactory(String directory) {

		// Cria uma instância do banco Neo4j no diretório informado, se já
		// houver um banco no diretório, então o mesmo é carregado.
		try {
			graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(directory);

			// this.graphDatabaseService = new
			// GraphDatabaseFactory().newEmbeddedDatabaseBuilder(directory)
			// .setConfig(GraphDatabaseSettings.node_auto_indexing,
			// "true").newGraphDatabase();

			// GraphDatabaseService graphDatabaseService = new
			// GraphDatabaseFactory().newEmbeddedDatabaseBuilder(directory)
			// .loadPropertiesFromFile(directory +
			// "\\neo4j.properties").newGraphDatabase();

			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.neo4jFactory]\n");
			return graphDatabaseService;
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.neo4jFactory]\n");
			return null;
		}
	}

	public boolean consistencyCheck(Neo4jNode node) {

		// Propósito: Comparar as propriedades e respectivos valores de um novo
		// nó (e.g. "br.ifsp.Node") com o nó antigo (e.g."org.neo4j.Node") já
		// inserido no banco Neo4j.
		Neo4jLabel label = new Neo4jLabel("URI");
		// String defaultValue = "#{-^|^- Value not found -^|^-}#";
		boolean upToDate = true;

		try (Transaction tx = graphDatabaseService.beginTx()) {

			// Recuperando o nó do banco Neo4j que possui a mesma URI que o nó a
			// ser comparado:
			org.neo4j.graphdb.Node nodeOfDB = graphDatabaseService.findNode(label, "uri", node.getURI());
			// Extraindo todas as keys (nome das propriedades) do nó recuperado
			// do banco Neo4j:
			Iterable<String> propertyKeys = nodeOfDB.getPropertyKeys();

			// Movendo os valores do Iterable para um ArrayList do tipo String
			ArrayList<String> propertyKeysList = new ArrayList<String>();
			for (String key : propertyKeys) {
				propertyKeysList.add(key);
			}

			//
			if (node.getProperties().size() == propertyKeysList.size()) {
				for (int i = 0; i < node.getProperties().size(); i++) {
					if (!node.findPropertyCollision(propertyKeysList.get(i))) {
						upToDate = false;
					}
				}
			} else {
				System.err.println("New property size: " + node.getProperties().size() + "   |   Old property size: "
						+ propertyKeysList.size());
				System.err.println("TAMANHOS DIFERENTES!");
			}

			if (upToDate) {

			} else {
				System.err.println("The node keys aren't up to date!");
			}
			tx.success();
			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.consistencyCheck]\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.consistencyCheck]\n");
		}
		return false;
	}

	public int checkURI(String uRI, String labelName) {
		System.out.println("[INFO     ] Checking if a node:" + labelName + " with the URI <" + uRI + "> exists.");
		try (Transaction tx = graphDatabaseService.beginTx()) {

			Neo4jLabel label = new Neo4jLabel(labelName);
			org.neo4j.graphdb.Node nodeOfDB = graphDatabaseService.findNode(label, "uri", uRI);

			if (nodeOfDB == null) {
				System.out.println("[INFO     ] The URI <" + uRI + "> don't exists, so it'll be created.");
				System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.checkURI]\n");
				tx.success();
				// Zero (0) significa FALSE
				return 0;
			} else {
				// Se houver um resultado, então a URI está inserida no banco.

				System.out.println("[INFO     ] The URI <" + uRI + "> exists, so it don't need to be created.");
				System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.checkURI]\n");
				tx.success();
				// Um (1) significa TRUE
				return 1;
			}
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.checkURI]\n");
		}
		// Menos um (-1) significa nem verdadeiro ou falso, mas uma "EXCEPTION"
		return -1;
	}

	public int checkURI_OLD(String uRI) {

		// ESSE MÉTODO FOI UMA SOLUÇÃO ALTERNATIVA PARA QUANDO TODAS AS
		// PROPRIEDADES ERAM INSERIDAS COMO ARRAYS NO BANCO NEO4J, OU SEJA,
		// ENTRE COLCHETES

		// O método checkURI recebe uma String e verifica se a mesma está
		// inserida no banco Neo4j (como o valor de uma propriedade "uri" de um
		// nó que possua o label :URI).
		// String defaultValue = "#{-^|^- Value not found -^|^-}#";

		try (Transaction tx = graphDatabaseService.beginTx()) {

			// O código Cypher a ser executado, busca pela uRI informada, e caso
			// encontre, retorna o nó encontrado. Embora a estrutura dessa query
			// permita o retorno de mais nós que possuam a mesma uRI, isso não
			// ocorrerá, uma vez que há tratamento para que não haja a inserção
			// de um nó (identificado (abstratamente falando)unicamente por sua
			// URI) no banco Neo4j.
			String query = " MATCH (node:URI{uri:'" + uRI + "'}) RETURN node";
			Result nodeOfDB = graphDatabaseService.execute(query);

			// Se não houver nenhum resultado, então a URI não está inserida no
			// banco.
			if (nodeOfDB.hasNext() == false) {
				System.out.println("URI: <" + uRI + "> don't exists.");
				System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.checkURI]\n");
				tx.success();
				// Zero (0) significa FALSE
				return 0;
			} else {
				// Se houver um resultado, então a URI está inserida no banco.
				System.out.println("URI: <" + uRI + "> exists.");
				System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.checkURI]\n");
				tx.success();
				// Um (1) significa TRUE
				return 1;
			}
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.checkURI]");
		}
		// Menos um (-1) significa nem verdadeiro ou falso, mas uma "EXCEPTION"
		return -1;
	}

	public long findURIID(String uri) {

		// O método findURIID recebe uma String e verifica se a mesma está
		// inserida no banco de dados, caso a encontre, retorna o ID do nó
		// utilizando a função do Neo4j ID(). Essa função é utilizada para
		// contornar o "problema" encontrado com o método findNode(), onde os
		// colchetes não podem ser utilizados e nenhum resultado é retornado
		// (mesmo se existente, isso devido a ausência dos colchetes que são
		// necessários por causa da propriedade se tratar de um "array" de
		// valores)
		try {

			String query = "MATCH (node:URI{uri:'" + uri + "'}) RETURN ID(node)";
			Result result = graphDatabaseService.execute(query);
			while (result.hasNext()) {

				Map<String, Object> row = result.next();
				for (Entry<String, Object> column : row.entrySet()) {

					if (column.getKey().equals("ID(node)")) {
						System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.findURIID]\n");
						return Long.parseLong(column.getValue().toString());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.findURIID]\n");
		}
		// Menos um (-1) não significa um ID, mas sim uma "EXCEPTION"
		return -1;
	}

	public void dropDB(String directory) {

		// Apaga um diretório informado, note que não apaga um banco Neo4j, mas
		// sim o diretório informado, independentemente de haver ou não um banco
		// Neo4j.
		try {
			FileUtils.deleteRecursively(new File(directory));
			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.dropDB]\n");
		} catch (IOException ex) {
			Logger.getLogger(Neo4jRepository.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.dropDB]\n");
		}
	}

	public void shutdownDB() {

		// "Desliga" o banco Neo4.
		try {
			graphDatabaseService.shutdown();
			//registerShutdownHook();
			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.shutdownDB]\n");
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.shutdownDB]\n");
		}
	}

	public void registerShutdownHook() {

		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDatabaseService.shutdown();
			}
		});
	}
	
	public ArrayList<String> findEndpointsURIs(){
		try {
			ArrayList<String> uRIsFound = new ArrayList<String>();
			String query = "MATCH (node:ENDPOINT) RETURN node.uri as uri";
			Result result = graphDatabaseService.execute(query);
			
			while (result.hasNext()) {
				Map<String, Object> row = result.next();
				for (Entry<String, Object> column : row.entrySet()) {
					if (column.getKey().equals("uri")) {
						uRIsFound.add(column.getValue().toString());
					}
				}
			}
			System.out.println("[SUCESS   ] [br.ifsp.neo4j.repository.findEndpointsURIs]\n");
			return uRIsFound;
		} catch (Exception e) {
			System.err.println("[EXCEPTION] [br.ifsp.neo4j.repository.findEndpointsURIs]\n");
		}
		return null;
	}
}