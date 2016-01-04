package br.ifsp.tcc.repository.sparql;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import br.ifsp.tcc.entities.Neo4jNode;
import br.ifsp.tcc.entities.Neo4jProperty;
import br.ifsp.tcc.entities.Neo4jRelationship;
import br.ifsp.tcc.entities.SPARQLPredicate;
import br.ifsp.tcc.entities.SPARQLTriple;
import br.ifsp.tcc.repository.neo4j.Neo4jRepository;

public class SPARQLRepository {
	
	private ArrayList<String> uris = new ArrayList<String>();
	
	private boolean doTheHop = true;
	private int hopCount = 0;
	private final int MAX_HOP_NODES = 10;
	
	public Boolean endpointOn(String endpoint){
		String sparqlQuery = "SELECT ?s WHERE{?s ?p ?o} LIMIT 1";
		try{
            Query query = QueryFactory.create(sparqlQuery);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            ResultSet results = qexec.execSelect();
            if(results!=null){
            	return true;
            }
            qexec.close();
            System.out.println("[SUCESS   ]: Run query SPARQL - Endpoint status.");
            System.out.println("[WARNING  ]: SPARQL Endpoint is available.");
		}catch(Exception e){
        	System.out.println("[EXCEPTION]: Error to run the querySPARQL.");
        	System.out.println("[WARNING  ]: SPARQL Endpoint is unavailable.");
        	printException(e.toString());
        	return false;
        }
        return false;
	}
	
	public ArrayList<SPARQLTriple> buscaSimples(String busca, String endpoint){
		ArrayList<SPARQLTriple> triplas = new ArrayList<SPARQLTriple>();
		
		String sparqlQuery = "SELECT DISTINCT * WHERE {"
				+ " ?s ?p ?o."
				+ " FILTER isLiteral(?o)."
				+ " FILTER (regex(lcase(str(?o)), \""+busca.toLowerCase()+"\"))"
				+ " }"
				+ " LIMIT 300";
		
		System.out.println(sparqlQuery);
        try{
            Query query = QueryFactory.create(sparqlQuery);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            ResultSet results = qexec.execSelect();
            if(!results.hasNext()){return null;}
            
            uris = new ArrayList<String>();
            while(results.hasNext()){
                QuerySolution qs = results.next();
                SPARQLTriple tripla = new SPARQLTriple();
                
                tripla.setSujeito(qs.get("s").toString());
                if(!existsURI(tripla.getSujeito())){
                	uris.add(tripla.getSujeito());
                }
                
                tripla.setPredicado(qs.get("p").asResource().getLocalName());
                tripla.setObjeto(qs.get("o").toString());
                
                triplas.add(tripla);
            }
            qexec.close();
        }catch(Exception e){
        	e.printStackTrace();
        	System.out.println("[EXCEPTION]: Error to run the querySPARQL.");
        	printException(e.toString());
        	return null;
        }
        return triplas;
	}

	public ArrayList<SPARQLPredicate> getEndpointPredicates(String endpoint, Neo4jRepository neo4j){
		int state = neo4j.checkURI(endpoint, "ENDPOINT");
		if (state == 0) {
			String sparqlQuery = "SELECT DISTINCT ?p WHERE{?s ?p ?o} ORDER BY ASC(?p)";
			try{
	            Query query = QueryFactory.create(sparqlQuery);
	            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
	            ResultSet results = qexec.execSelect();
	            ArrayList<SPARQLPredicate> predicates = new ArrayList<SPARQLPredicate>();
	            while(results.hasNext()){
	            	QuerySolution qs = results.next();
	            	SPARQLPredicate predicate = new SPARQLPredicate();
	            	predicate.setUri(qs.get("p").asResource().getURI());
	            	predicate.setLocalName(qs.get("p").asResource().getLocalName());
	            	predicates.add(predicate);
	            }
	            qexec.close();
	            System.out.println("[SUCESS   ]: Run query SPARQL - Predicates.");
	            neo4j.indexEndpoint(predicates, endpoint);
	            return predicates;
			}catch(Exception e){
	        	System.out.println("[EXCEPTION]: Error to run the querySPARQL.");
	        	printException(e.toString());
	        	return null;
	        }
		} else if (state == 1) {
			return neo4j.retrieveEndpointData(endpoint);
		}
		return null;
	}
	
