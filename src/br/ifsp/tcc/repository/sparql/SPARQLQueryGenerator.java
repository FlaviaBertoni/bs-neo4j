package br.ifsp.tcc.repository.sparql;

import java.util.ArrayList;

public class SPARQLQueryGenerator {
	
	private String sparqlQuery;
	
	// Busca Avançada 1
	public ArrayList<String> advancedSPARQLQuery(String search, String predicate, Boolean exact){
		clean();
		if(exact){
			sparqlQuery = "SELECT DISTINCT * WHERE {"
					+ " ?s ?p ?o."
					+ " FILTER(?p=<"+predicate+"> && ?o=\""+search+"\")"
					+ "}"
					+ " LIMIT 20";
		}else{
			sparqlQuery = "SELECT DISTINCT * WHERE {"
					+ " ?s ?p ?o."
					+ " FILTER(?p=<"+predicate+"> && regex(lcase(str(?o)),\""+search.toLowerCase()+"\"))"
					+ "}"
					+ " LIMIT 20";			
		}
		ArrayList<String> queryAndType = new ArrayList<String>();
		queryAndType.add(sparqlQuery);
		queryAndType.add("cmp");
		return queryAndType;
	}
		
	// Busca Avançada 2
	public ArrayList<String> advancedSPARQLQuery(String search1, String search2,
			String predicate1, String predicate2, String link, Boolean exact1, Boolean exact2){
		clean();
		String type="";
		ArrayList<String> queryAndType = new ArrayList<String>();
		if(link.equals("E")){
			if(exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> \""+search1+"\"" + "."
						+ " ?s <"+predicate2+"> \""+search2+"\""
						+ "}"
						+ " LIMIT 20";
				type="ee";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}
			if(exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> \""+search1+"\"" + "."
						+ " ?s <"+predicate2+"> ?o."
						+ " FILTER regex(lcase(str(?o)),\""+search2.toLowerCase()+"\")"
						+ "}"
						+ " LIMIT 20";
				type="ef";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}			
			if(!exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> ?o."
						+ " FILTER regex(lcase(str(?o)),\""+search1.toLowerCase()+"\")."
						+ " ?s <"+predicate2+"> \""+search2+"\""
						+ "}"
						+ " LIMIT 20";
				type="fe";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}
			if(!exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> ?o1."
						+ " FILTER regex(lcase(str(?o1)),\""+search1.toLowerCase()+"\")."
						+ " ?s <"+predicate2+"> ?o2."
						+ " FILTER regex(lcase(str(?o2)),\""+search2.toLowerCase()+"\")"
						+ "}"
						+ " LIMIT 20";
				type="ff";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}			
		}
		
		if(link.equals("OU")){
			if(exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s ?p ?o."
						+ " FILTER ("
						+ "(?p=<"+predicate1+"> && ?o=\""+search1+"\")"
						+ " || "
						+ "(?p=<"+predicate2+"> && ?o=\""+search2+"\")"
						+ ")"
						+ "}"
						+ " LIMIT 20";
				type="cmp";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}
			if(exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s ?p ?o."
						+ " FILTER ("
						+ "(?p=<"+predicate1+"> && ?o=\""+search1+"\")"
						+ " || "
						+ "(?p=<"+predicate2+"> && regex(lcase(str(?o)),\""+search2.toLowerCase()+"\"))"
						+ ")"
						+ "}"
						+ " LIMIT 20";
				type="cmp";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}			
			if(!exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s ?p ?o."
						+ " FILTER ("
						+ "(?p=<"+predicate1+"> && regex(lcase(str(?o)),\""+search1.toLowerCase()+"\"))"
						+ " || "
						+ "(?p=<"+predicate2+"> && ?o=\""+search2+"\")"
						+ ")"
						+ "}"
						+ " LIMIT 20";
				type="cmp";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}
			if(!exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s ?p ?o."
						+ " FILTER ("
						+ "(?p=<"+predicate1+"> && regex(lcase(str(?o)),\""+search1.toLowerCase()+"\"))"
						+ " || "
						+ "(?p=<"+predicate2+"> && regex(lcase(str(?o)),\""+search2.toLowerCase()+"\"))"
						+ ")"
						+ "}"
						+ " LIMIT 20";
				type="cmp";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}
		}

		if(link.equals("NÃO")){
			if(exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> \""+search1+"\"" + "."
						+ " MINUS{"
						+ " ?s <"+predicate2+"> \""+search2+"\""
						+ "}"
						+ "}"
						+ " LIMIT 20";
				type="e";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}
			if(exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> \""+search1+"\"" + "."
						+ " MINUS{"
						+ " ?s <"+predicate2+"> ?o2."
						+ " FILTER regex(lcase(str(?o2)),\""+search2.toLowerCase()+"\")"
						+ "}"
						+ "}"
						+ " LIMIT 20";
				type="e";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;				
			}			
			if(!exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> ?o."
						+ " FILTER regex(lcase(str(?o)),\""+search1.toLowerCase()+"\")."
						+ " MINUS{"
						+ " ?s <"+predicate2+"> \""+search2+"\""
						+ "}"
						+ "}"
						+ " LIMIT 20";
				type="f";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;
			}
			if(!exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> ?o."
						+ " FILTER regex(lcase(str(?o)),\""+search1.toLowerCase()+"\")."
						+ " MINUS{"
						+ " ?s <"+predicate2+"> ?o2."
						+ " FILTER regex(lcase(str(?o2)),\""+search2.toLowerCase()+"\")"
						+ "}"
						+ "}"
						+ " LIMIT 20";
				type="f";
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;
			}
		}
		return queryAndType;
	}
	
