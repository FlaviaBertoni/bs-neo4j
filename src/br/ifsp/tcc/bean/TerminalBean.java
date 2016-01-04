package br.ifsp.tcc.bean;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.ifsp.tcc.repository.neo4j.Neo4jRepository;
import br.ifsp.tcc.repository.sparql.SPARQLRepository;

@ManagedBean
@SessionScoped
public class TerminalBean {

	private String query;
	private String result;
	private ArrayList<String> endpoints;
	private String endpoint="http://wifo5-04.informatik.uni-mannheim.de/drugbank/sparql";
	private String status;
	private Boolean rendered;
	
	public TerminalBean(){
		Neo4jRepository nRepository = new Neo4jRepository();
		SPARQLRepository sRepository = new SPARQLRepository();
		this.endpoints = nRepository.findEndpointsURIs();
		if(sRepository.endpointOn(this.endpoint)){this.status = "On";}
		else{this.status = "Off";}
		this.rendered=false;
	}
	
	public String executeCustomQuery(){
		System.out.println(this.query);
		SPARQLRepository sRepository = new SPARQLRepository();
		if(!sRepository.endpointOn(this.endpoint)){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage
					(FacesMessage.SEVERITY_FATAL, "Endpoint Indisponível", "você foi redirecionado para a busca local."));
			return "buscaLocal";
		}
		this.rendered = true;
		this.result = sRepository.runSPARQLQuery(this.query, this.endpoint);
		return "";
	}
	
	public void checkStatus(){
		System.out.println("[INFO     ]: Selected SPARQL Endpoint "+this.endpoint);
		SPARQLRepository sRepository = new SPARQLRepository();
		if(sRepository.endpointOn(this.endpoint)){this.status = "On";}
		else{this.status = "Off";}
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public ArrayList<String> getEndpoints() {
		return endpoints;
	}
	public void setEndpoints(ArrayList<String> endpoints) {
		this.endpoints = endpoints;
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

	public Boolean getRendered() {
		return rendered;
	}

	public void setRendered(Boolean rendered) {
		this.rendered = rendered;
	}
}