	public ArrayList<SPARQLTriple> advancedSearch(ArrayList<String> sparqlQuery, String endpoint,
			String busca1, String busca2, String busca3,
			String predicado1, String predicado2, String predicado3){
		System.out.println(sparqlQuery.get(0));
		ArrayList<SPARQLTriple> triplas = new ArrayList<SPARQLTriple>();
        try{
            Query query = QueryFactory.create(sparqlQuery.get(0));
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            ResultSet results = qexec.execSelect();
            if(!results.hasNext()){return null;}
            
            uris = new ArrayList<String>();
            System.out.println(sparqlQuery.get(1));
            
            while(results.hasNext()){
                QuerySolution qs = results.next();
                SPARQLTriple result3;
                switch(sparqlQuery.get(1)){
                case "cmp":
                    triplas.add(resultCmp(qs));
                	break;
                case "ee":
                	triplas.add(resultE(qs,predicado1,busca1));
                	triplas.add(resultE(qs,predicado2,busca2));
                	break;
                case "ef":
                	triplas.add(resultE(qs,predicado1,busca1));
                	triplas.add(resultF(qs,predicado2,"o"));
                	break;
                case "fe":
                	triplas.add(resultF(qs,predicado1,"o"));
                	triplas.add(resultE(qs,predicado2,busca2));
                	break;
                case "ff":
                	triplas.add(resultF(qs,predicado1,"o1"));
                	triplas.add(resultF(qs,predicado2,"o2"));
                	break;
                case "e":
                	triplas.add(resultE(qs,predicado1,busca1));
                	break;
                case "f":
                	triplas.add(resultF(qs,predicado1,"o"));
                	break;
                case "eee":
                	triplas.add(resultE(qs,predicado1,busca1));
                	triplas.add(resultE(qs,predicado2,busca2));
                	result3 = resultE(qs,predicado3,busca3);
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "eef":
                	triplas.add(resultE(qs,predicado1,busca1));
                	triplas.add(resultE(qs,predicado2,busca2));
                	result3 = resultF(qs,predicado3,"o3");
                	if(result3!=null){triplas.add(result3);}
                	break;                	
                case "efe":
                	triplas.add(resultE(qs,predicado1,busca1));
                	triplas.add(resultF(qs,predicado2,"o"));
                	result3 = resultE(qs,predicado3,busca3);
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "eff":
                	triplas.add(resultE(qs,predicado1,busca1));
                	triplas.add(resultF(qs,predicado2,"o"));
                	result3 = resultF(qs,predicado3,"o3");
                	if(result3!=null){triplas.add(result3);}
                	
                	break;                	
                case "fee":
                	triplas.add(resultF(qs,predicado1,"o"));
                	triplas.add(resultE(qs,predicado2,busca2));
                	result3 = resultE(qs,predicado3,busca3);
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "fef":
                	triplas.add(resultF(qs,predicado1,"o"));
                	triplas.add(resultE(qs,predicado2,busca2));
                	result3 = resultF(qs,predicado3,"o3");
                	if(result3!=null){triplas.add(result3);}
                	break;                	
                case "ffe":
                	triplas.add(resultF(qs,predicado1,"o1"));
                	triplas.add(resultF(qs,predicado2,"o2"));
                	result3 = resultE(qs,predicado3,busca3);
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "fff":
                	triplas.add(resultF(qs,predicado1,"o1"));
                	triplas.add(resultF(qs,predicado2,"o2"));
                	result3 = resultF(qs,predicado3,"o3");
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "eOPe":
                	triplas.add(resultE(qs,predicado1,busca1));
                	result3 = resultE(qs,predicado3,busca3);
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "eOPf":
                	triplas.add(resultE(qs,predicado1,busca1));
                	result3 = resultF(qs,predicado3,"o3");
                	if(result3!=null){triplas.add(result3);}
                	break;                	
                case "fOPe":
                	triplas.add(resultF(qs,predicado1,"o1"));
                	result3 = resultE(qs,predicado3,busca3);
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "fOPf":
                	triplas.add(resultF(qs,predicado1,"o1"));
                	result3 = resultF(qs,predicado3,"o3");
                	if(result3!=null){triplas.add(result3);}
                	break; 
                case "cmpe":
                	triplas.add(resultCmp(qs));
                	triplas.add(resultE(qs,predicado3,busca3));
                	break;  
                case "cmpf":
                	triplas.add(resultCmp(qs));
                	triplas.add(resultF(qs,predicado3,"o3"));
                	break; 
                case "ene":
                	triplas.add(resultE(qs,predicado1,busca1));
                	result3 = resultE(qs,predicado3,busca3);
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "enf":
                	triplas.add(resultE(qs,predicado1,busca1));
                	result3 = resultF(qs,predicado3,"o3");
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "fnf":
                	triplas.add(resultF(qs,predicado3,"o1"));
                	result3 = resultF(qs,predicado3,"o3");
                	if(result3!=null){triplas.add(result3);}
                	break;
                case "fne":
                	triplas.add(resultF(qs,predicado3,"o1"));
                	result3 = resultE(qs,predicado3,busca3);
                	if(result3!=null){triplas.add(result3);}
                	break;
                }
            }
            qexec.close();
            System.out.println("[SUCESS   ]: Run SPARQL query");
        }catch(Exception e){
        	e.printStackTrace();
        	System.out.println("[EXCEPTION]: Error to run the SPARQL query.");
        	printException(e.toString());
        	return null;
        }
        return triplas;		
	}