	// Busca Avançada 3
	public ArrayList<String> advancedSPARQLQuery(String search1, String search2, String search3, 
			String predicate1, String predicate2, String predicate3, 
			String link1, String link2,
			Boolean exact1, Boolean exact2, Boolean exact3){
		String type="";
		ArrayList<String> queryAndType = new ArrayList<String>();
				
		// Parte 1 --------------------------------------------------------------------
		if(link1.equals("E")){
			if(exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> \""+search1+"\"" + "."
						+ " ?s <"+predicate2+"> \""+search2+"\".";
				type="ee";
			}
			if(exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> \""+search1+"\"" + "."
						+ " ?s <"+predicate2+"> ?o."
						+ " FILTER regex(lcase(str(?o)),\""+search2.toLowerCase()+"\").";
				type="ef";
			}			
			if(!exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> ?o."
						+ " FILTER regex(lcase(str(?o)),\""+search1.toLowerCase()+"\")."
						+ " ?s <"+predicate2+"> \""+search2+"\".";
				type="fe";
			}
			if(!exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> ?o1."
						+ " FILTER regex(lcase(str(?o1)),\""+search1.toLowerCase()+"\")."
						+ " ?s <"+predicate2+"> ?o2."
						+ " FILTER regex(lcase(str(?o2)),\""+search2.toLowerCase()+"\").";
				type="ff";
			}			
		}
		
		if(link1.equals("OU")){
			if(exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s ?p ?o."
						+ " FILTER ("
						+ "(?p=<"+predicate1+"> && ?o=\""+search1+"\")"
						+ " || "
						+ "(?p=<"+predicate2+"> && ?o=\""+search2+"\")";
				type="cmp";
			}
			if(exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s ?p ?o."
						+ " FILTER ("
						+ "(?p=<"+predicate1+"> && ?o=\""+search1+"\")"
						+ " || "
						+ "(?p=<"+predicate2+"> && regex(lcase(str(?o)),\""+search2.toLowerCase()+"\"))";
				type="cmp";
			}			
			if(!exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s ?p ?o."
						+ " FILTER ("
						+ "(?p=<"+predicate1+"> && regex(lcase(str(?o)),\""+search1.toLowerCase()+"\"))"
						+ " || "
						+ "(?p=<"+predicate2+"> && ?o=\""+search2+"\")";
				type="cmp";
			}
			if(!exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s ?p ?o."
						+ " FILTER ("
						+ "(?p=<"+predicate1+"> && regex(lcase(str(?o)),\""+search1.toLowerCase()+"\"))"
						+ " || "
						+ "(?p=<"+predicate2+"> && regex(lcase(str(?o)),\""+search2.toLowerCase()+"\"))";
				type="cmp";
			}
		}

		if(link1.equals("NÃO")){
			if(exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> \""+search1+"\"" + "."
						+ " MINUS{"
						+ " ?s <"+predicate2+"> \""+search2+"\"";
				type="e";
			}
			if(exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> \""+search1+"\"" + "."
						+ " MINUS{"
						+ " ?s <"+predicate2+"> ?o2."
						+ " FILTER regex(lcase(str(?o2)),\""+search2.toLowerCase()+"\")";
				type="e";
			}			
			if(!exact1 && exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> ?o1."
						+ " FILTER regex(lcase(str(?o1)),\""+search1.toLowerCase()+"\")."
						+ " MINUS{"
						+ " ?s <"+predicate2+"> \""+search2+"\"";
				type="f";
			}
			if(!exact1 && !exact2){
				sparqlQuery = "SELECT DISTINCT * WHERE {"
						+ " ?s <"+predicate1+"> ?o1."
						+ " FILTER regex(lcase(str(?o1)),\""+search1.toLowerCase()+"\")."
						+ " MINUS{"
						+ " ?s <"+predicate2+"> ?o2."
						+ " FILTER regex(lcase(str(?o2)),\""+search2.toLowerCase()+"\")";
				type="f";
			}
		}
			
			// Parte 2 ---------------------------------------------------------------------
			if(link2.equals("E")){
				if(link1.equals("OU")){
					sparqlQuery+=")";
				}
				if(link1.equals("NÃO")){
					sparqlQuery+="}";
					type+="n";
				}
				if(exact3){
					sparqlQuery += 
							" ?s <"+predicate3+"> \""+search3+"\""
							+ "}"
							+ " LIMIT 20";
					type+="e";
				}
				if(!exact3){
					sparqlQuery += 
							" ?s <"+predicate3+"> ?o3."
							+ " FILTER regex(lcase(str(?o3)),\""+search3.toLowerCase()+"\")"
							+ "}"
							+ " LIMIT 20";
					type+="f";
				}
				queryAndType.add(sparqlQuery);
				queryAndType.add(type);
				return queryAndType;
			}
			
			if(link2.equals("OU")){
				if(exact3){
					sparqlQuery += " || "
							+ "(?p=<"+predicate3+"> && ?o=\""+search3+"\")"							
							+ ")"
							+ "}"
							+ " LIMIT 20";
					queryAndType.add(sparqlQuery);
					queryAndType.add(type);
					return queryAndType;
				}
				if(!exact3){
					sparqlQuery += " || "
							+ "(?p=<"+predicate3+"> && regex(lcase(str(?o)),\""+search3.toLowerCase()+"\"))"
							+ ")"
							+ "}"
							+ " LIMIT 20";
					queryAndType.add(sparqlQuery);
					queryAndType.add(type);
					return queryAndType;
				}
			}
			
			if(link2.equals("OPCIONAL")){
				if(link1.equals("NÃO")){
					sparqlQuery+="}";
				}
				if(!search3.equals("")){
					sparqlQuery += " OPTIONAL {"
							+ " ?s <"+predicate3+"> \""+search3+"\""
							+ "}"
							+ "}"
							+ " LIMIT 20";
					if(link1.equals("NÃO")){type+="OPe";}else{type+="e";}
					queryAndType.add(sparqlQuery);
					queryAndType.add(type);
					return queryAndType;
				}else{
					sparqlQuery += " OPTIONAL {"
							+ " ?s <"+predicate3+"> ?o3"						
							+ "}"
							+ "}"
							+ " LIMIT 20";
					if(link1.equals("NÃO")){type+="OPf";}else{type+="f";}
					queryAndType.add(sparqlQuery);
					queryAndType.add(type);
					return queryAndType;					
				}
			}

			if(link2.equals("NÃO")){
				if(link1.equals("NÃO")){
					if(exact3){
						sparqlQuery += "."
								+ " ?s <"+predicate3+"> \""+search3+"\""
								+ "}"
								+ "}"
								+ " LIMIT 20";
					}
					if(!exact3){
						sparqlQuery += "."
								+ " ?s <"+predicate3+"> ?o3."
								+ " FILTER regex(lcase(str(?o3)),\""+search3.toLowerCase()+"\")"
								+ "}"
								+ "}"
								+ " LIMIT 20";
					}
				}else{
					if(exact3){
						sparqlQuery += " MINUS{"
								+ " ?s <"+predicate3+"> \""+search3+"\""
								+ "}"
								+ "}"
								+ " LIMIT 20";
					}
					if(!exact3){
						sparqlQuery += " MINUS{"
								+ " ?s <"+predicate3+"> ?o3."
								+ " FILTER regex(lcase(str(?o3)),\""+search3.toLowerCase()+"\")"
								+ "}"
								+ "}"
								+ " LIMIT 20";
					}
				}
			}
		queryAndType.add(sparqlQuery);
		queryAndType.add(type);
		return queryAndType;
	}
	
	//Limpando a variavel
	private void clean(){
		this.sparqlQuery="";
	}
}
