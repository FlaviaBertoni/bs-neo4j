package br.ifsp.tcc.visualization;

import java.util.ArrayList;
import java.util.List;

import br.ifsp.tcc.entities.SPARQLTriple;

public class GraphGenerator {
	
	private List<SPARQLTriple> triplas;
	private ArrayList<GraphNode> nodes;
	private int nextNode;
	private String busca;
	private ArrayList<String> uris;

	public GraphGenerator(List<SPARQLTriple> triplas, String busca, ArrayList<String> uris) {
		this.triplas = triplas;
		this.busca = busca;
		this.uris = uris;
	}
	
	public String returnGraph(){
		createReferenceNodes();
		JsonGenerator.begin();
		System.out.println("[WARNING  ]: Begin JSON.");
		createNodes();
		System.out.println("[SUCESS   ]: Create graph nodes.");
		createEdges();
		System.out.println("[SUCESS   ]: Create graph edges.");
		return JsonGenerator.finishJSON();
	}
	
	public void createReferenceNodes(){
		this.nodes = new ArrayList<GraphNode>();
		nextNode=0;
		String name;
		for(int i=0; i<triplas.size(); i++){
			name = triplas.get(i).getSujeito();
			name = name.replaceAll("\n", " ");
			if(!nodeExists(name)){
				newNode(nextNode,name); 
			}
			
			name = triplas.get(i).getObjeto();
			name = name.replaceAll("\n", " ");
			if(!nodeExists(name)){
				newNode(nextNode,name);
			}
		}
		//Adicionando o nó busca
		int id = nodes.size();
		name = "busca: "+busca;
		name = name.replaceAll("\n", " ");
		GraphNode node = new GraphNode();
		node.setId(id);
		node.setName(name);
		nodes.add(node);
	}
	
	public void createNodes(){	
		
		JsonGenerator.firstNode(nodes.get(0).getName(), group(nodes.get(0).getName()));
			if(nodes.size()>2){
				for(int i=1; i<(nodes.size()-1); i++){
					JsonGenerator.generateNodes(nodes.get(i).getName(), group(nodes.get(i).getName()));
				}
			}
			JsonGenerator.lastNode(nodes.get(nodes.size()-1).getName(), group(nodes.get(nodes.size()-1).getName()));	
	}
	
	public void createEdges(){
		
			JsonGenerator.firstLink(0, 0, 1, triplas.get(0).getPredicado().replaceAll("\n", " "));
			for(int i=1; i<triplas.size(); i++){
				JsonGenerator.generateLinks(i,
						getNodeID(triplas.get(i).getSujeito().replaceAll("\n", " ")),
						getNodeID(triplas.get(i).getObjeto().replaceAll("\n", " ")),
						triplas.get(i).getPredicado().replaceAll("\n", " "));
			}
				
			int numBusca = uris.size()+triplas.size()-1;
			for(int i=triplas.size(); i<(numBusca); i++){
				JsonGenerator.generateLinks(i, nodes.get(nodes.size()-1).getId(), getNodeID(uris.get(i-triplas.size())),"resultado");
			}
			JsonGenerator.lastLink(numBusca, nodes.get(nodes.size()-1).getId(), getNodeID(uris.get(uris.size()-1)),"resultado");
		}
	
	public void newNode(int id, String name){
		GraphNode node = new GraphNode();
		node.setId(id);
		node.setName(name);
		nodes.add(node);
		nextNode++;
	}
	
	public boolean nodeExists(String node){
		for(int i=0;i<nodes.size();i++){
			if(node.equals(nodes.get(i).getName())){
				return true;
			}
		}
		return false;
	}
	
	public int getNodeID(String node){
		for(int i=0;i<nodes.size();i++){
			if(node.equals(nodes.get(i).getName())){
				return nodes.get(i).getId();
			}
		}
		return 0;
	}
	
	public int group(String name){
		
		if(name.length()>6){
			if(name.substring(0, 6).equals("http:/")){
	        	return 0;
	        }
	        return 1;
        }
		return 1;
	}

}
