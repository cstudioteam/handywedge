
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
//バリデーション,同じものがあるとそのidを返す
function validate(db,targetdata,toadd,j){//toadd:追加されるid,j:toaddのid
  for(i in db){
    if(i!=j&&toadd==db[i][targetdata]){
      return true
    }
  }
  return false;
}

//events
//マウスオーバーが難しかったのでマウスクリック
paper.on('cell:pointerclick', function(cellView, evt, x, y) {
  if(cellView.model.get('type')=='status.Element'){
    $('.editable').css('visibility','visible');
    $('.uneditable').css('visibility','hidden');
  }
});

paper.on('blank:pointerdown', function(cellView, evt, x, y) {
  $('.status-element .uneditable').css('visibility','visible');
  $('.status-element .editable').css('visibility','hidden');
});