	// Quando o resultado é de uma busca exata
	public SPARQLTriple resultE(QuerySolution qs, String predicate, String object){
		SPARQLTriple tripla = new SPARQLTriple();
		tripla.setSujeito(qs.get("s").toString());
		if(!existsURI(tripla.getSujeito())){uris.add(tripla.getSujeito());}
        tripla.setPredicado(predicate);
        tripla.setObjeto(object);
        if(object.length()>6){
	        if(object.substring(0, 6).equals("http:/")){
	        	System.out.println("[SPARQLQueryGenerator]: Entrou como URI");
	        	if(!existsURI(tripla.getSujeito())){uris.add(object);}
	        }
        }
        return tripla;
	}
	
	// Quando o resultado é de uma busca livre
	public SPARQLTriple resultF(QuerySolution qs, String predicate, String objectName){
		SPARQLTriple tripla = new SPARQLTriple();
		tripla.setSujeito(qs.get("s").toString());
		if(!existsURI(tripla.getSujeito())){uris.add(tripla.getSujeito());}
        tripla.setPredicado(predicate);
        if(qs.get(objectName)==null){return null;}
        tripla.setObjeto(qs.get(objectName).toString());
        if(qs.get(objectName).isResource()){
        	if(!existsURI(tripla.getSujeito())){
        	uris.add(tripla.getObjeto());}}
        return tripla;
	}
	
