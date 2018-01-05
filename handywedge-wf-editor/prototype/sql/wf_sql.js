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
      for(i in db_status.data){
        if(this.model.id==db_status.data[i].id)
        db_status.data.splice(i,1);
        $('#view_status .'+this.model.id).remove();
      }
      this.$box.remove();
    }
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
  var num=db_status.box.length;
  var datum='S'+num;
  db_status.box.push(new joint.shapes.status.Element({
    '.status':datum,
    '.status_name':'',
    position: { x: 50+num*5, y: 50+num*5 },
    size: { width: 90, height: 90 }
  }));
  graph.addCell(db_status.box[db_status.box.length-1]);
});
