
joint.shapes.rote ={};
//joint.shapes.rote.

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
                  markup: '<g><text class="action"/></g>',
                  position : 0.6,
                  attrs : {
                    text : {
                      text : 'action',
                      fill:'green',
                      'font-size': 16
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
    },joint.shapes.devs.Link.prototype.defaults),
  initialize: function() {
    _.bindAll(this, 'updateBox');
    //以下の初期化文無しでは意味不明のエラーが出る。
    joint.dia.Link.prototype.initialize.apply(this, arguments);
    // Update the box position whenever the underlying model changes.
    this.on('change', this.updateBox, this);
    // Remove the box when the model gets removed from the graph.
    this.on('remove', this.removeBox, this);
    this.updateBox();
  },
  updateBox:function(){
    this.prop(['labels',0,'attrs','text','text'],this.prop(['.action']));
    if(this.prop(['source']).id){
      var source_status=db_status.data[this.prop(['source']).id].status;
      $('#view_action .'+this.id+' .pre_status').text(source_status);
      db_rote.data[this.id].pre_status=source_status;
    }
    if(this.prop(['target']).id){
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
    position: { x: 50+num*10, y: 50+num*10},
    size: { width: 150, height: 100 }
  }));
  //直前にaddCell()したもののviewを呼び出す。
  var tc=paper.findViewByModel(graph.getCells()[graph.getCells().length-1]);
  db_status.AddData(tc,[tc.model.id,datum,'']);
});

$('.head_btn.add_link').on('click', function () {
  var num=Object.keys(db_rote.data).length;
  var datum='R'+num;
  graph.addCell(new joint.shapes.rote({
    source: { x: 300+num*10, y: 100+num*10},
    target: { x: 450+num*10, y: 70+num*10},
    attrs: {
      '.connection': {
        strokeWidth: 5,
        stroke: '#34495E'
      },
      '.marker-target': { fill: '#34495E', d: 'M 10 0 L 0 5 L 10 10 z' }
    },
    '.action_code':datum,
    '.action':''
  }));
  var tc=paper.findViewByModel(graph.getCells()[graph.getCells().length-1]);
  db_rote.AddData(tc,[tc.model.id,datum,'']);
});
