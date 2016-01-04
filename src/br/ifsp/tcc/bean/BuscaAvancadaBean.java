package br.ifsp.tcc.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.ifsp.tcc.entities.Neo4jNode;
import br.ifsp.tcc.entities.SPARQLPredicate;
import br.ifsp.tcc.entities.SPARQLTriple;
import br.ifsp.tcc.repository.neo4j.Neo4jRepository;
import br.ifsp.tcc.repository.sparql.SPARQLQueryGenerator;
import br.ifsp.tcc.repository.sparql.SPARQLRepository;
import br.ifsp.tcc.visualization.GraphGenerator;

import org.primefaces.event.SelectEvent;

@ManagedBean
@SessionScoped
public class BuscaAvancadaBean {
	
	private List<SPARQLTriple> triplas;
	private String graph="";
	private String endpoint="http://wifo5-04.informatik.uni-mannheim.de/drugbank/sparql";
	private String status;
	private ArrayList<String> endpoints;
	private ArrayList<SPARQLPredicate> predicates;
	private ArrayList<String> links1;
	private ArrayList<String> links2;
	private String link1;
	private String link2;
	private String predicate1;
	private String predicate2;
	private String predicate3;
	private String busca1;
	private String busca2;
	private String busca3;
	private Boolean exact1;
	private Boolean exact2;
	private Boolean exact3;
	private SPARQLTriple selectedURI;
	private Neo4jNode selectedNode;
	private String fullBusca;
	
	public BuscaAvancadaBean(){
		
		Neo4jRepository nRepository = new Neo4jRepository();
		SPARQLRepository sRepository = new SPARQLRepository();
		
		this.endpoints = nRepository.findEndpointsURIs();
		links1 = new ArrayList<String>();
		linksWithOU(false);
		links2 = new ArrayList<String>();
		linksWithOPTIONAL();
		
		
		if(sRepository.endpointOn(this.endpoint)){
			this.status = "On";
		}else{
			this.status = "Off";
		}
		this.predicates = nRepository.retrieveEndpointData(this.endpoint);
	}
	
