package br.ifsp.tcc.visualization;

public class JsonGenerator {
	
	private static StringBuffer graphJSON;
	
	// Estrutura JavaScript
	public static void begin(){
		graphJSON = new StringBuffer();
		graphJSON.append("<script type=\"application/json\" id=\"jt\">\n");
	}
	
	//  Gerando os nós em JSON
	public static void generateNodes(String name, int group){
		graphJSON.append("\t{\n")
				.append("\t\t\"name\":\""+name+"\",\n")
				.append("\t\t\"group\":"+group+"\n")
				.append("\t},\n");
	}
	public static void firstNode(String name, int group){
		graphJSON.append("{\n")
			.append("\t\"nodes\": [{\n")
			.append("\t\t\"name\":\""+name+"\",\n")
			.append("\t\t\"group\":"+group+"\n")
			.append("\t},\n");
	}
	public static void lastNode(String name, int group){
		graphJSON.append("\t{\n")
			.append("\t\t\"name\":\""+name+"\",\n")
			.append("\t\t\"group\":"+group+"\n")
			.append("\t}],\n");
	}
	
	//  Gerando as arestas(links) em JSON
	public static void generateLinks(int id, int source, int target, String localName){
		graphJSON.append("\t{\n")
				.append("\t\t\"id\":"+id+",\n")
				.append("\t\t\"source\":"+source+",\n")
				.append("\t\t\"target\":"+target+",\n")
				.append("\t\t\"localName\":\""+localName+"\"\n")
				.append("\t},\n");
	}
	public static void firstLink(int id, int source, int target, String localName){
		graphJSON.append("\t\"links\": [{\n")
				.append("\t\t\"id\":"+id+",\n")
				.append("\t\t\"source\":"+source+",\n")
				.append("\t\t\"target\":"+target+",\n")
				.append("\t\t\"localName\":\""+localName+"\"\n")
				.append("\t},\n");
	}
	public static void lastLink(int id, int source, int target, String localName){
		graphJSON.append("\t{\n")
				.append("\t\t\"id\":"+id+",\n")
				.append("\t\t\"source\":"+source+",\n")
				.append("\t\t\"target\":"+target+",\n")
				.append("\t\t\"localName\":\""+localName+"\"\n")
				.append("\t}]\n")
				.append("}");
	}
	
	// Finalizando
	public static String finishJSON()
    {
		graphJSON.append("\n</script>");
		return graphJSON.toString();
    }
}
