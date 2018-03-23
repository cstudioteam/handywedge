var currenttab=0;
var graph =[];
var paper =[];

var config={
  color:{
    paper:'#fffafa',
    toolbox:'#FFFFFF',
    status:{
      rect:{
        fill:'#EFE',
        stroke:'#CDC',
      }
    },
    rote:{}
  }
};

function blob_buffer(output,name){
  var blob = new Blob([ output ], { "type" : "text/plain" });
  window.URL = window.URL || window.webkitURL;
  var objectURL = window.URL.createObjectURL(blob);
  var link = document.createElement("a");
  document.body.appendChild(link);
  link.href = objectURL;
  link.download = name;
  link.click();
  document.body.removeChild(link);
};

//ツールボックス
joint.shapes.toolbox={};
joint.shapes.toolbox.Element = joint.shapes.basic.Rect.extend({
  defaults: joint.util.deepSupplement({
    type: 'toolbox.Element',
      attrs: {
        rect: {
          stroke: 'none',
        //'fill-opacity': 0,
          fill:config.color.toolbox
       }
      }
    },joint.shapes.basic.Rect.prototype.defaults),
});

joint.shapes.toolbox.ElementView = joint.dia.ElementView.extend({
  template: [
    '<div class="toolbox-element">',
      '<div class="bar">',
      'ツールボックス',
      '</div>',
      '<div class="content">',
      /*'<button class="save">個別にJSONで保存</button>',
      '<button class="sql">個別にSQLで出力</button>',
      '<hr>',*/
      '<button class="add_box">ステータスノードを追加</button>',
    '</div></div>'
  ].join(''),
  initialize: function() {
    _.bindAll(this, 'updateBox');
    _.bindAll(this, 'addBox');
    joint.dia.ElementView.prototype.initialize.apply(this, arguments);
    this.$box = $(_.template(this.template)());
    this.$box.find('.input').on('mousedown click', function(evt) {
      evt.stopPropagation();
    });
    //ボタン関係
    this.$box.find('.save').on('click',_.bind(this.save,this));
    this.$box.find('.sql').on('click',_.bind(this.sql,this));
    this.$box.find('.add_box').on('click',this.addBox);
    this.model.on('change', this.updateBox, this);
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
    this.$box.css({
      width: bbox.width,
      height: bbox.height,
      left: bbox.x,
      top: bbox.y,
      transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)'
    });
  },
  //ボタン関係
  save:function(){
    var output=JSON.stringify(graph[currenttab].toJSON());
    blob_buffer(output,this.$box.find('.filename').val()+'.json');
  },
  sql:function(){
    var table=this.$box.find('.filename').val();
    var output='';
    graph[currenttab].getCells().forEach(
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
    });
    blob_buffer(output,table+'.sql');
  },
  addBox:function(){
    var statusbox=new joint.shapes.status.Element({
      position: { x: this.model.get('position').x+200, y: this.model.get('position').y+50},
      size: { width: 150, height: 100 }
    });
    graph[currenttab].addCell(statusbox);
  },
  removeBox: function(evt) {
    this.$box.remove();

  }
});
/*
joint.shapes.basic.Generic.define('toolbox', {
    attrs: {
        rect: { 'width': 200 },

        '.toolbox-name-rect': { 'stroke': 'black', 'stroke-width': 1, 'fill': '#DDD','height':15 },
        '.toolbox-attrs-rect': { 'stroke': 'black', 'stroke-width': 1,'height':500,'y':15 },
        '.toolbox-statusbox-rect': { 'stroke': 'black', 'stroke-width': 1, 'fill': '#AADDDD','height':50,'width':150,'x':25,'y':20 },

        '.toolbox-name-text': {
            'ref': '.toolbox-name-rect',
            'ref-y': .1,
            'ref-x': .1,
            'text-anchor': 'middle',
            'x-alignment': 'left',
            'fill': 'black',
            'font-size': 15,
            'text':'ツール'
        },
        '.toolbox-statusbox-text': {
            'ref': '.toolbox-statusbox-rect',
            'ref-y': .4,
            'ref-x': .0,
            'text-anchor': 'middle',
            'x-alignment': 'left',
            'fill': 'black',
            'font-size': 12,
            'text':'ステータスボックスを追加'
        },
    },
}, {
    markup: [
        //'<rect class="toolbox-name-rect"/>',
        //'<text class="toolbox-name-text"/>',
        '<rect class="toolbox-attrs-rect"/>',
        '<rect class="toolbox-statusbox-rect"/>',
        '<text class="toolbox-statusbox-text"/>',
        '<g class="rotatable">',
        '<g class="scalable">',
        '</g>',
        '</g>'
    ].join(''),

    initialize: function() {
      this.$box = $(_.template(this.template)());
      this.$box.find('.toolbox-statusbox-rect').on('click',function(){
        var statusbox=new joint.shapes.status.Element({
          position: { x: 50, y: 200},
          size: { width: 150, height: 100 }
        });
        graph[currenttab].addCell(statusbox);
      });
      this.$box.find('.toolbox-statusbox-rect').on('mousedown',function(evt){
        evt.stopPropagation();
      });
      joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }
});
joint.shapes.toolbox.View = joint.dia.ElementView.extend({}, {
    initialize: function() {
      _.bindAll(this);
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'toolbox-update', function() {
            this.update();
            this.resize();
        });
    }
});
//statusノード
/*joint.shapes.basic.Generic.define('status', {
    attrs: {
        rect: { 'width': 200 , 'height':150},
        '.status-status-rect': { 'stroke': 'black', 'stroke-width': 1, 'fill': '#DDD','rx':50 },
        //'.status-name-rect': { 'stroke': 'black', 'stroke-width': 1, 'fill': '#3498db','rx':20,'ry':20 },
        '.status-name-text': {
            'ref': '.status-status-rect',
            'ref-y': .1,
            'ref-x': .5,
            'text-anchor': 'middle',
            'y-alignment': 'middle',
            'text-align':"justify",
            'font-weight': 'bold',
            'fill': 'black',
            'font-size': 12
            //'font-family': 'Times New Roman'
        },
        '.status-status-text': {
            'ref': '.status-status-rect',
            'ref-y': .5,
            'ref-x': .5,
            'text-anchor': 'middle',
            'y-alignment': 'middle',
            //'font-weight': '',
            'fill': 'black',
            'font-size': 12
            //'font-family': 'Times New Roman'
        }
    },
    status:'',
    name: ''
},{
    markup: [
        '<g class="rotatable">',
        '<g class="scalable">',
        '<rect class="status-status-rect"/>',
        //'<rect class="status-name-rect"/>',
        '</g>',
        '<text class="status-status-text"/>',
        '<text class="status-name-text"/>',
        '</g>'
    ].join(''),

    initialize: function() {
      _.bindAll(this,'openeditbox');
        /*this.on('change:status change:name', function() {
            this.updateRectangles();
            this.trigger('status-update');
        }, this);
        this.updateRectangles();
        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },
    updateRectangles: function() {
        var attrs = this.get('attrs');
        var rects = [
            { type: 'name', text: this.get('name') },
            { type: 'status', text: this.get('status') }
        ];
        var offsetY = 0;
        rects.forEach(function(rect) {
            var lines = Array.isArray(rect.text) ? rect.text : [rect.text];
            var rectHeight = lines.length * 20 + 20;
            attrs['.status-' + rect.type + '-text'].text = lines.join('\n');
            offsetY += rectHeight;
        });
    },
    openeditbox:function(evt){
      var edit=new joint.shapes.editbox.Element({
        position: { x: this.get('position').x+150,
                    y: this.get('position').y+100},
        size: { width: 200, height: 150 }
      });
      graph[currenttab].addCell(edit);
      graph[currenttab].addCell(new joint.dia.Link({
        source: { id: this.get('id')},
        target: { id: edit.get('id')}
      }));
      this.listenTo(edit,'editbox-update',function(e){
        this.prop('status',e[1]);
        this.prop('name',e[0]);
        this.updateRectangles();
      });
    }
});
joint.shapes.status.View = joint.dia.ElementView.extend({
  initialize: function() {
    joint.dia.ElementView.prototype.initialize.apply(this, arguments);
    this.listenTo(this.model, 'status-update', function() {
      this.update();
      this.resize();
    });
  },
});
*/
//statusノード
joint.shapes.status = {};
//Element
joint.shapes.status.Element = joint.shapes.basic.Rect
.extend({
  defaults : joint.util.deepSupplement({
    type : 'status.Element',
    attrs : {
      rect : {
        /*stroke : '#EEEEEE',
        'fill-opacity' : 0*/
        'stroke': '#CDC', 'stroke-width': 3, 'fill': '#EFE','rx':20
      }
    },
    status:'',
    status_name:'',
  },joint.shapes.basic.Rect.prototype.defaults),
});
//ElementView
joint.shapes.status.ElementView = joint.dia.ElementView.extend({
    template: [
        '<div class="status-element">',
        '<span>',
          '<div class="delete editable"><i class="fas fa-times"/></div>',
          //'<div class="linker editable"><i class="fas fa-arrow-right"/></div>',
        '</span>',
        '<div class="status"/>',
        '<div class="status_name"/>',
        '</div>'
    ].join(''),
//関数、不明部分多し。
    initialize: function() {
        _.bindAll(this, 'updateBox');
        _.bindAll(this,'initstatus');
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.$box = $(_.template(this.template)());
        /*this.$box.find('.status-element').on('mousedown click', function(evt) {
          evt.stopPropagation();
        });*/
        paper[currenttab].on('blank:pointerclick',_.bind(this.closeeditbox,this));
        this.$box.find('.delete').on('click', _.bind(this.model.remove, this.model));
        //this.$box.find('.linker').on('mousedown', _.bind(this.addlink, this));
        // Update the box position whenever the underlying model changes.
        this.model.on('change',this.updateBox, this);
        // Remove the box when the model gets removed from the graph.
        this.model.on('remove', this.removeBox, this);
        this.initstatus();
        this.updateBox();
    },
    initstatus:function(){
      //採番
      var i=1;
      while(validate('status',"S"+i,this.model.get('id')).length!=0){
        i++;
      }
      this.model.prop('status',"S"+i);
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
        this.$box.find('.status').text(this.model.get('status'));
        this.$box.find('.status_name').text(this.model.get('status_name'));
        this.$box.css({
            width: bbox.width,
            height: bbox.height,
            left: bbox.x,
            top: bbox.y,
            transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)'
        });
    },
    removeBox: function(evt){
      this.closeeditbox();
      this.$box.remove();
    },
    openeditbox:function(evt){
      this.$box.find('.editable').css('visibility',' visible');
      if(this.model.getEmbeddedCells().length>0)return 0;
      var edit=new joint.shapes.editbox.Element({
        status:this.$box.find('.status').text(),
        status_name:this.$box.find('.status_name').text(),
        position: { x: this.model.get('position').x+150,
                    y: this.model.get('position').y+100},
        size: { width: 200, height: 180 }
      });
      graph[currenttab].addCell(edit);
      graph[currenttab].addCell(new joint.dia.Link({
        source: { id: this.model.get('id')},
        target: { id: edit.get('id')}
      }));
      this.model.embed(edit);
      this.listenTo(edit,'editbox-cancelled',_.bind(function(){
        this.closeeditbox();
      },this));

      this.listenTo(edit,'editbox-update',function(e){
        var val=validate('status',e[0],this.model.get('id'));
        console.log(val);
        if(val.length==0){
          this.model.prop('status',e[0]);
          this.model.prop('status_name',e[1]);
          this.updateBox();
          this.closeeditbox();
        }else{
          alert('エラー:ステータス要素のなかに重複したものがあります。');
        }
      });
      var rote=new joint.shapes.rote({
        source: { id: this.model.get('id')},
        target: {
          x: this.model.get('position').x+this.model.get('size').width+100,
          y: this.model.get('position').y+this.model.get('size').height/2
        }
      });
      graph[currenttab].addCell(rote);
      this.model.embed(rote);
      this.listenTo(rote,'change:target:id',function(e){
        this.model.unembed(rote);
        this.closeeditbox();
      });

 },
    closeeditbox:function(){
      this.$box.find('.editable').css('visibility','hidden');
      graph[currenttab].removeCells(this.model.getEmbeddedCells());
    },
});

