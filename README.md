device
======

Smirateのdevice側

実験段階のソースでも、アプリが動く状態であればコミットOK

・ListenerActivity
　待ち受け用のActivityです。
　このActivityが起動されると以下の3つのスレッドを立てて処理を行う予定です。
　　・bluetooth通信にてオムロンからデータを取得し、オムロンデータ保管用のフィールド変数を更新
　　・エヴィクサーの○○通信にてTVからデータを取得し、TVデータ保管用のフィールド変数を更新
　　・Timer処理にて、フィールド変数の情報を取得し、サーバーへリクエスト送信

・NotificationActivity
　通知用のActivityです。
　pushされた通知から起動します。
　pushされたデータから画面に表示するテキストを構築します。
　TVON用のボタンを押下することで、pushされた番組データをもとにTVを起動します。
