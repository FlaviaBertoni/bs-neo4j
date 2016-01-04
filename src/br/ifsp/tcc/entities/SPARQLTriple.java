package br.ifsp.tcc.entities;

public class SPARQLTriple {
	
	private String sujeito;
	private String predicado;
	private String objeto;
	
	public String getSujeito() {
		return sujeito;
	}
	public void setSujeito(String sujeito) {
		this.sujeito = sujeito;
	}
	public String getPredicado() {
		return predicado;
	}
	public void setPredicado(String predicado) {
		this.predicado = predicado;
	}
	public String getObjeto() {
		return objeto;
	}
	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}
}