//ステータス編集ボックス
joint.shapes.editbox = {};
joint.shapes.editbox.Element = joint.shapes.basic.Rect.extend({
  defaults: joint.util.deepSupplement({
    type: 'editbox.Element',
      attrs: {
        rect: { stroke: 'none', 'fill-opacity': 0 }
      }
    },joint.shapes.basic.Rect.prototype.defaults),
});

joint.shapes.editbox.ElementView = joint.dia.ElementView.extend({
  template: [
    '<div class="editbox-element">',
      '<div class="bar">',
      'ステータス編集',
      '</div>',
      '<div class="content">',
      '<label>ステータスコード</label>',
      '<input class="editbox-status"/>',
      '<label>ステータスの内容</label>',
      '<input class="editbox-status_name"/>',
      '<button class="btn btn-secondary btn-sm">Cancel</button>',
      '<button class="btn btn-primary btn-sm">OK</button>',
    '</div></div>'
  ].join(''),
  initialize: function() {
    _.bindAll(this, 'updateBox');
    joint.dia.ElementView.prototype.initialize.apply(this, arguments);
    this.$box = $(_.template(this.template)());
    this.$box.find('.bar').append(' -'+this.model.get('status'));
    this.$box.find('.editbox-status_name').val(this.model.get('status_name'));
    this.$box.find('.editbox-status').val(this.model.get('status'));
    // Prevent paper from handling pointerdown.
    this.$box.find('input').on('mousedown click', function(evt) {
      evt.stopPropagation();
    });
    // This is an example of reacting on the input change and storing the input data in the cell model.
    /*this.$box.find('input').on('change', _.bind(function(evt) {
      this.model.set('input', $(evt.target).val());
    }, this));*/
    this.$box.find('.btn-primary').on('click',_.bind(function(evt) {
      this.model.trigger('editbox-update',
      [this.$box.find('.editbox-status').val(),
        this.$box.find('.editbox-status_name').val(),
      ]);
    },this));
    this.$box.find('.btn-secondary').on('click',_.bind(function(evt) {
      this.model.trigger('editbox-cancelled');
    },this));
    // Update the box position whenever the underlying model changes.
    this.model.on('change', this.updateBox, this);
    this.$box.find('.btn').on('click', _.bind(this.model.remove, this.model));
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
    this.$box.css({
      width: bbox.width,
      height: bbox.height,
      left: bbox.x,
      top: bbox.y,
      transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)'
    });
  },
  removeBox: function(evt) {
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
                '<g class="link-edit btn-link-edit" event="link:edit">',
                '<circle r="11" class="link-edit" transform="translate(22, 0)"/>',
                '<path class="link-edit" fill="#FFFFFF" transform="scale(.7) translate(12,-20)" d="M 10 10 L 10 30 L 30 30 L 30 10 z M 15 15 L 25 15 L 25 25 L 15 25 L 15 15 z"/>',
                '<title>リンクを編集</title>',
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
          position: 0.4,
          attrs: {
            text: {
              text: [],
              fill: 'blue',
              //'font-family': 'sans-serif'
            },
            rect: {
              stroke: '#31d0c6',
              'stroke-width': 20,
              //rx: 5, ry: 5
            }
          }
        }
     ],
      attrs : {
                '.marker-source' : {
                  //        d : 'M 10 0 L 0 5 L 10 10 z',
                          stroke : '#34495E',
                          fill : '#34495E'
                       },
                '.marker-target' : {
                  fill : '#34495E',
                  stroke : '#34495E',
                  d : 'M 10 0 L 0 5 L 10 10 z'
                },
                '.connection' : {
                  stroke : '#34495E',
                  //'stroke-dasharray' : '0',
                  'stroke-width' : 2,
                }
      },
      action:'',
      action_code:''
    },joint.shapes.devs.Link.prototype.defaults),
  initialize: function() {
    _.bindAll(this, 'updateBox');
    _.bindAll(this, 'initcode');
    //以下の初期化文無しでは意味不明のエラーが出る。
    joint.dia.Link.prototype.initialize.apply(this, arguments);
    // Update the box position whenever the underlying model changes.
    this.on('change', this.updateBox, this);
    this.on('mouseover', this.updateBox, this);
    // Remove the box when the model gets removed from the graph.
    this.on('remove', this.removeBox, this);
    this.listenTo(paper[currenttab],'link:edit',_.bind(function(cellView){
      if(cellView.model.id==this.id){
        this.openedit();
      }
    },this));
    this.initcode();
    this.updateBox();
  },
  initcode:function(){
    //採番
    var i=1;
    while(true){
      let valid=true;
      graph[currenttab].getCells().forEach(function(e){
        if(e.get('type')=='rote'&&e.get('action_code')=="L"+i){
          valid=false;
        }
      });
      if(valid){
        break;
      }else{
        i++;
      }
    }
    this.prop('action_code','L'+i);
  },
  updateBox:function(){
    this.prop('labels/0/attrs/text/text/0',this.get('action_code'));
    this.prop('labels/0/attrs/text/text/1',this.get('action'));
    if(this.get('source').id){
      this.attr({'.marker-source': {d: 'M 0 0A 3 3 0 0 0 0 6A 3 3 0 0 0 0 0'},});
    }
    if(this.get('target').id){
      this.trigger('change:target:id');
      this.attr({'.marker-target': { d: 'M0 5A3 3 0 0 0 6 5L16 10L16 0L6 5A3 3 0 0 0 0 5' },});
    }
  },
  openedit:function(){
    let pos={};
    if(this.get('source').id){
      pos.sid=graph[currenttab].getCell(this.get('source').id);
      pos.sx=pos.sid.get('position').x;
      pos.sy=pos.sid.get('position').y;
    }else{
      pos.sx=this.get('source').x;
      pos.sy=this.get('source').y;
    }
    if(this.get('target').id){
      pos.tid=graph[currenttab].getCell(this.get('target').id);
      pos.tx=pos.sid.get('position').x;
      pos.ty=pos.sid.get('position').y;
    }else{
      pos.tx=this.get('target').x;
      pos.ty=this.get('target').y;
    }
    //this.$box.find('.editable').css('visibility',' visible');
    if(this.getEmbeddedCells().length>0)return 0;
    var edit=new joint.shapes.editlink.Element({
      action_code:this.get('action_code'),
      action:this.get('action'),
      position: {
        x: (pos.sx+pos.tx)/2+150,
        y: (pos.sy+pos.ty)/2+50
      },
      size: {
         width: 200,
         height: 180
        }
    });
    graph[currenttab].addCell(edit);
    /*graph[currenttab].addCell(new joint.dia.Link({
      source: { id: this.get('id')},
      target: { id: edit.get('id')}
    }));*/
    //this.embed(edit);
    this.listenTo(edit,'editlink-cancelled',_.bind(function(){
      this.closeeditbox();
    },this));

    this.listenTo(edit,'editlink-update',function(e){
      var val=validate('action_code',e[0],this.get('id'));
      //console.log(e);
      if(val.length==0){
        this.prop('action_code',e[0]);
        this.prop('action',e[1]);
        this.updateBox();
        this.closeeditbox();
      }else{
        alert('エラー:ステータス要素のなかに重複したものがあります。');
      }
    });
  },
  closeeditbox:function(){
    //this.$box.find('.editable').css('visibility','hidden');
    //graph[currenttab].removeCells(this.getEmbeddedCells());
  },
  removeBox:function(){
    this.closeeditbox();
  }
});

