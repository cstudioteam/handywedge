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
        '<div class="status-element unedittable">',
        '<label class="status"/>',
        '<label class="status_name"/>',
        '</div>',
        '<div class="status-element editable">',
        '<button class="delete">x</button>',
        '<input class="status"/>',
        '<input class="status_name"/>',
        '</div>'
    ].join(''),
//関数、不明部分多し。
    initialize: function() {
        _.bindAll(this, 'updateBox');
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.$box = $(_.template(this.template)());
        db_status.data[this.model.id]={status:this.model.prop(['.status']),status_name:''};
        if(!$('#view_status .'+this.model.id+' .status').val()){
          var toappend='<tbody class='+this.model.id+'>'+
          '<td>'+
          '<input class="status" value="'+this.model.prop(['.status'])+'"></input>'+
          '</td>'+
          '<td><input class="status_name" value="'+this.model.prop(['.status_name'])+'"></input></td></tbody>';
          $('#view_status table').append(toappend);
        }

        //データ更新 下段->グラフ
        //status
        var upd_status=function(t){
          $('#view_status .'+t.model.id+' .status').on('keyup',function(){
            //box内のデータを更新
            t.model.set('.status', $(this).val());
            //バリデーション.アルゴリズムは後でメモ付きに改良すること
            $('#view_status .status').removeClass('bg-danger');
            for(i in db_status.data){
              for(j in db_status.data){
                if(i!=j&&db_status.data[i].status==db_status.data[j].status){
                  $('#view_status .'+i+' .status').addClass('bg-danger');
                }
              }
            }
          });
        };
        upd_status(this);
        //status_name
        var upd_status_name=function(t){
          $('#view_status .'+t.model.id+' .status_name').on('keyup',function(){
            //box内のデータを更新
            t.model.set('.status_name', $(this).val());
          });
        };
        upd_status_name(this);
        // Prevent paper from handling pointerdown.

        this.$box.find('.status_name').on('mousedown click', function(evt) {
            evt.stopPropagation();
        });
        //statusの更新
        this.$box.find('.status').on('change', _.bind(function(evt) {
            this.model.set('.status', $(evt.target).val());
        }, this));
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
      //データベース内のデータを更新 グラフ->db
      db_status.data[this.model.id].status=this.model.prop('.status');

      db_status.data[this.model.id].status_name=this.model.prop('.status_name');
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
                  position : 0.5,
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
    //this.$box = $(_.template(this.template)());
    //db_roteに追加
    db_rote.data[this.id]={action_code:'',action:'',pre_status:'',post_status:''};
    //下部編集ゾーンに追加
    var toappend='<tbody class='+this.id+'>'+
      '<td><input class="action_code" value="'+this.prop(['.action_code'])+'"></input></td>'+
      '<td><input class="action" value="'+this.prop(['.action'])+'"></input></td>'+
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
        //バリデーション.アルゴリズムは後でメモ付きに改良すること
        $('#view_action .action_code').removeClass('bg-danger');
        for(i in db_rote.data){
          for(j in db_rote.data){
            if(i!=j&&db_rote.data[i].action_code==db_rote.data[j].action_code){
              $('#view_action .'+i+' .action_code').addClass('bg-danger');
            }
          }
        }
      });
    };
    update_action_code(this);
    //pre_status,post_status初期化時にstatusが初期化されていない時の対策
    /*if(this.prop(['source']).id&&!$('#view_status .'+this.prop(['source']).id+'.status').val()){
      var toappend='<tbody class='+this.prop(['source']).id+'>'+
      '<td>'+
      '<input class="status" value="'+db_status.data[this.prop(['source']).id].status+'"></input>'+
      '</td>'+
      '<td><input class="status_name" value="'+db_status.data[this.prop(['source']).id].status_name+'"></input></td></tbody>';
      $('#view_status table').append(toappend);
    }
    if(this.prop(['target']).id&&!$('#view_status .'+this.prop(['target']).id+'.status').val()){
      var toappend='<tbody class='+this.prop(['target']).id+'>'+
      '<td>'+
      '<input class="status" value="'+db_status.data[this.prop(['target']).id].status+'"></input>'+
      '</td>'+
      '<td><input class="status_name" value="'+db_status.data[this.prop(['target']).id].status_name+'"></input></td></tbody>';
      $('#view_status table').append(toappend);
    }*/

    // Update the box position whenever the underlying model changes.
    this.on('change', this.updateBox, this);
    this.on('mouseover', this.updateBox, this);
    // Remove the box when the model gets removed from the graph.
    this.on('remove', this.removeBox, this);
    this.updateBox();
  },
  updateBox:function(){
    //
    //リンクの見た目の更新
    this.prop(['labels',0,'attrs','text','text'],this.prop(['.action']));
    //action_code データベース内のデータを更新
    db_rote.data[this.id].action_code=this.prop(['.action_code']);
    //action データベース内のデータを更新
    db_rote.data[this.id].action=this.prop(['.action']);
    var thisid=this.id;
    if(this.prop(['source']).id){
      //pre_status
      $('#view_action .'+thisid+' .pre_status').text(db_status.data[this.prop(['source']).id].status);
      db_rote.data[thisid].pre_status=db_status.data[this.prop(['source']).id].status;
      $('#view_status  .status').off('change.pre_status'+thisid);
      $('#view_status .'+this.prop(['source']).id+' .status').on('change.pre_status'+thisid,function(){
        $('#view_action .'+thisid+' .pre_status').text($(this).val());
        db_rote.data[thisid].pre_status=$(this).val();
      });
      //linkの見た目を変更
      this.attr({'.marker-source': { fill: '#000000', d: 'M 0 0 A 5 5 0 0 0 0 -10 A 5 5 0 0 0 0 0' },});
    }else{
      $('#view_status .status').off('change.pre_status'+thisid);
      $('#view_action .'+thisid+' .pre_status').text('');
      db_rote.data[thisid].pre_status='';
      //linkの見た目を変更
      this.attr({'.marker-source': { d: '' },});
    }
    if(this.prop(['target']).id){
      //post_status
      $('#view_action .'+thisid+' .post_status').text(db_status.data[this.prop(['target']).id].status);
      db_rote.data[thisid].post_status=db_status.data[this.prop(['target']).id].status;
      $('#view_status .status').off('change.post_status'+thisid);
      $('#view_status .'+this.prop(['target']).id+' .status').on('change.post_status'+thisid,function(){
        $('#view_action .'+thisid+' .post_status').text($(this).val());
        db_rote.data[thisid].post_status=$(this).val();
      });
      //linkの見た目を変更
      this.attr({'.marker-target': { d: 'M0 5A3 3 0 0 0 6 5L16 10L16 0L6 5A3 3 0 0 0 0 5' },});
    }else{
      $('#view_status .status').off('change.post_status'+thisid);
      $('#view_action .'+thisid+' .post_status').text('');
      db_rote.data[thisid].post_status='';
      this.attr({'.marker-target': {
        fill : '#000000',
        stroke : '#000000',
        d : 'M 10 0 L 0 5 L 10 10 z'
      }});

    }
  },
  removeBox:function(){
    delete db_rote.data[this.id];
    $('#view_action .'+this.id).remove();
    $('#view_status .status').off('change.pre_status'+this.id);
    $('#view_status .status').off('change.post_status'+this.id);
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
  error:{
    get validate(){
      for(i in db_status.data){
        if(validate(db_status.data,'status',db_status.data[i].status,i)){
          alert('整合性エラー:ステータスの値値は重複できません。');
          return true;
        }
      }
      for(i in db_rote.data){
        if(validate(db_rote.data,'action_code',db_rote.data[i].action_code,i)){
          alert('整合性エラー:アクションコードの値は重複できません。');
          return true;
        }
      }
      return false;
    }
  },
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
