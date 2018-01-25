//言語トグル
function lang_change(lang){
  if(lang=='Japanese'){
    //モーダル部分
    $('.load_json .modal-title').text('ファイル読み込み');
    //上部バー
    $('#headbar .head_btn.add_box').text('新規ステータス');
    $('#headbar .head_btn.add_link').text('新規リンク');
    $('#headbar .head_btn.tosql').text('SQLダウンロード');
    $('#headbar .head_btn.load_json').text('読込');
    $('#headbar .head_btn.save_json').text('保存');
    //下部バー
    $('#belowbar .pull .status').text('ステータス');
    $('#belowbar .pull .action').text('アクション');
    //テーブルのタイトル
    $('#view_status .contents_title').text('ステータス一覧');
    $('#view_action .contents_title').text('アクション一覧');
    //テーブル内部
    //status
    $('#view_status .table th.status').text('ステータスコード');
    $('#view_status .table th.status_name').text('ステータス');

    $('#view_action .table th.action_code').text('アクションコード');
    $('#view_action .table th.action').text('アクション');
    $('#view_action .table th.pre_status').text('直前のステータス');
    $('#view_action .table th.post_status').text('直後のステータス');


  }
}
lang_change('Japanese');