	public SPARQLTriple resultCmp(QuerySolution qs){
		SPARQLTriple tripla = new SPARQLTriple();
    	tripla.setSujeito(qs.get("s").toString());
    	if(!existsURI(tripla.getSujeito())){uris.add(tripla.getSujeito());}
        tripla.setPredicado(qs.get("p").asResource().getLocalName());       
        tripla.setObjeto(qs.get("o").toString());
        if(qs.get("o").isResource()){if(!existsURI(tripla.getSujeito())){uris.add(tripla.getObjeto());}}
        return tripla;
	}
	
	
	public ArrayList<Neo4jNode> constructNodes(ArrayList<String> uRIs, Neo4jRepository neo4j, String endpointURL) {
		
		// ArrayList que conterá os retornos do método
		ArrayList<Neo4jNode> nodes = new ArrayList<Neo4jNode>();

		for (int i = 0; i < uRIs.size(); i++) {

			// O método state checa se a URI já está inserido no banco Neo4j.
			// state = 0; URI não encontrada no banco Neo4j.
			// state = 1; URI encontrada no banco Neo4j.
			// state = -1; EXCEPTION do método.
			int state = neo4j.checkURI(uRIs.get(i), "URI");

			if (state == 0) {

				try {

					// for que executará uma query para cada URI fornecida

					// Query sendo montada
					String sparqlQuery = "SELECT DISTINCT ?propertyName ?propertyValue WHERE { <" + uRIs.get(i)
							+ "> ?propertyName ?propertyValue }";
					// Query sendo criada
					Query query = QueryFactory.create(sparqlQuery);

					// Query sendo enviada ao endpoint para ser executada
					QueryExecution qexec = QueryExecutionFactory.sparqlService(endpointURL, query);
					// Obtendo o resultado
					ResultSet results = qexec.execSelect();

					// ArrayList que armazenará vários Property, que
					// descrevem um
					// "nó"
					ArrayList<Neo4jProperty> properties = new ArrayList<Neo4jProperty>();

					// Como a busca foi realizada buscando por todos os
					// itens
					// relacionados à uma URI a própria URI não é retornada,
					// para
					// também adicionar a URI como um "atributo" do nó, é
					// utilizado
					// o propertyHelper
					Neo4jProperty propertyHelper = new Neo4jProperty();
					propertyHelper.setPropertyName("uri");
					propertyHelper.setPropertyValue(uRIs.get(i));
					properties.add(propertyHelper);
					// propertyHelper = new Property();
					// propertyHelper.setPropertyName("endpoint");
					// propertyHelper.setPropertyValue(endpointURL);
					// properties.add(propertyHelper);

					// Guarda as URIs "de salto +1" que deverão também ser
					// construídas
					ArrayList<String> hopURIs = new ArrayList<String>();
					// while para percorrer os resultados obtidos no
					// endpoint
					while (results.hasNext()) {
						// Property para armazenar os dados
						// NodeA_URI -[Relationship]-> NodeB_URI
						Neo4jProperty property = new Neo4jProperty();
						QuerySolution qs = results.next();

						// if checa se o valor (object) é um literal, se o
						// mesmo
						// for, os dados são setados em um Property
						if (qs.get("propertyValue").isLiteral()) {
							// apenas o local name do relacionamento
							// (predicate) com
							// um literal (object) é armazenado
							property.setPropertyName(qs.get("propertyName").asResource().getLocalName());
							// valor literal sendo armazenado
							property.setPropertyValue(qs.get("propertyValue").toString());

							// Verificando se a propriedade já existe antes
							// de
							// "criar" uma nova. Se existir, o valor será
							// adicionado
							// ao array, do contrário uma nova propriedade é
							// inserida
							boolean matchFound = false;
							for (int j = 0; j < properties.size(); j++) {
								if (properties.get(j).getPropertyName().equals(property.getPropertyName())) {
									matchFound = true;
									properties.get(j).setPropertyValue(property.getPropertyValue(0));
								}
							}
							if (!matchFound) {
								properties.add(property);
							}

						} else if (doTheHop && hopCount < MAX_HOP_NODES) {
							hopCount++;
							String propertyValue = qs.get("propertyValue").toString();
							hopURIs.add(propertyValue);

							Neo4jRelationship relationship = new Neo4jRelationship();
							relationship.setStartNode(uRIs.get(i));
							relationship.setRelationshipName(qs.get("propertyName").asResource().getLocalName());
							relationship.setEndNode(propertyValue);

							Neo4jNode.relationships.add(relationship);

						}
						if (!results.hasNext() && doTheHop == true) {
							doTheHop = false;

							for (int j = 0; j < uRIs.size(); j++) {
								Neo4jRelationship relationship = new Neo4jRelationship();
								relationship.setStartNode(uRIs.get(j));
								relationship.setRelationshipName("belongsTo");
								relationship.setEndNode(endpointURL);
								// System.err.println("\n" +
								// relationship.getStartNode() + " - [ "+
								// relationship.getRelationshipName() + " ] -> "
								// + relationship.getEndNode()+ "\n");
								Neo4jNode.relationships.add(relationship);
							}
							for (int j = 0; j < hopURIs.size(); j++) {
								Neo4jRelationship relationship = new Neo4jRelationship();
								relationship.setStartNode(hopURIs.get(j));
								relationship.setRelationshipName("belongsTo");
								relationship.setEndNode(endpointURL);
								// System.err.println("\n" +
								// relationship.getStartNode() + " - [ " +
								// relationship.getRelationshipName() + " ] -> "
								// + relationship.getEndNode() + "\n");
								Neo4jNode.relationships.add(relationship);
							}
							nodes.addAll(constructNodes(hopURIs, neo4j, endpointURL));
						}
					}
					Neo4jNode node = new Neo4jNode();
					node.setURI(uRIs.get(i));
					node.setProperties(properties);
					nodes.add(node);
					qexec.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return nodes;

	}
	
	public String runSPARQLQuery(String sparqlQuery, String endpoint){
		String resultQuery="";
		try{
            Query query = QueryFactory.create(sparqlQuery);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            ResultSet results = qexec.execSelect();
            System.out.println("Entrou");
            if(results==null){
            	 System.out.println("Nenhum resultado");
            	resultQuery = "Nenhum resultado encontrado.";
            	return resultQuery;
            }
            List<String> vars =  results.getResultVars();
            ArrayList<String> subjectsUris =  new ArrayList<String>();
            
            while(results.hasNext()){
            	QuerySolution qs = results.next();
            	System.out.println("Tem resultado");
            	for(String var: vars){
            		resultQuery+="\n"+" "+var+": "+qs.get(var).toString()+"\n";
            		System.out.println("Resultado:"+resultQuery);
            		
            		if(qs.get(var).isURIResource()){
            			if(qs.get(var).asResource().getLocalName().equals("") || qs.get(var).asResource().getLocalName()==null){
            				subjectsUris.add(qs.get(var).toString());
            			}
            		}
	            }
            	resultQuery+="\n";
            }
            // Indexando
            if(subjectsUris.size()>0){
	            Neo4jRepository nRepository = new Neo4jRepository();
	            SPARQLRepository sRepository = new SPARQLRepository();
				nRepository.nodesURIFactory(sRepository.constructNodes(subjectsUris, nRepository, endpoint));
				nRepository.relationshipFactory(Neo4jNode.relationships);
            }
			
            qexec.close();
            System.out.println("[SUCESS   ]: Run query SPARQL - Endpoint status.");
            return resultQuery;
		}catch(Exception e){
        	System.out.println("[EXCEPTION]: Error to run the querySPARQL.");
        	printException(e.toString());
        	return null;
        }
	}

	public Boolean existsURI(String uri){
		for(String oldUri: uris){
			if(oldUri.equals(uri)){
				return true;
			}
		}
		return false;
	}

	public void printException(String e){
		// Descrição dos erros conforme https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
		if(e.equals("HttpException: 500")){
			System.out.println("[EXCEPTION]: 500 Internal Server Error");
			System.out.println("[INFO    ]: A generic error message, given when an unexpected condition was encountered and no more specific message is suitable.");
			return;
		}
		if(e.equals("HttpException: 501")){
			System.out.println("[EXCEPTION]: 501 Not Implemented");
			System.out.println("[INFO    ]: The server either does not recognize the request method, or it lacks the ability to fulfill the request.");
			return;
		}
		if(e.equals("HttpException: 502")){
			System.out.println("[EXCEPTION]: 502 Bad Gateway");
			System.out.println("[INFO    ]: The server was acting as a gateway or proxy and received an invalid response from the upstream server.");
			return;
		}
		if(e.equals("HttpException: 503")){
			System.out.println("[EXCEPTION]: 503 Service Unavailable");
			System.out.println("[INFO    ]: The server is currently unavailable (because it is overloaded or down for maintenance).");
			return;
		}
		if(e.equals("HttpException: 504")){
			System.out.println("[EXCEPTION]: 504 Gateway Timeout");
			System.out.println("[INFO    ]: The server was acting as a gateway or proxy and did not receive a timely response from the upstream server.");
			return;
		}		
	}
	
	public ArrayList<String> getUris() {
		return uris;
	}

	public void setUris(ArrayList<String> uris) {
		this.uris = uris;
	}
}
