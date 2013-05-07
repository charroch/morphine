	 
var dataset = [ 5, 10, 13, 19, 21, 25, 22, 18, 15, 13,
                11, 12, 15, 20, 18, 17, 16, 18, 23, 25 ];
	 //Width and height
	 var w  = 500;
	 var h = 500;

//Create SVG element
var svg = d3.select("div.container").append("svg").attr("width", w).attr("height", h);

svg.selectAll("rect").data(dataset.sort()).enter().append("rect") .attr("x", 0).attr("y", 0)
.attr("height", 20)
.attr("y", function(d, i) {
    return i * 21;
}).attr("width", function(d) {
    return d * 4; 
}).text(function(d) {
        return d;
   }).attr("fill", function(d) {
    return "rgb(0, 0, " + (d * 10) + ")";
});;