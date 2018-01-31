バイナリストア機能
====================


.. seqdiag::
   :name: seq-wf-init-action

   seqdiag {
      span_height = 10;
              ユーザー; 登録画面; 業務プログラム; FWBinaryStore; BinaryStoreコンテナ; LocalFile; S3;

      === 登録 ===
              ユーザー -> 登録画面 [label="POST"];
              登録画面 -> 業務プログラム [label=""];
              業務プログラム -> FWBinaryStore [label="store(filename, InputStream)"];
      FWBinaryStore -> BinaryStoreコンテナ [label="POST appId, filename, file"];
      BinaryStoreコンテナ => LocalFile [label="WRITE"];
      BinaryStoreコンテナ => LocalFile [label="READ"];
      BinaryStoreコンテナ => S3 [label="POST appId, filename, file"];
      BinaryStoreコンテナ => LocalFile [label="DELETE"];
      FWBinaryStore <-- BinaryStoreコンテナ [label=""];
              業務プログラム <-- FWBinaryStore [label=""];
              登録画面 <-- 業務プログラム [label=""];
              ユーザー <-- 登録画面 [label=""];

      === 取得 ===
              ユーザー -> 業務プログラム [label="GET"];
              業務プログラム -> FWBinaryStore [label="get(filename, [IPAddrZone])"];
      FWBinaryStore -> BinaryStoreコンテナ [label="get(apid, filename, [IPAddrZone])"];
      BinaryStoreコンテナ => S3 [label=""];
      FWBinaryStore <-- BinaryStoreコンテナ [label="時限URL"];
              業務プログラム <-- FWBinaryStore [label="時限URL"];
              ユーザー <-- 業務プログラム [label="時限URL"];
              ユーザー => S3 [label="GET"];

              ユーザー [shape=actor]
      LocalFile [shape=circle]
              登録画面 [color=pink]
              業務プログラム [color=pink]
      FWBinaryStore [color=palegreen]
      BinaryStoreコンテナ [color=palegreen]

      group {
        color=khaki

                   登録画面; 業務プログラム; FWBinaryStore;
      }

      group {
        color=gainsboro

        BinaryStoreコンテナ; LocalFile;
      }

      group {
        color=azure

        S3;
      }
   }


