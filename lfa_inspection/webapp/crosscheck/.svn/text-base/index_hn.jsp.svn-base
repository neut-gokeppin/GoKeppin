<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>スペックシート×ＰＤＡ　クロスチェック</title>
</head>

<body>
<h1>スペックシート×ＰＤＡ　クロスチェック</h1>

<p>ファイルとマスタ種類を選択し、検査ボタンをクリックしてください。</p>
<br>

<html:errors />
<html:messages id="msg" message="true">
	<bean:write name="msg" /><BR>
</html:messages >

<form method="POST" enctype="multipart/form-data">

	<fieldset>
		<legend>検査項目Excel選択</legend>
		<input type="file" name="formFile" style="width:500px;" >
	</fieldset>
	<br>

	<fieldset>
		<legend>ＰＤＡ　マスタ種類</legend>
		&nbsp;&nbsp;&nbsp;&nbsp;<label><input type=radio name="tlcodePda" value="0" />仮</label>
		&nbsp;&nbsp;&nbsp;&nbsp;<label><input type=radio name="tlcodePda" value="1" checked />本番</label>
	</fieldset>
	<br>

	<p>
		<input type=submit name="submit" value="検査 (ｽﾍﾟｯｸ:検査項目Excel×PDA:項目ﾏｽﾀ)" style="padding:4px" /><br>
		検査項目のクロスチェックを行います。
	</p>

	<p>
		<input type=button name="back" value="終了" onClick="javascript:history.back()" style="padding:4px" /><br>
		前の画面に戻ります。
	</p>
</form>

</body>
</html>
