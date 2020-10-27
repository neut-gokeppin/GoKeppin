Option Explicit
'***********************************************************
' 名称  : 誤欠品検出力向上ツール　メール送信スクリプト
' 概要  : ALC受信ファイル取込処理に失敗したときに起動され、関係者にメールで通知する
' 作成日: 2017/12/01  作成者: DA
'***********************************************************

'*******************************************************************************
'                             定数定義
'*******************************************************************************
' 各関数戻り値定義
Const iRetOK = 0                        '正常
Const iRetNG = -1                       '異常

' メールアドレス定義（送信先複数の場合、カンマ区切り）
Const mailTo = "kenji.nagai@dragonagency.co.jp, kenji.nagai@dragonagency.co.jp"
Const mailFrom = "kenji.nagai@dragonagency.co.jp, kenji.nagai@dragonagency.co.jp"
Const title = "【テスト】誤欠品検出力向上ツール - エラー通知"
Const message = "撮影画像の取込処理にてエラーが発生しました。"

'***********************************************************
' 関数名：cmn_sendMessage
' 概要  ：引数の内容をメールする
' 引数  ：1) メール送信者
'       ：2) メール受信者
'       ：3) メールタイトル
'       ：4) メールメッセージ
' 戻り値：なし
' 作成日：2017/12/01  作成者  ：DA
' 更新日：　　　　　　更新者  ：
'　　　　　　　　　　 更新内容：
'***********************************************************
Sub cmn_sendMessage(FromMlAddr, ToMlAddr, Title, Message)
    Dim oMsg
    Set oMsg = CreateObject("CDO.Message")
    oMsg.BodyPart.Charset = "ISO-2022-JP"           '送信文字コード
    oMsg.From = FromMlAddr
    oMsg.To = ToMlAddr
    oMsg.Subject = Title
    oMsg.TextBody = Message
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/sendusing") = 2                                      '送信方法。1:ローカルSMTPサービス、2:外部SMTPサーバー、3:OLE DBを利用してローカルのExchangeに接続する
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpconnectiontimeout") = 30                         'SMTPサーバーとの接続タイムアウト時間
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpserver") = "smtp.gmoserver.jp"                   'SMTPサーバーのアドレス
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpserverport") = 587                               'SMTPサーバーのポート
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpauthenticate") = 1                               'SMTP認証 1:Basic認証 / 2:NTLM認証
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/sendusername") = "kenji.nagai@dragonagency.co.jp"    'SMTP認証アカウント
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/sendpassword") = "vly$auA8"                          'SMTP認証パスワード
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpusessl") = False                                 'SSLの利用 True :使用 / False:未使用
    oMsg.Configuration.Fields.Update
    oMsg.Send
End Sub

'*******************************************************************************
'                             処理開始
'*******************************************************************************
'パラメータが無い場合でも動作させるようにする。
On Error Resume Next
Dim body
Dim bodyItem
Dim item
For Each item In Wscript.Arguments
    bodyItem = bodyItem & item & vbCrLf
Next
On Error GoTo 0
body = message & vbCrLf & vbCrLf & bodyItem

'メール送信
call cmn_sendMessage(mailFrom, mailTo, title, body)

