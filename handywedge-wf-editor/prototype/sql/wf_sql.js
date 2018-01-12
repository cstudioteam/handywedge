joint.shapes.status = {};
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

joint.shapes.status.ElementView = joint.dia.ElementView.extend({

    template: [
        '<div class="status-element">',
        '<button class="delete">x</button>',
        '<label class="status"></label>',
        '<label class="status_name"/>',
        '</div>'
    ].join(''),

    initialize: function() {
        _.bindAll(this, 'updateBox');
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.$box = $(_.template(this.template)());
        // Prevent paper from handling pointerdown.
        /*
        this.$box.find('.status_name').on('mousedown click', function(evt) {
            evt.stopPropagation();
        });*/
        //status_nameの更新
        this.$box.find('.status_name').on('change', _.bind(function(evt) {
            this.model.set('.status_name', $(evt.target).val());
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

joint.shapes.devs.DepartmentLink = joint.shapes.devs.Link
    .extend({
      defaults : _
          .defaultsDeep(
              {
                type : 'devs.DepartmentLink',
                toolMarkup : [
                    '<g class="link-tool">',
                    '<g class="tool-remove" event="remove">',
                    '<circle r="11" class="tool-remove" />',
                    '<path class="tool-remove" transform="scale(.8) translate(-16, -16)" d="M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z" />',
                    '<title>Remove link.</title>',
                    '</g>',
                    '<g class="link-edit editable" event="edit">',
                    '<circle r="11" transform="translate(25)" class="link-edit editable"/>',
                    '<path class="link-edit" fill="white" transform="scale(.55) translate(29, -16)" d="M31.229,17.736c0.064-0.571,0.104-1.148,0.104-1.736s-0.04-1.166-0.104-1.737l-4.377-1.557c-0.218-0.716-0.504-1.401-0.851-2.05l1.993-4.192c-0.725-0.91-1.549-1.734-2.458-2.459l-4.193,1.994c-0.647-0.347-1.334-0.632-2.049-0.849l-1.558-4.378C17.165,0.708,16.588,0.667,16,0.667s-1.166,0.041-1.737,0.105L12.707,5.15c-0.716,0.217-1.401,0.502-2.05,0.849L6.464,4.005C5.554,4.73,4.73,5.554,4.005,6.464l1.994,4.192c-0.347,0.648-0.632,1.334-0.849,2.05l-4.378,1.557C0.708,14.834,0.667,15.412,0.667,16s0.041,1.165,0.105,1.736l4.378,1.558c0.217,0.715,0.502,1.401,0.849,2.049l-1.994,4.193c0.725,0.909,1.549,1.733,2.459,2.458l4.192-1.993c0.648,0.347,1.334,0.633,2.05,0.851l1.557,4.377c0.571,0.064,1.148,0.104,1.737,0.104c0.588,0,1.165-0.04,1.736-0.104l1.558-4.377c0.715-0.218,1.399-0.504,2.049-0.851l4.193,1.993c0.909-0.725,1.733-1.549,2.458-2.458l-1.993-4.193c0.347-0.647,0.633-1.334,0.851-2.049L31.229,17.736zM16,20.871c-2.69,0-4.872-2.182-4.872-4.871c0-2.69,2.182-4.872,4.872-4.872c2.689,0,4.871,2.182,4.871,4.872C20.871,18.689,18.689,20.871,16,20.871z"/>',
                    '<title>Edit link.</title>', '</g>',
                    '</g>' ].join(''),
                router : {
                  name : 'normal'
                },
                connector : {
                  name : 'normal'
                },
                labels : [ {
                  position : 15,
                  attrs : {
                    text : {
                      text : 'ラベル1'
                    },
                    rect : {
                      'fill-opacity' : '0'
                    }
                  }
                }, {
                  position : 0.5,
                  attrs : {
                    text : {
                      text : 'ラベル2'
                    },
                    rect : {
                      'fill-opacity' : '0'
                    }
                  }
                }, {
                  position : -15,
                  attrs : {
                    text : {
                      text : 'ラベル3'
                    },
                    rect : {
                      'fill-opacity' : '0'
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
              }, joint.shapes.devs.Link.prototype.defaults)
    });




var graph = new joint.dia.Graph();

var paper = new joint.dia.Paper({
  el : $('#paper'),
  width : 1530,
  height : 870,
  gridSize : 1,
  model : graph,
  restrictTranslate : true, // 領域外の移動制限
  drawGrid : true
});

//イベントハンドラ
//上部ボタン
$('.head_btn.add_box').on('click', function () {
  var num=Object.keys(db_status.data).length;
  var datum='S'+num;
  graph.addCell(new joint.shapes.status.Element({
    '.status':datum,
    '.status_name':'',
    position: { x: 50+num*5, y: 50+num*5 },
    size: { width: 150, height: 100 }
  }));
  //直前にaddCell()したもののviewを呼び出す。
  var tc=paper.findViewByModel(graph.getCells()[graph.getCells().length-1]);
  db_status.AddData(tc,[tc.model.id,datum,'']);
});

$('.head_btn.add_link').on('click', function () {
  var num=Object.keys(db_rote.data).length;
  var datum='R'+num;
  graph.addCell(new joint.dia.Link({
    source: { x: 100, y: 100},
    target: { x: 300, y: 100},
    attrs: {
      '.connection': {
        strokeWidth: 5,
        stroke: '#34495E'
      },
      '.marker-target': { fill: '#34495E', d: 'M 10 0 L 0 5 L 10 10 z' }
    }
  }));
  var tc=paper.findViewByModel(graph.getCells()[graph.getCells().length-1]);
  db_rote.AddData(tc,[tc.model.id,datum,'']);
});
