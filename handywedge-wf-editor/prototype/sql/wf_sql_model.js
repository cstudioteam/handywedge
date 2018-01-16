//statusノード
joint.shapes.status = {};
//Element
joint.shapes.status.Element = joint.shapes.basic.Rect
.extend({
  defaults : joint.util.deepSupplement({
    type : 'status.Element',
    attrs : {
      rect : {
        stroke : '#EEEEEE',
        'fill-opacity' : 0
      }
    }
  }, joint.shapes.basic.Rect.prototype.defaults)
});
//ElementView
joint.shapes.status.ElementView = joint.dia.ElementView.extend({
    template: [
        '<div class="status-element">',
        '<button class="delete">x</button>',
        '<label class="status"/>',
        '<label class="status_name"/>',
        '</div>'
    ].join(''),
//関数、不明部分多し。
    initialize: function() {
        _.bindAll(this, 'updateBox');
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.$box = $(_.template(this.template)());
        // Prevent paper from handling pointerdown.
        /*
        this.$box.find('.status_name').on('mousedown click', function(evt) {
            evt.stopPropagation();
        });*/
        /*status_nameの更新,現在はGUI外から操作している為コメントアウト
        this.$box.find('.status_name').on('change', _.bind(function(evt) {
            this.model.set('.status_name', $(evt.target).val());
        }, this));*/
        this.$box.find('.delete').on('click', _.bind(this.model.remove, this.model));
        // Update the box position whenever the underlying model changes.
        this.model.on('change', this.updateBox, this);
        // Remove the box when the model gets removed from the graph.
        this.model.on('remove', this.removeBox, this);

        this.updateBox();
    },
    render: function() {
        joint.dia.ElementView.prototype.render.apply(this, arguments);
        this.paper.$el.prepend(this.$box);
        this.updateBox();
        return this;
    },
    updateBox: function() {
        // Set the position and dimension of the box so that it covers the JointJS element.
        var bbox = this.model.getBBox();
        // Example of updating the HTML with a data stored in the cell model.
        this.$box.find('.status').text(this.model.get('.status'));
        this.$box.find('.status_name').text(this.model.get('.status_name'));
        this.$box.css({
            width: bbox.width,
            height: bbox.height,
            left: bbox.x,
            top: bbox.y,
            transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)'
        });
    },
    removeBox: function(evt) {
      delete db_status.data[this.model.id];
      $('#view_status .'+this.model.id).remove();
      this.$box.remove();
    }
});


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
