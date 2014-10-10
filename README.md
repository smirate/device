device
======

Smirateのdevice側

実験段階のソースでも、アプリが動く状態であればコミットOK

TODO
------

・待ち受け画面のデザインを頑張ること

・アプリのアイコンを変えること

・通知のアイコンを設定すること

・OMRONからデータをとること

ListenerActivity：待ち受け用のActivityです。
------

このActivityが起動されると以下の3つのスレッドを立てて処理を行う予定です。

・【未】bluetooth通信にてオムロンからデータを取得し、オムロンデータ保管用のフィールド変数を更新

・【済】エヴィクサーの○○通信にてTVからデータを取得し、TVデータ保管用のフィールド変数を更新

・【済】Timer処理にて、フィールド変数の情報を取得し、サーバーへリクエスト送信


NotificationActivity：通知用のActivityです。
------

【済】pushされた通知から起動します。

【済】pushされたデータから画面に表示するテキストを構築します。

※TVのリモコン操作はしない

キー情報
------

一時的なもの

PJ:540534399326

API:AIzaSyCPtVaUNHozHkWpBfOGwcNQfuzcIQ6fzZw

token:APA91bGmoS8A0j92U1xLdeQBmhRCeJCsGHVu4Wvu7LVDvW9NVD4b_PJDr4F-GXHpsoLYmNAUNFtEx5UpWb_R_g5RmrZS1odzBwrYDAVDen-iBVos_GlgRFpIB-wq6fFBu8WooWj_DXYa85QfZVlVPkCjA9dBST4y0JOwtPqfh-BDwMVfqRB_XH0
