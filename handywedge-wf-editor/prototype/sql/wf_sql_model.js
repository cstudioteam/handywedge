//データベースをやり取りするオブジェクトを生成
function db_model(table_name,columns){
  this.table=table_name;
  this.column=columns;
  this.dnd=new db_DnDBox();
}
db_model.prototype={
  table:'',
  box:new Array(),
  data:new Array(),//[[status,status_name]]
  AddData:function(box,datum){
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
      });
    }
  }
};

//DnDボックス
function db_DnDBox(){
}
db_DnDBox.prototype={
  dropzone:new joint.shapes.basic.Rect({
    position:{ x:500, y:300 },
    size: { width:200, height:100 },
    attrs:{
      rect:{},
      text:{
        text:'ドロップしてデータベースに追加',
        'font-size': 10,
        style: { 'text-shadow': '1px 1px 1px lightgray' }
      }
    }
  }),
  delete:function(){
    this.dropzone.remove();
  }
};

//sqlオブジェクト。
sql={
  insert:function(table,column,data){
    var s='';
    for(i=0;i<data.length;i++){
      if(!data[i][0]) continue;
      s+='INSERT INTO '+table+' SET';
      for(c in column){
        s+=' '+column[c]+'='+data[i][c];
      }s+=';\n';
    }return s;
  }
};
