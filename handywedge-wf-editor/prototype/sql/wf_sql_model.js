//データベースへの情報を保持するオブジェクトを生成
function db_model(table_name,columns){
  this.table=table_name;
  this.column=columns;
  //this.dnd=new db_DnDBox();
  this.data={};
}
db_model.prototype={
  table:'',
  column:[],
  data:{}//{id:{status:'',status_name:''}}
};

//DnDボックス(未使用)
/*
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
*/
//sqlオブジェクト。
sql={
  insert:function(table,column,data){
    var s='';
    for(i in data){
      s+='INSERT INTO '+table+'(';
      for(k=0;k<column.length;k++){
        s+=column[k];
        if(k<column.length-1){
          s+=','
        }
      }
      s+=') VALUES(';
      for(k=0;k<column.length;k++){
        s+='\''+data[i][column[k]]+'\'';
        if(k<column.length-1){
          s+=','
        }
      }s+=');\n';
    }return s;
  }
};
