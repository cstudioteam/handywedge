//データベース系のコントローラ
//fw_db_statusを管理するオブジェクト
var db_status=new db_model('fw_status_master',['status','status_name']);
db_status.AddData=function(box,datum){
    var sa=true;
    for(i=0;i<this.data.length;i++){//order:O(n). 速度改善の余地あり
      if(this.data[i][0]==datum[0]) sa=false;
    }
    if(sa){
      this.data.push(datum);
      var toappend='<tbody><td>'+datum[0]+'</td>'+
      '<td><input class="'+datum[0]+' value='+datum[1]+'"></input></td>';
      $('#view_status table').append(toappend);
      //依存性の解決
      $('#view_status .'+datum[0]).on('keyup',function(){
        box.model.set('.status_name', $('#view_status .'+datum[0]).val());
        db_status.data[db_status.data.length-1][1]=$('#view_status .'+datum[0]).val();
      });
    }
  };

//ボックスとdb_modelを紐付ける
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
  db_status.AddData(cellView,[cellView.model.id,cellView.model.get('.status_name')]);
}});

//イベントハンドラ
//上メニュー
//SQL生成ボタン
$('.tosql').on('click',function(){
  var output='';
  output+=sql.insert(db_status.table,db_status.column,db_status.data);
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
    graph.removeCells(db_status.dnd.dropzone);
  }
  else{
    var index = $('.pull span').index(this);
    $('.pull .pull_btn.selected').removeClass('selected');
    graph.removeCells(db_status.dnd.dropzone);
    $(this).addClass('selected');
    $('#belowbar').addClass('open');
    $('#belowbar .content_area .contents').css('display','none');
    $('#belowbar .content_area .contents').eq(index).css('display','block');
    if(index==0){
      //DnDできる箱を表示
      graph.addCell(db_status.dnd.dropzone);
    }
  }
});