//リンク編集ボックス
joint.shapes.editlink = {};
joint.shapes.editlink.Element = joint.shapes.basic.Rect.extend({
  defaults: joint.util.deepSupplement({
    type: 'editlink.Element',
      attrs: {
        rect: { stroke: 'none', 'fill-opacity': 0 }
      }
    },joint.shapes.basic.Rect.prototype.defaults),
});

joint.shapes.editlink.ElementView = joint.dia.ElementView.extend({
  template: [
    '<div class="editlink-element">',
      '<div class="bar">',
      'リンク編集',
      '</div>',
      '<div class="content">',
      '<label>アクションコード</label>',
      '<input class="editlink-action_code"/>',
      '<label>アクション</label>',
      '<input class="editlink-action"/>',
      '<button class="btn btn-secondary btn-sm">Cancel</button>',
      '<button class="btn btn-primary btn-sm">OK</button>',
    '</div></div>'
  ].join(''),
  initialize: function() {
    _.bindAll(this, 'updateBox');
    joint.dia.ElementView.prototype.initialize.apply(this, arguments);
    this.$box = $(_.template(this.template)());
    this.$box.find('.bar').append(' -'+this.model.get('action_code'));
    this.$box.find('.editlink-action_code').val(this.model.get('action_code'));
    this.$box.find('.editlink-action').val(this.model.get('action'));
    // Prevent paper from handling pointerdown.
    this.$box.find('input').on('change', function(evt) {
      evt.stopPropagation();
    });
    this.$box.find('.btn-primary').on('click',_.bind(function(evt) {
      this.model.trigger('editlink-update',
      [this.$box.find('.editlink-action_code').val(),
        this.$box.find('.editlink-action').val(),
      ]);
    },this));
    this.$box.find('.btn-secondary').on('click',_.bind(function(evt) {
      this.model.trigger('editlink-cancelled');
    },this));
    // Update the box position whenever the underlying model changes.
    this.model.on('change', this.updateBox, this);
    this.$box.find('.btn').on('click', _.bind(this.model.remove, this.model));
    // Remove the box when the model gets removed from the graph.
    this.model.on('remove', this.removeBox, this);
    this.listenTo(paper[currenttab],'blank:pointerclick',this.removeBox);
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
    this.$box.css({
      width: bbox.width,
      height: bbox.height,
      left: bbox.x,
      top: bbox.y,
      transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)'
    });
  },
  removeBox: function(evt) {
    this.$box.remove();
  }
});
//一覧

