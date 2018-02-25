var currenttab=0;
var graph =[];
var paper =[];

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
        rect: { stroke: 'none', 'fill-opacity': 0 }
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
      'ファイル名<br>',
      '<input class="filename"/>',
      '<hr>',
      '<button class="save">個別にJSONで保存</button>',
      '<button class="sql">個別にSQLで出力</button>',
      '<hr>',
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
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.$box = $(_.template(this.template)());
        /*this.$box.find('.status-element').on('mousedown click', function(evt) {
          evt.stopPropagation();
        });*/
        paper[currenttab].on('blank:pointerclick',_.bind(this.closeeditbox,this));
        this.$box.find('.delete').on('click', _.bind(this.model.remove, this.model));
        //this.$box.find('.linker').on('mousedown', _.bind(this.addlink, this));
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
        size: { width: 200, height: 150 }
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
        this.model.prop('status',e[0]);
        this.model.prop('status_name',e[1]);
        this.updateBox();
        this.closeeditbox();
      });
      var linkbox=new joint.shapes.linker.Element({
        position: { x: this.model.get('position').x+150,
                    y: this.model.get('position').y-10},
        size: { width: 15, height: 15 }
      });
      graph[currenttab].addCell(linkbox);
      this.model.embed(linkbox);
      paper[currenttab].on('cell:pointerup', _.bind(function(cellView, evt, x, y) {
       // Find the first element below that is not a link nor the dragged element itself.
       var elementBelow = graph[currenttab].get('cells').find(function(cell) {
           if (cell instanceof joint.dia.Link) return false; // Not interested in links.
           if (cell.id === cellView.model.id) return false; // The same element as the dropped one.
           if (cell.getBBox().containsPoint(g.point(x, y))) {
               return true;
           }
           return false;
       });
       // If the two elements are connected already, don't
       // connect them again (this is application specific though).
       if (elementBelow && !_.contains(graph[currenttab].getNeighbors(elementBelow), cellView.model)) {
           graph[currenttab].addCell(new joint.dia.Link({
               source: { id: this.model.id }, target: { id: elementBelow.id },
               attrs: { '.marker-source': { d: 'M 10 0 L 0 5 L 10 10 z' } }
           }));
           // Move the element a bit to the side.
           cellView.removeBox();
           cellView.remove();
       }
   },this));
    },
    closeeditbox:function(){
      this.$box.find('.editable').css('visibility','hidden');
      graph[currenttab].removeCells(this.model.getEmbeddedCells());
    },
    /*addlink:function(e){
      var toadd=new joint.dia.Link({
        source: { id: this.model.id },
        target: { x:e.clientX,y:e.clientY}
      });
    graph[currenttab].addCell(toadd);
    toadd.on
  }*/
});

//編集ボックス
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
      '編集',
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
joint.shapes.linker = {};
joint.shapes.linker.Element = joint.shapes.basic.Rect.extend({
  defaults: joint.util.deepSupplement({
    type: 'linker.Element',
      attrs: {
        rect: { stroke: 'none', 'fill-opacity': 0 }
      }
    },joint.shapes.basic.Rect.prototype.defaults),
});

joint.shapes.linker.ElementView = joint.dia.ElementView.extend({
  template: [
    '<div class="linker-element">',
      '<i class="fas fa-arrow-right"/>',
    '</div>'
  ].join(''),
  initialize: function() {
    _.bindAll(this, 'updateBox');
    joint.dia.ElementView.prototype.initialize.apply(this, arguments);
    this.$box = $(_.template(this.template)());
      // Update the box position whenever the underlying model changes.
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
  removeBox: function(evt) {
    this.$box.remove();
  }
});
