var tab=0;
var graph =[new joint.dia.Graph()];

var paper =[ new joint.dia.Paper({
  el : $('.paper.0'),
  width : 800,
  height : 600,
  gridSize : 1,
  model : graph[0],
  restrictTranslate : true, // 領域外の移動制限
  drawGrid : true,
  background: {
   color: '#fffafa'
}
}),
new joint.dia.Paper({
  el : $('.paper.1'),
  width : 800,
  height : 600,
  gridSize : 1,
  model : graph[0],
  restrictTranslate : true, // 領域外の移動制限
  drawGrid : true,
  background: {
   color: '#fffafa'
}
})];

//タブ関連
AddTab=function(){
  graph.push(new joint.dia.Graph());
  $('#paperzone').append('<div class="paper '+graph.length-1+'"></div>');
  paper.push(new joint.dia.Paper({
    el : $('.paper.'+(graph.length-1)),
    width : 800,
    height : 600,
    gridSize : 1,
    model : graph[graph.length-1],
    restrictTranslate : true, // 領域外の移動制限
    drawGrid : true,
    background: {
     color: '#fffafa'
  }
  }));
  $('#tabbar div').append('<span class="tab '+graph.length-1+'">dammy x</span>');
};
ChangeTab=function(totoggle){
  tab=totoggle;
  $('#tabbar .tab').css('background-color','lightgray');
  $('#tabbar .tab.'+tab).css('background-color','white');
};
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
paper[tab].on('cell:pointerclick', function(cellView, evt, x, y) {
  if(cellView.model.get('type')=='status.Element'){
    $('.editable').css('visibility','visible');
    $('.uneditable').css('visibility','hidden');
  }
});

paper[tab].on('blank:pointerdown', function(cellView, evt, x, y) {
  $('.status-element .uneditable').css('visibility','visible');
  $('.status-element .editable').css('visibility','hidden');
});