/*
joint.shapes.basic.Generic.define('listbox', {
    attrs: {
        rect: { 'width': 200 },

        '.listbox-name-rect': { 'stroke': 'black', 'stroke-width': 2, 'fill': '#3498db' },
        '.listbox-attrs-rect': { 'stroke': 'black', 'stroke-width': 2, 'fill': '#2980b9' },
        '.listbox-methods-rect': { 'stroke': 'black', 'stroke-width': 2, 'fill': '#2980b9' },

        '.listbox-name-text': {
            'ref': '.listbox-name-rect',
            'ref-y': .5,
            'ref-x': .5,
            'text-anchor': 'middle',
            'y-alignment': 'middle',
            'font-weight': 'bold',
            'fill': 'black',
            'font-size': 12,
            'font-family': 'Times New Roman'
        },
        '.listbox-attrs-text': {
            'ref': '.listbox-attrs-rect', 'ref-y': 5, 'ref-x': 5,
            'fill': 'black', 'font-size': 12, 'font-family': 'Times New Roman'
        },
        '.listbox-methods-text': {
            'ref': '.listbox-methods-rect', 'ref-y': 5, 'ref-x': 5,
            'fill': 'black', 'font-size': 12, 'font-family': 'Times New Roman'
        }
    },

    name: [],
    attributes: [],
    methods: []
}, {
    markup: [
        '<g class="rotatable">',
        '<g class="scalable">',
        '<rect class="listbox-name-rect"/><rect class="listbox-attrs-rect"/><rect class="listbox-methods-rect"/>',
        '</g>',
        '<text class="listbox-name-text"/><text class="listbox-attrs-text"/><text class="listbox-methods-text"/>',
        '</g>'
    ].join(''),

    initialize: function() {

        this.on('change:name change:attributes change:methods', function() {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();
        this.model.on('transition:start',function(){alert()});
        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },

    getClassName: function() {
        return this.get('name');
    },

    updateRectangles: function() {

        var attrs = this.get('attrs');

        var rects = [
            { type: 'name', text: this.getClassName() },
            { type: 'attrs', text: this.get('attributes') },
            { type: 'methods', text: this.get('methods') }
        ];

        var offsetY = 0;

        rects.forEach(function(rect) {

            var lines = Array.isArray(rect.text) ? rect.text : [rect.text];
            var rectHeight = lines.length * 20 + 20;

            attrs['.listbox-' + rect.type + '-text'].text = lines.join('\n');
            attrs['.listbox-' + rect.type + '-rect'].height = rectHeight;
            attrs['.listbox-' + rect.type + '-rect'].transform = 'translate(0,' + offsetY + ')';

            offsetY += rectHeight;
        });
    }

});
*/
