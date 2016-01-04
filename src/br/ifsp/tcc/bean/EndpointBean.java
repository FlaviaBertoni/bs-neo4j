package br.ifsp.tcc.bean;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import br.ifsp.tcc.repository.neo4j.Neo4jRepository;
import br.ifsp.tcc.repository.sparql.SPARQLRepository;

@ManagedBean
public class EndpointBean {

	private String endpoint;
	private ArrayList<String> endpoints;
	
	public EndpointBean(){
		Neo4jRepository nRepository = new Neo4jRepository();
		this.endpoints = nRepository.findEndpointsURIs();
	}
	
	public void registerEndpoint(){
		Neo4jRepository nRepository = new Neo4jRepository();
		SPARQLRepository sRepository = new SPARQLRepository();
		System.out.println(this.endpoint);
		if(sRepository.getEndpointPredicates(this.endpoint, nRepository)==null){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage
				(FacesMessage.SEVERITY_FATAL, "Endpoint não encontrado", "não foi possível cadastrar o SPARQL endpoint informado."));
		}
		this.endpoints = nRepository.findEndpointsURIs();
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public ArrayList<String> getEndpoints() {
		return endpoints;
	}
	public void setEndpoints(ArrayList<String> endpoints) {
		this.endpoints = endpoints;
	}
}
