Option Explicit
'***********************************************************
' 名称  : 誤欠品検出力向上ツール　メール送信スクリプト
' 概要  : ALC受信ファイル取込処理に失敗したときに起動され、関係者にメールで通知する
' 作成日: 2014/08/21  作成者: 海津
'***********************************************************

'*******************************************************************************
'                             定数定義
'*******************************************************************************
' 各関数戻り値定義
Const iRetOK = 0                        '正常
Const iRetNG = -1                       '異常

' メールアドレス定義（送信先複数の場合、カンマ区切り）
' Const mailTo = "takeo_kaizu_za@mail.toyota.co.jp, takahiro_ban_zb@mail.toyota.co.jp, eiichi_yamaguchi_za@mail.toyota.co.jp"
' Const mailTo = "eiichi_yamaguchi_za@mail.toyota.co.jp"
' Const mailFrom = "eiichi_yamaguchi_za@mail.toyota.co.jp"
Const mailTo = "masato_nishiguchi_za@mail.toyota.co.jp"
Const mailFrom = "masato_nishiguchi_za@mail.toyota.co.jp"
Const title = "【テスト】誤欠品検出力向上ツール - エラー通知"
Const message = "ALC受信ファイル取込処理にてエラーが発生しました。取込できないデータを受信しました。"

Dim body

'*******************************************************************************
'                             処理開始
'*******************************************************************************
body = message & vbCrLf & vbCrLf & "受信日時_ライン_T/P_ボデーNO = " & Wscript.Arguments.Item(0)
call cmn_sendMessage(mailFrom, mailTo, title, body)

'***********************************************************
' 関数名：cmn_sendMessage
' 概要  ：引数の内容をメールする
' 引数  ：1) メール送信者
'       ：2) メール受信者
'       ：3) メールタイトル
'       ：4) メールメッセージ
' 戻り値：なし
' 作成日：2011/03/14  作成者  ：岩本
' 更新日：　　　　　　更新者  ：
'　　　　　　　　　　 更新内容：
'***********************************************************
Sub cmn_sendMessage(FromMlAddr, ToMlAddr, Title, Message)
	Dim oMsg
	Set oMsg = CreateObject("CDO.Message")
	oMsg.From = FromMlAddr
	oMsg.To = ToMlAddr
	oMsg.Subject = Title
	oMsg.TextBody = Message
	oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/sendusing") = 2
	oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpconnectiontimeout") = 30
	oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpserver") = "mail07.toyota.co.jp"
	oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpserverport") = 25
	oMsg.Configuration.Fields.Update
	oMsg.Send
End Sub
