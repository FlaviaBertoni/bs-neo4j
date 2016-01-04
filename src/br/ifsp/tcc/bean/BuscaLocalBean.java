package br.ifsp.tcc.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.event.SelectEvent;

import br.ifsp.tcc.entities.Neo4jNode;
import br.ifsp.tcc.entities.SPARQLPredicate;
import br.ifsp.tcc.entities.SPARQLTriple;
import br.ifsp.tcc.repository.neo4j.Neo4jRepository;
import br.ifsp.tcc.visualization.GraphGenerator;

@ManagedBean
@SessionScoped
public class BuscaLocalBean {
	private List<SPARQLTriple> triplas;
	private String graph;
	private ArrayList<String> endpoints;
	private String endpoint = "Todos";
	private ArrayList<SPARQLPredicate> predicates;
	private String predicate;
	private String busca;
	private SPARQLTriple selectedURI;
	private Neo4jNode selectedNode;
	private Boolean detailDisable;
	
	public BuscaLocalBean(){
		Neo4jRepository nRepository = new Neo4jRepository();
		this.endpoints = nRepository.findEndpointsURIs();
		this.predicates = nRepository.retrieveAllEndpointsData();
		this.detailDisable = true;
	}
	
	public String searchLocal(){
		this.graph="";
		this.detailDisable=true;
		Neo4jRepository nRepository = new Neo4jRepository();
		if(this.endpoint.equals("Todos")){
			System.out.println("Predicade: "+this.predicate+"| Busca: "+this.busca);
			this.triplas = nRepository.localSearch(this.predicate, this.busca);
			System.out.println("[Entrou] Todos: "+triplas.size());
		}else{
			this.triplas = nRepository.localSearch(this.predicate, this.busca, this.endpoint);
			System.out.println("[Entrou] Endpoint: "+triplas.size());
		}
		ArrayList<String> uris = uniqueURIs();
		if(uris.size() > 0){
			GraphGenerator gGenerator = new GraphGenerator(this.triplas, this.busca, uris);
			this.setGraph(gGenerator.returnGraph());
		}
		return "resultadoBL";
	}
	
	public void loadPredicates(ValueChangeEvent event){
		this.endpoint = event.getNewValue().toString();
		Neo4jRepository nRepository = new Neo4jRepository();
		if(this.endpoint.equals("Todos")){
			this.predicates = nRepository.retrieveAllEndpointsData();
		}else{
			this.predicates = nRepository.retrieveEndpointData(this.endpoint);
		}
	}
	
	public void changeEndpoint(){
		System.out.println("[INFO     ]: Selected SPARQL Endpoint "+this.endpoint);
		Neo4jRepository nRepository = new Neo4jRepository();
		if(this.endpoint.equals("Todos")){
			this.predicates = nRepository.retrieveAllEndpointsData();
		}else{
			this.predicates = nRepository.retrieveEndpointData(this.endpoint);
		}
	}	
	
	public void onRowSelect(SelectEvent event) {
		this.selectedURI = (SPARQLTriple) event.getObject();
		Neo4jRepository nRepository = new Neo4jRepository();
		this.selectedNode = nRepository.uRIToNode(this.selectedURI.getSujeito());
		this.detailDisable=false;
	}
	
	public ArrayList<String> uniqueURIs(){
		ArrayList<String> uris = new ArrayList<String>();
		for(SPARQLTriple triple: this.triplas){
			System.out.println(triple.getSujeito());
			uris.add(triple.getSujeito());
		}
		return uris;
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
	public ArrayList<SPARQLPredicate> getPredicates() {
		return predicates;
	}
	public void setPredicates(ArrayList<SPARQLPredicate> predicates) {
		this.predicates = predicates;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getBusca() {
		return busca;
	}
	public void setBusca(String busca) {
		this.busca = busca;
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

	public Boolean getDetailDisable() {
		return detailDisable;
	}

	public void setDetailDisable(Boolean detailDisable) {
		this.detailDisable = detailDisable;
	}
}