	public String buscaAvancada(){
		this.graph="";
		SPARQLRepository sRepository = new SPARQLRepository();
		if(!sRepository.endpointOn(this.endpoint)){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage
					(FacesMessage.SEVERITY_FATAL, "Endpoint Indisponível", "você foi redirecionado para a busca local."));
			return "buscaLocal";
		}
		this.triplas = sRepository.advancedSearch(sparqlQuery(), this.endpoint, this.busca1, this.busca2,
				this.busca3, this.predicate1, this.predicate2, this.predicate3);
		if(this.triplas != null){
			System.out.println(sRepository.getUris().size());
			
			// Indexando os dados retornados
			Neo4jRepository nRepository = new Neo4jRepository();
			nRepository.nodesURIFactory(sRepository.constructNodes(sRepository.getUris(), nRepository, this.endpoint));
			nRepository.relationshipFactory(Neo4jNode.relationships);
			
			fullBusca=this.busca1+this.busca2+this.busca3;
			GraphGenerator gGenerator = new GraphGenerator(this.triplas, this.fullBusca, sRepository.getUris());
			this.setGraph(gGenerator.returnGraph());
			System.out.println("[SUCESS   ]: Finish graph JSON.");
		}
		clean();
		return "resultadoBA";
	}
	
	public void onRowSelect(SelectEvent event) {
        this.selectedURI = (SPARQLTriple) event.getObject();
        Neo4jRepository nRepository = new Neo4jRepository();
        this.selectedNode = nRepository.uRIToNode(this.selectedURI.getSujeito());
    }	
	
	public ArrayList<String> sparqlQuery(){
		int qtdfull=1;
		SPARQLQueryGenerator queryGenerator = new SPARQLQueryGenerator();
		ArrayList<String> query = new ArrayList<String>();
		if((this.busca2 != null) && (this.busca2.equals("")==false)){qtdfull++;}
		if((this.busca3 != null) && ((this.busca3.equals("")==false)||this.link2.equals("OPCIONAL"))){qtdfull++;}
		switch(qtdfull){
			case 1:  query = queryGenerator.advancedSPARQLQuery(this.busca1, this.predicate1, this.exact1);
					break;
			case 2: 
				if((this.busca2 != null) && (this.busca2.equals("")==false)){
					query = queryGenerator.advancedSPARQLQuery(this.busca1, this.busca2, 
							this.predicate1, this.predicate2, this.link1, this.exact1, this.exact2);}
				if((this.busca3 != null) && (this.busca3.equals("")==false)){
					query = queryGenerator.advancedSPARQLQuery(this.busca1, this.busca3, 
							this.predicate1, this.predicate3, this.link2, this.exact1, this.exact3);
					this.predicate2 = this.predicate3;
					this.busca2 = this.busca3;
				}	
				break;
			case 3: 
				query = queryGenerator.advancedSPARQLQuery(this.busca1, this.busca2, this.busca3,
						this.predicate1, this.predicate2, this.predicate3,
						this.link1, this.link2,
						this.exact1, this.exact2, this.exact3);
				break;
		}
		return query;
	}
	
	public void changeEndpoint(){
		System.out.println("[INFO     ]: Selected SPARQL Endpoint "+this.endpoint);
		SPARQLRepository sRepository = new SPARQLRepository();
		if(sRepository.endpointOn(this.endpoint)){
			Neo4jRepository nRepository = new Neo4jRepository();
			this.predicates = nRepository.retrieveEndpointData(this.endpoint);
			this.status = "On";
		}else{
			this.status = "Off";
		}
	}
	
	public void showToLocal(){
		if(this.status.equals("Off")){
			System.out.println("TESTE");
		}
	}
	
	public void checkLinks(){
		if(link1.equals("OU")){
			links1.clear();
			links2.clear();
			linksWithOU(true);
		}else{
			links2.clear();
			linksWithOPTIONAL();
		}
	}
	
	public void linksWithOU(Boolean link2){
		links1.add("E");
		links1.add("OU");
		links1.add("NÃO");
		if(link2){
			links2.add("E");
			links2.add("OU");
			links2.add("NÃO");			
		}
	}
	public void linksWithOPTIONAL(){
		links2.add("E");
		links2.add("OPCIONAL");
		links2.add("NÃO");
	}
	
	public void clean(){
		this.exact1=false;
		this.exact2=false;
		this.exact3=false;
	}
	
	public List<SPARQLTriple> getTriplas() {
		return triplas;
	}
	public void setTriplas(List<SPARQLTriple> triplas) {
		this.triplas = triplas;
	}
	public String getGraph() {
		return graph;
	}
	public void setGraph(String graph) {
		this.graph = graph;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<SPARQLPredicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(ArrayList<SPARQLPredicate> predicates) {
		this.predicates = predicates;
	}

	public String getBusca1() {
		return busca1;
	}

	public void setBusca1(String busca1) {
		this.busca1 = busca1;
	}

	public String getBusca2() {
		return busca2;
	}

	public void setBusca2(String busca2) {
		this.busca2 = busca2;
	}

	public String getBusca3() {
		return busca3;
	}

	public void setBusca3(String busca3) {
		this.busca3 = busca3;
	}
	
	public String getLink1() {
		return link1;
	}

	public void setLink1(String link1) {
		this.link1 = link1;
	}

	public String getLink2() {
		return link2;
	}

	public void setLink2(String link2) {
		this.link2 = link2;
	}

	public String getPredicate1() {
		return predicate1;
	}

	public void setPredicate1(String predicate1) {
		this.predicate1 = predicate1;
	}

	public String getPredicate2() {
		return predicate2;
	}

	public void setPredicate2(String predicate2) {
		this.predicate2 = predicate2;
	}

	public String getPredicate3() {
		return predicate3;
	}

	public void setPredicate3(String predicate3) {
		this.predicate3 = predicate3;
	}

	public Boolean getExact1() {
		return exact1;
	}

	public void setExact1(Boolean exact1) {
		this.exact1 = exact1;
	}

	public Boolean getExact2() {
		return exact2;
	}

	public void setExact2(Boolean exact2) {
		this.exact2 = exact2;
	}

	public Boolean getExact3() {
		return exact3;
	}

	public void setExact3(Boolean exact3) {
		this.exact3 = exact3;
	}

	public ArrayList<String> getLinks2() {
		return links2;
	}

	public void setLinks2(ArrayList<String> links2) {
		this.links2 = links2;
	}

	public ArrayList<String> getLinks1() {
		return links1;
	}

	public void setLinks1(ArrayList<String> links1) {
		this.links1 = links1;
	}

	public SPARQLTriple getSelectedURI() {
		return selectedURI;
	}

	public void setSelectedURI(SPARQLTriple selectedURI) {
		this.selectedURI = selectedURI;
	}

	public Neo4jNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(Neo4jNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public ArrayList<String> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(ArrayList<String> endpoints) {
		this.endpoints = endpoints;
	}
}
