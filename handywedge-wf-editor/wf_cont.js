/*handywedge Workflow Editor
version ß2.2
2017-2018 Cstudio Co.,Ltd. www.cstudio.jp
*/

//バリデーション,同じものがあると同じものの含まれるグラフとidを返す
function validate(target,toadd,addid){//toadd:追加される要素
  var doubled=[];
  var targettype;
  for(i in graph){
    if(graph[i].getCell(addid)){
      targettype=graph[i].getCell(addid).prop('type');}
  }
  for(i in graph){
    for(j in graph[i].getCells()){
      if(toadd==graph[i].getCells()[j].prop(target)){
        if(addid!=graph[i].getCells()[j].prop('id')
          &&targettype==graph[i].getCells()[j].prop('type')
        ){
          doubled.push({graph:i,id:graph[i].getCells()[j].prop('id')});
        }
      }
    }
  }
  return doubled;
}

//ステータス、リンク一覧
function listUpdate() {
  $('#list_status tbody').empty();
  $('#list_link tbody').empty();
  graph.forEach(function(gr){
    gr.getCells().forEach(function(j){
      if(j.get('type')=='status.Element'&&j.get('status')!=''){
        $('#list_status tbody').append(
          '<tr class="'+j.get('id')+'">'+
          '<td>'+j.get('status')+'</td>'+
          '<td>'+j.get('status_name')+'</td></tr>'
        );
      }else if(j.get('type')=='rote'&&j.get('source').id&&j.get('target').id){
        $('#list_link tbody').append(
          '<tr class="'+j.get('id')+'">'+
          '<td>'+j.prop('labels/0/attrs/text/text/0')+'</td>'+
          '<td>'+j.prop('labels/0/attrs/text/text/1')+'</td>'+
          '<td>'+gr.getCell(j.get('source').id).get('status')+'</td>'+
          '<td>'+gr.getCell(j.get('target').id).get('status')+'</td>'+
          '</tr>'
        );
      }
    });
  });
}
/*イベント*/
//>>初期化関連
//ペーパー初期化
function Graph_Init(tab_in){
  var toolbox=new joint.shapes.toolbox.Element({
    position: { x: 0, y: 150},
    size: { width: 170, height: 100 }
  });
  var statusbox=new joint.shapes.status.Element({
    /*name:'ステータスノード',
    status:['ここをクリックするとペーパー上に追加します。',
    'ペーパー上のステータスノードをクリックすると編集できます'],
    */position: { x: 250, y: 200},
    size: { width: 150, height: 100 }
  });
  graph[tab_in].addCells([toolbox,statusbox]);
}
//>>タブ関連
//タブを押した際のイベント
$('#tabs').on('click','.tab',function(){Tab_Change($(this).index());});
//タブの閉じるボタンを押した際のイベント
$('#tabs').on('click','.tab .tabclose',function(e){
   e.stopPropagation();
  Tab_Close($(this).parent().index());
  listUpdate();
});
//タブダブルクリックでタブ設定モーダル表示
$('#tabs').on('dblclick','.tab',function(e){
   e.stopPropagation();
   $('#modal_tabconf').modal();
   $('#modal_tabconf .name').val(
     $('#tabs .tab:eq('+currenttab+') .name').text());
});
//プラスボタンを押した際のイベント
$('.tab.plus').on('click',function(){
  Tab_Add();
  Graph_Init(currenttab);
  $('#modal_tabconf').modal();
});
//ホームボタンを押した際のイベント
$('#zone_tab .tab_home').on('click',Tab_Home);

//タブを増やす
function Tab_Add(){
  var addtab=$('#tabs .tab').length;
  $('#tabs').append('<span class="tab">'+
  '<span class="name"></span><i class="fas fa-times tabclose"></i>'+
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
     color: config.color.paper
  }
  }));
  //ホームの一覧に書き込み
  graph[addtab].on('change add remove',function(){
    listUpdate();
  });
  paper[addtab].on('cell:pointerclick',function(cellView,x,y){
    if(cellView.model.get('type')=="status.Element")
    cellView.openeditbox();
  });
  //スクロールバーを初期化
  Tab_Change(addtab);
  //Graph_Init(addtab);
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
}
//タブを閉じる
function Tab_Close(i){
  let trigger_changetab=0;
  if($('#tabs .tab:eq('+i+')').css('background-color')=='rgb(136, 136, 136)')trigger_changetab=1;
  $('#tabs .tab:eq('+i+')').remove();
  $('#zone_paper .paperframe:eq('+i+')').remove();
  paper.shift(i);
  graph.shift(i);
  Tab_Change(i==0?0:i-1);
  if($('#tabs .tab').size()==0) Tab_Home();
}
//ホームペーパーを呼び出す
function Tab_Home(){
  $('.tab:not(.plus)').css('background-color','#26272A');
  $('.tab_home').css('color','#EEE');
  $('#zone_paper .paperframe').css('display','none');
  $('#zone_paper .home').css('display','');
  currenttab=-1;
}
//タブ編集モーダル
$('#m_tab_ok').click(function(){
  $('#tabs .tab:eq('+currenttab+') .name').replaceWith(
    '<span class="name">'+
    $('#modal_tabconf .name').val()+
    '</span>'
  );
  $('#modal_tabconf .name').val('');
});
//>>スクロール関連

