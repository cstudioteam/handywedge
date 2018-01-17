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
        db_status.data[this.model.id]={status:this.model.prop(['.status']),status_name:''};
        var toappend='<tbody class='+this.model.id+'>'+
        '<td><input class="status" value="'+this.model.prop(['.status'])+'"></input></td>'+
        '<td><input class="status_name" value=""></input></td></tbody>';
        $('#view_status table').append(toappend);
        //データ更新
        //status
        var upd_status=function(t){
          $('#view_status .'+t.model.id+' .status').on('keyup',function(){
            //box内のデータを更新
            t.model.set('.status', $(this).val());
            //データベース内のデータを更新
            db_status.data[t.model.id].status=$(this).val();
          });
        };
        upd_status(this);
        //status_name
        var upd_status_name=function(t){
          $('#view_status .'+t.model.id+' .status_name').on('keyup',function(){
            //box内のデータを更新
            t.model.set('.status_name', $(this).val());
            //データベース内のデータを更新
            db_status.data[t.model.id].status_name=$(this).val();
          });
        };
        upd_status_name(this);
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
      $('#view_status .'+this.model.id).off('keyup');
      $('#view_status .'+this.model.id).remove();
      this.$box.remove();
    }
});

//fw_rote リンク
joint.shapes.rote=joint.dia.Link.extend({
  defaults : _
    .defaultsDeep({
      type : 'rote',
      toolMarkup : [
                '<g class="link-tool">',
                '<g class="tool-remove" event="remove">',
                '<circle r="11" class="tool-remove" />',
                '<path class="tool-remove" transform="scale(.8) translate(-16, -16)" d="M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z" />',
                '<title>Remove link.</title>',
                '</g>',
                '</g>' ].join(''),
      router : {
                name : 'normal'
      },
      connector : {
                name : 'normal'
      },
      labels : [
        {
                  markup: '<g><rect /><text class="action"/></g>',
                  position : 0.6,
                  attrs : {
                    text : {
                      text : '',
                      fill:'orange',
                      'font-size': 16,
                    }
                }
      } ],
      attrs : {
                  //      '.marker-source' : {
                  //        d : 'M 10 0 L 0 5 L 10 10 z',
                  //        stroke : '#000000',
                  //        fill : '#000000'
                  //      },
                '.marker-target' : {
                  fill : '#000000',
                  stroke : '#000000',
                  d : 'M 10 0 L 0 5 L 10 10 z'
                },
                '.connection' : {
                  stroke : '#000000',
                  'stroke-dasharray' : '0',
                  'stroke-width' : 1
                }
      }
    },joint.shapes.devs.Link.prototype.defaults),
  initialize: function() {
    _.bindAll(this, 'updateBox');
    //以下の初期化文無しでは意味不明のエラーが出る。
    joint.dia.Link.prototype.initialize.apply(this, arguments);
    //db_roteに追加
    db_rote.data[this.id]={action_code:'',action:'',pre_status:'',post_status:''};
    //下部編集ゾーンに追加
    var toappend='<tbody class='+this.id+'>'+
      '<td><input class="action_code" value=""></input></td>'+
      '<td><input class="action" value=""></input></td>'+
      '<td><label class="pre_status" value=""></p></td>'+
      '<td><label class="post_status" value=""></p></td>'+
      '</tbody>';
    $('#view_action table').append(toappend);
    //Elementの情報に他の部分を合わせる
    //action
    var update_action=function(t){
      $('#view_action .'+t.id+' .action').on('keyup',function(){
        //box内のデータを更新
        t.prop(['.action'],$(this).val());
        //データベース内のデータを更新
      });
    };
    update_action(this);

    //action_code
    var update_action_code=function(t){
      $('#view_action .'+t.id+' .action_code').on('keyup',function(){
        //box内のデータを更新
        t.prop(['.action_code'],$(this).val());
        //データベース内のデータを更新
      });
    };
    update_action_code(this);

    // Update the box position whenever the underlying model changes.
    this.on('change', this.updateBox, this);
    // Remove the box when the model gets removed from the graph.
    this.on('remove', this.removeBox, this);
    this.updateBox();
  },
  updateBox:function(){
    //
    //リンクの見た目の更新
    this.prop(['labels',0,'attrs','text','text'],this.prop(['.action']));
    //action_code データベース内のデータを更新
    db_rote.data[this.id].action_code=$('#view_action .'+this.id+' .action_code').val();
    //action データベース内のデータを更新
    db_rote.data[this.id].action=$('#view_action .'+this.id+' .action').val();
    if(this.prop(['source']).id){
      //pre_status
      var source_status=db_status.data[this.prop(['source']).id].status;
      $('#view_action .'+this.id+' .pre_status').text(source_status);
      db_rote.data[this.id].pre_status=source_status;
    }
    if(this.prop(['target']).id){
      //post_status
      var target_status=db_status.data[this.prop(['target']).id].status;
      $('#view_action .'+this.id+' .post_status').text(target_status);
      db_rote.data[this.id].post_status=target_status;
    }
  },
  removeBox:function(){
    delete db_rote.data[this.id];
    $('#view_action .'+this.id).remove();
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

//fw_db_statusを管理するオブジェクト
var db_status=new db_model('fw_status_master',['status','status_name']);
//fw_wf_roteを管理するオブジェクト
var db_rote=new db_model('fw_wf_rote',['action_code','action','pre_status','post_status']);
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
