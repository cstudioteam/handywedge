/*handywedge Workflow Editor
//ライセンス関係をかく。後で
This ware depends on
jquery
Bootstrap
fontawesome
*/

//イベント
//初期化関連
//ペーパー初期化
function Graph_Init(tab_in){
  var toolbox=new joint.shapes.toolbox.Element({
    position: { x: 0, y: 150},
    size: { width: 170, height: 300 }
  });
  var statusbox=new joint.shapes.status.Element({
    /*name:'ステータスノード',
    status:['ここをクリックするとペーパー上に追加します。',
    'ペーパー上のステータスノードをクリックすると編集できます'],
    */position: { x: 250, y: 200},
    size: { width: 150, height: 100 }
  });
  graph[tab_in].addCells([toolbox,statusbox]);
  paper[tab_in].on('cell:pointerclick',function(cellView,x,y){
    if(cellView.model.get('type')=="status.Element")
    cellView.openeditbox();
  });
}
//タブ関連
//タブを押した際のイベント
$('#tabs').on('click','.tab',function(){Tab_Change($(this).index());});
//タブの閉じるボタンを押した際のイベント
$('#tabs').on('click','.tab .tabclose',function(){Tab_Close($(this).parent().index());});
//プラスボタンを押した際のイベント
$('.tab.plus').on('click',Tab_Add);
//ホームボタンを押した際のイベント
$('#zone_tab .tab_home').on('click',Tab_Home);
//リサイズ時にタブの大きさを変更
$(window).on('load resize',tab_resize);

//タブの大きさを決定する
function tab_resize(){
  let tabsize=120;//タブ一つあたりの規定の大きさ。紛らわしいので後で分かり易い場所に移動させる事。
  let sizebywindow=(window.innerWidth-250)/$('#tabs .tab').length-$('.tab.plus').width();
  tabsize=((tabsize<sizebywindow)?tabsize:sizebywindow);
  $('#tabs .tab').css('width',tabsize+'px');
}
//タブを増やす
function Tab_Add(){
  /*---------------------------

  ここにモーダル機能を実装する。ファイル名を決定させる

  -----------------------------*/
  var addtab=$('#tabs .tab').length;
  $('#tabs').append('<span class="tab">'+
  '<span class="name">'+addtab+'</span><i class="fas fa-times tabclose"></i>'+
  '</span>');
  graph.push(new joint.dia.Graph());
  $('#zone_paper').append(
    '<div class="paperframe">'+
      '<div class="paper"></div>'+
    '</div>');
  paper.push(new joint.dia.Paper({
    el : $('#zone_paper .paperframe:eq('+addtab+')'),
    width :  window.parent.screen.width,
    height :  window.parent.screen.height,
    gridSize : 1,
    model : graph[addtab],
    restrictTranslate : true, // 領域外の移動制限
    drawGrid : true,
    background: {
     color: '#fffafa'
  }
  }));
  //スクロールバーを初期化
  let scrollper={
    x:$('#zone_paper').width()/paper[addtab].options.width,
    y:$('#zone_paper').height()/paper[addtab].options.height
  };
  $('#zone_paper .slide_horizontal .bar').css(
    'width',
    scrollper.x*100+'%'
  );
  $('#zone_paper .slide_longitudinal .bar').css(
    'height',
    scrollper.y*100+'%'
  );
  tab_resize();
  Tab_Change(addtab);
  Graph_Init(addtab);
};
//タブを変更する
function Tab_Change(to){
  $('#tabs .tab').css('background-color','#26272A');
  $('#tabs .tab:eq('+to+')').css('background-color','#888');
  $('.tab_home').css('color','');
  $('#zone_paper .paperframe').css('display','none');
  $('#zone_paper .home').css('display','none');
  $('#zone_paper .paperframe:eq('+to+')').css('display','');
  currenttab=to;
};
//タブを閉じる
function Tab_Close(i){
  let trigger_changetab=0;
  if($('#tabs .tab:eq('+i+')').css('background-color')=='rgb(136, 136, 136)')trigger_changetab=1;
  $('#tabs .tab:eq('+i+')').remove();
  $('#zone_paper .paperframe:eq('+i+')').remove();
  paper.shift(i);
  graph.shift(i);
  if(i!=0&&trigger_changetab==1)Tab_Change(i-1);
  if(i==0&&trigger_changetab==1)Tab_Change(0);
}
//ホームペーパーを呼び出す
function Tab_Home(){
  $('.tab:not(.plus)').css('background-color','#26272A');
  $('.tab_home').css('color','#EEE');
  $('#zone_paper .paperframe').css('display','none');
  $('#zone_paper .home').css('display','');
}

//スクロール関連
