
/*sqlクラス:sqlとJSONの仲介を行う
（SQLクエリ作成時、テーブルの選択はクラス名を使う為）
--constructor--
sql(table):テーブルの名前を取得
--function--
insert(json):JSONからINSERTクエリを作成

*/
function sql(name){
  this.table=name;
}

sql.prototype={
  table:null,
  insert:function(js){
    s='INSERT INTO '+this.table+' SET';
    j=JSON.parse(js);
    for(c in this.js){
      s+=' '+c+'='+this.js[c];
    }
    s+=';';
    return s;
  }
};
