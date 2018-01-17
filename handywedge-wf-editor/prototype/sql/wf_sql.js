
var graph = new joint.dia.Graph();

var paper = new joint.dia.Paper({
  el : $('#paper'),
  width : 1530,
  height : 870,
  gridSize : 1,
  model : graph,
  restrictTranslate : true, // 領域外の移動制限
  drawGrid : true,
  background: {
   color: '#fffafa'
}
});
