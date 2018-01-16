//データベース系のコントローラ
//fw_db_statusを管理するオブジェクト
var db_status=new db_model('fw_status_master',['status','status_name']);
db_status.AddData=function(box,datum){
  var sa=true;
  if(this.data[datum[0]]) sa=false;

  if(sa){
      this.data[datum[0]]={status:datum[1],status_name:datum[2]};
      var t=Object.keys(this.data).length-1;
      var toappend='<tbody class='+datum[0]+'><td><input class="status" value='+datum[1]+'></input></td>'+
      '<td><input class="status_name" value='+datum[2]+'></input></td></tbody>';
      $('#view_status table').append(toappend);
      //データ更新
      //status
      $('#view_status .'+datum[0]+' .status').on('keyup',function(){
        //box内のデータを更新
        box.model.set('.status', $('#view_status .'+datum[0]+' .status').val());
        //データベース内のデータを更新
        db_status.data[datum[0]].status=$('#view_status .'+datum[0]+' .status').val();
      });
      //status_name
      $('#view_status .'+datum[0]+' .status_name').on('keyup',function(){
        //box内のデータを更新
        box.model.set('.status_name', $('#view_status .'+datum[0]+' .status_name').val());
        //データベース内のデータを更新
        db_status.data[datum[0]].status_name=$('#view_status .'+datum[0]+' .status_name').val();
      });
  }
};

/*DnDボックスとdb_modelを紐付ける
paper.on('cell:pointerup', function(cellView, evt, x, y) {
  var elementBelow = graph.get('cells').find(function(cell) {
    if (cell instanceof joint.dia.Link) return false;
    if (cell.id === cellView.model.id) return false;
    if (cell.getBBox().containsPoint(g.point(x, y))) {
      return true;
    }
    return false;
  });
  if (elementBelow&&elementBelow.get('id')==db_status.dnd.dropzone.get('id')) {
  //重ねた部分の特定部分をthis.target.dataに書き出す。
  //名前部分の変数の呼び出しが汚い。改善を検討中。
  db_status.AddData(cellView,[cellView.model.id,cellView.model.get('.status'),cellView.model.get('.status_name')]);
}});*/

var db_rote=new db_model('fw_wf_rote',['action_code','action','pre_status','post_status']);
db_rote.AddData=function(box,datum){
  var sa=true;
  if(this.data[datum[0]]) sa=false;

  if(sa){
      this.data[datum[0]]={action_code:datum[1],action:datum[2],pre_status:'',post_status:''};
      var t=Object.keys(this.data).length-1;
      var toappend='<tbody class='+datum[0]+'>'+
        '<td><input class="action_code" value='+datum[1]+'></input></td>'+
        '<td><input class="action" value='+datum[2]+'></input></td>'+
        '<td><label class="pre_status" value=""></p></td>'+
        '<td><p class="post_status" value=""></p></td>'+
        '</tbody>';
      $('#view_action table').append(toappend);
      //データ更新
      //action_code
      $('#view_action .'+datum[0]+' .action_code').on('keyup',function(){
        //box内のデータを更新
        box.model.set('.action_code', $('#view_action .'+datum[0]+' .action_code').val());
        //データベース内のデータを更新
        db_rote.data[datum[0]].action_code=$('#view_action .'+datum[0]+' .action_code').val();
      });
      //action
      $('#view_action .'+datum[0]+' .action').on('keyup',function(){
        //box内のデータを更新
        box.model.set('.action', $('#view_action .'+datum[0]+' .action').val());
        //データベース内のデータを更新
        db_rote.data[datum[0]].action=$('#view_action .'+datum[0]+' .action').val();
      });
      //pre_status - リンクモデル内に記述
      //post_status- リンクモデル内に記述
    }
};


//イベントハンドラ
//上メニュー
//SQL生成ボタン
$('.tosql').on('click',function(){
  var output='';
  output+=sql.insert(db_status.table,db_status.column,db_status.data);
  output+=sql.insert(db_rote.table,db_rote.column,db_rote.data);
  console.log(output);
  var blob = new Blob([ output ], { "type" : "text/plain" });
  window.URL = window.URL || window.webkitURL;
  $(".head_btn.tosql").attr("href", window.URL.createObjectURL(blob));
  var link = document.createElement( 'a' );
  link.href = window.URL.createObjectURL(blob);
  link.download = 'sql_ddl';
  link.click();
});

//下メニュー(DBメニュー)
//タブボタン(.pull_btn)を押すと、cssにより表示する部分が出てくる。
$('.pull .pull_btn').on('click', function () {
  if($(this).hasClass('selected')){
    $('.pull .pull_btn.selected').removeClass('selected');
    $('#belowbar').removeClass('open');
    //graph.removeCells(db_status.dnd.dropzone);
  }
  else{
    var index = $('.pull span').index(this);
    $('.pull .pull_btn.selected').removeClass('selected');
    //graph.removeCells(db_status.dnd.dropzone);
    $(this).addClass('selected');
    $('#belowbar').addClass('open');
    $('#belowbar .content_area .contents').css('display','none');
    $('#belowbar .content_area .contents').eq(index).css('display','block');
    //if(index==0){
      //DnDできる箱を表示
      //graph.addCell(db_status.dnd.dropzone);
    //}
  }
});
