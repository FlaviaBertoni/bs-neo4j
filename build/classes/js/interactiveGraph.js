var main = function(){
	var margin = {top: -5, right: -5, bottom: -5, left: -5},
	width = 1000,
	height = 900;

	//var color = "#2196f3";
	function color(n) {
		  var colores = ["#9ed0fa", "#99ffcc"];//["#2196f3", "#00cc66"]; //["#b7dcfb", "#b3ffe1",];
		  return colores[n % colores.length];
	}
	function textcolor(n) {
		  var colores = ["#2196f3", "#00cc66"];//["#0b71c1", "#00994d"]; // ["#0c8bf3", "#00cc7b"];
		  return colores[n % colores.length];
	}

	var force = d3.layout.force()
		.charge(-100)
		.linkDistance(45) //50
		.size([width, height]);
		
	// Zoom
	var zoom = d3.behavior.zoom()
	.scaleExtent([1, 10])
	.on("zoom", redraw);
	
	function redraw() {
	  svg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
	}
	
	var svg = d3.select("#graph").append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.right + ")")
		.call(zoom)
		.append("g");
		// Drag
		svg.append('svg:rect')
		.attr('width', width)
		.attr('height', height)
		.attr('fill', 'white');
		
		var jsontext = document.getElementById('jt').innerHTML;
		graph = JSON.parse(jsontext);

	  force
		  .nodes(graph.nodes)
		  .links(graph.links)
		  .start();
		  
	  var link = svg.selectAll(".link")
		  .data(graph.links)
		  .enter().append("g")
		  .attr("class", "link");
	  link.append("line")
		  .style("marker-end",  "url(#suit)")
		  .on('click', viewTriple);	  
	  link.append("text")
		  .attr("dx", 0)
		  .attr("dy", "0.35em")
		  .text(function(d) { 
			var name = d.localName;
			if(name.length > 20){name = name.substring(0,20) + "...";}
			return name;
		  })
		  .style("fill", "#737373") //2196f3
		  .style("font-size","3px")
		  .attr("class", "triplaEdge");
	  // escondendo os nomes
	  //$(".triplaEdge").hide();
	  
	  var node = svg.selectAll(".node")
		  .data(graph.nodes)
		  .enter().append("g")
		  .attr("class", "node");				  
	  node.append("circle")
		  .attr("r", 5)
		  .style("fill", function(d) { return color(d.group); })
		  .on('click', connectedNodes);
	  node.append("text")
		  .attr("dx", 0)
		  .attr("dy", ".35em")
		  .text(function(d) { 
			var name = d.name;
			if(name.length > 20){name = name.substring(0,20) + "...";}
			return name; 
		  })
		  .style("fill", function(d) { return textcolor(d.group); })//#2196f3
		  .attr("class", "triplaNode");
	  // escondendo os nomes
	 // $(".triplaNode").hide();
		  
	  node.append("title").text(function(d) { return d.name; });
	  link.append("title").text(function(d) { return d.localName; });
	  
	  force.on("tick", function() {
		d3.selectAll("line").attr("x1", function(d) { return d.source.x; })
			.attr("y1", function(d) { return d.source.y; })
			.attr("x2", function(d) { return d.target.x; })
			.attr("y2", function(d) { return d.target.y; });		
			
		d3.selectAll("circle").attr("cx", function (d) {
			return d.x;
		})
			.attr("cy", function (d) {
			return d.y;
		});
		d3.selectAll("text").attr("x", function (d) {
			return d.x;
		})
			.attr("y", function (d) {
			return d.y;
		});
		
		link.select("text").attr("transform", function(d) {
		  return "translate(" + (d.source.x + d.target.x) / 2 + "," 
		  + (d.source.y + d.target.y) / 2 + ")"; });
	  });

	// Setas: Direção do relacionamento
	svg.append("defs").selectAll("marker")
	.data(["suit", "licensing", "resolved"])
	.enter().append("marker")
		.attr("id", function(d) { return d; })
		.attr("viewBox", "0 -5 10 10")
		.attr("refX", 20)
		.attr("refY", 0)
		.attr("markerWidth", 6)
		.attr("markerHeight", 6)
		.attr("orient", "auto")
	.append("path")
		.attr("d", "M0,-5L10,0L0,5 L10,0 L0, -5")
		.style("stroke", "#999999")
		.style("opacity", "0.6");
	
	//---------------------------------------------
	// Interações: Nós e Linhas
	  
	var isLinkSelected = false;
	
	function viewTriple(){
		if(!isLinkSelected){
			var l = d3.select(this);
			l.style("stroke", "#2196f3");
			var edge = l.select(function(d) { return d.id});
			//$(".triplaEdge:eq("+edge+")").show();					
			var s = l.select(function(d) { return d.source.index});
			var t = l.select(function(d) { return d.target.index});
			node.style("stroke", function (d) {
				if(d.index == s || d.index == t){				  
					return "#bbdefb";
				}
			});
			/*node.select("text").select(function (d) {
				if(d.index == s || d.index == t){				  
					$(".triplaNode:eq("+d.index+")").show();
				}
			});*/
			isLinkSelected = true;
		}else
		{
			node.style("stroke","#fff");
			d3.selectAll("line").style("stroke","#ccc");
			//$(".triplaNode").hide();
			//$(".triplaEdge").hide();
			isLinkSelected = false;
		}

	}
	
	var toggle = 0;

	var linkedByIndex = {};
	for (i = 0; i < graph.nodes.length; i++) {
		linkedByIndex[i + "," + i] = 1;
	};
	graph.links.forEach(function (d) {
		linkedByIndex[d.source.index + "," + d.target.index] = 1;
	});

	function neighboring(a, b) {
		return linkedByIndex[a.index + "," + b.index];
	}

	function connectedNodes() {

		if (toggle == 0) {
			d = d3.select(this).node().__data__;
			node.style("opacity", function (o) {
				return neighboring(d, o) | neighboring(o, d) ? 1 : 0.1;
			});
			
			link.style("opacity", function (o) {
				return d.index==o.source.index | d.index==o.target.index ? 1 : 0.1;
			});
			
			toggle = 1;
		} else {
			node.style("opacity", 1);
			link.style("opacity", 1);
			toggle = 0;
		}

	}
}
$(document).ready(main);