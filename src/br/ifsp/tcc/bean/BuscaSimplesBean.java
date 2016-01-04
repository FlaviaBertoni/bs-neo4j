package br.ifsp.tcc.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import br.ifsp.tcc.entities.Neo4jNode;
import br.ifsp.tcc.entities.SPARQLTriple;
import br.ifsp.tcc.repository.neo4j.Neo4jRepository;
import br.ifsp.tcc.repository.sparql.SPARQLRepository;
import br.ifsp.tcc.visualization.GraphGenerator;

@ManagedBean
@SessionScoped
public class BuscaSimplesBean {
	
	private List<SPARQLTriple> triplas;
	private String busca;
	private String graph="";
	private String endpoint="http://wifo5-04.informatik.uni-mannheim.de/drugbank/sparql";
	private ArrayList<String> endpoints;
	private String status;
	private SPARQLTriple selectedURI;
	private Neo4jNode selectedNode;
	
	public BuscaSimplesBean(){
		SPARQLRepository sRepository = new SPARQLRepository();
		Neo4jRepository nRepository = new Neo4jRepository();
		this.endpoints = nRepository.findEndpointsURIs();
		
		if(sRepository.endpointOn(this.endpoint)){
			this.status = "On";
		}else{
			this.status = "Off";
		}
	}
   
	public String buscaSimples(){
		SPARQLRepository sRepository = new SPARQLRepository();
		if(!sRepository.endpointOn(this.endpoint)){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage
					(FacesMessage.SEVERITY_FATAL, "Endpoint Indisponível", "você foi redirecionado para a busca local."));
			return "buscaLocal";
		}
		this.triplas = sRepository.buscaSimples(this.busca, this.endpoint);
		if(this.triplas != null){
			// Indexando os dados retornados
			Neo4jRepository nRepository = new Neo4jRepository();
			nRepository.nodesURIFactory(sRepository.constructNodes(sRepository.getUris(), nRepository, this.endpoint));
			nRepository.relationshipFactory(Neo4jNode.relationships);
			GraphGenerator gGenerator = new GraphGenerator(this.triplas, this.busca, sRepository.getUris());
			this.setGraph(gGenerator.returnGraph());
			System.out.println("[SUCESS   ]: Finish graph JSON.");
		}
		return "resultadoBS";
	}
	
	public void checkStatus(){
		System.out.println("[INFO     ]: Selected SPARQL Endpoint "+this.endpoint);
		SPARQLRepository sRepository = new SPARQLRepository();
		if(sRepository.endpointOn(this.endpoint)){
			this.status = "On";
		}else{
			this.status = "Off";
		}
	}
	
	public void onRowSelect(SelectEvent event) {
        this.selectedURI = (SPARQLTriple) event.getObject();
        Neo4jRepository nRepository = new Neo4jRepository();
        this.setSelectedNode(nRepository.uRIToNode(this.selectedURI.getSujeito()));
    }	
	
	public List<SPARQLTriple> getTriplas() {
		return triplas;
	}

	public void setTriplas(List<SPARQLTriple> triplas) {
		this.triplas = triplas;
	}

	public String getBusca() {
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
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

	public ArrayList<String> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(ArrayList<String> endpoints) {
		this.endpoints = endpoints;
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
}