//>>上メニュー
$("#zone_menu span").hover(function(){
    $(this).children().show();
  }, function(){
    $(".menu_content",this).hide();
  });
  //プロジェクトメニュー
  $("#m_c_open").click(function(){
    $('#modal_load_json').modal();
    $('#modal_load_json #file_json').val('');
  });
  //タブメニュー
  $("#m_c_newtab").click(function(){
    Tab_Add();
    Graph_Init(currenttab);
    $('#modal_tabconf').modal();
  });
  $("#m_c_conftab").click(function(){
    $('#modal_tabconf').modal();
    $('#modal_tabconf .name').val(
      $('#tabs .tab:eq('+currenttab+') .name').text());
  });

//セーブ関係（個別セーブはmodelのツールボックスの中にある）
//全体ロード（json）
$('#modal_load_json .btn-primary').on('click',load_json);
function load_json(){
  var input=document.getElementById('file_json').files[0];
  var reader = new FileReader();
  reader.onload = function(e) {
    var loaded=JSON.parse(e.target.result);
    //初期化
    $('#tabs').empty();
    graph=[];
    paper=[];
    for(key in loaded){
      Tab_Add();
      $('#tabs .tab:eq('+currenttab+') .name').text(key);
      graph[graph.length-1].fromJSON(loaded[key]);
      listUpdate();
    }
  };
  reader.readAsText(input,'UTF-8');
}
//全体セーブ
$('#m_c_json').on('click',function(){
  var output='{';
  for(var t=0;t<graph.length;t++){
    output+='"'+$('#tabs .tab:eq('+t+') .name').text()+'":';
    output+=JSON.stringify(graph[t]);
    output+=','
  }
  output=output.slice(0,-1);
  output+='}';
  var blob = new Blob([ output ], { "type" : "text/plain" });
  window.URL = window.URL || window.webkitURL;
  $("#save_json").attr("href", window.URL.createObjectURL(blob));
  var link = document.createElement( 'a' );
  link.href = window.URL.createObjectURL(blob);
  link.download = '名称未設定.json';
  link.click();
});
$('#m_c_csv').on('click',function(){
  var output='';
  //output+='"ステータス"\n';
  output+='"status","status_name","action_code","action","pre_status","post_status"\n';
  for(var t=0;t<graph.length;t++){
    graph[t].getCells().forEach(
      function(i){
        if(i.get('type')=='status'&&i.get('status')!=''){
          //output+=$('#tabs .tab:eq('+t+') .name').text()+',';
          output+='"'+i.get('status')+'",';
          output+='"'+i.get('status_name')+'",';
          output+=',,,\n';
        }
      });
  }
  //output+='リンク\n';
  //output+='タブ,コード,アクション,アクション前ステータス,アクション後ステータス\n';
  for(var t=0;t<graph.length;t++){
    graph[t].getCells().forEach(
      function(i){
        if(i.get('type')=='rote'){
          //output+=$('#tabs .tab:eq('+t+') .name').text()+',';
          output+=',,';
          output+='"'+i.get('action_code')+'",';
          output+='"'+i.get('action')+'",';
          output+='"'+graph[t].getCell(i.get('source').id).get('status')+'",';
          output+='"'+graph[t].getCell(i.get('target').id).get('status')+'"\n';
        }
      }
    );
  }
  var blob = new Blob([ output ], { "type" : "text/plain" });
  window.URL = window.URL || window.webkitURL;
  $("#m_c_csv").attr("href", window.URL.createObjectURL(blob));
  var link = document.createElement( 'a' );
  link.href = window.URL.createObjectURL(blob);
  link.download = '名称未設定.csv';
  link.click();
});

/*
$('#save_sql').on('click',function(){
  var output='{';
  for(var t=0;t<graph.length;t++){
    //var table=this.$box.find('.filename').val();
var output='';
graph[t].getCells().forEach(
  function(i){
    if(i.get('type')=='status.Element'){
      output+='INSERT INTO '+table+'(';
      output+='status,status_name';
      output+=') VALUES(';
      output+='\''+i.get('status')+'\',';
      output+='\''+i.get('status_name')+'\');';
    }
    if(i.get('action')&&
       i.get('action_code')&&
       i.get('pre_status')&&
       i.get('post_status')
     ){
      output+='INSERT INTO '+table+'(';
      output+='action,action_code,pre_status,post_status';
      output+=') VALUES(';
      output+='\''+i.get('action')+'\',';
      output+='\''+i.get('action_code')+'\',';
      output+='\''+i.get('pre_status')+'\',';
      output+='\''+i.get('post_status')+'\');';
    }
  }
  output+='}';
  var blob = new Blob([ output ], { "type" : "text/plain" });
  window.URL = window.URL || window.webkitURL;
  $("#save_json").attr("href", window.URL.createObjectURL(blob));
  var link = document.createElement( 'a' );
  link.href = window.URL.createObjectURL(blob);
  link.download = 'wfe.json';
  link.click();
});*/
