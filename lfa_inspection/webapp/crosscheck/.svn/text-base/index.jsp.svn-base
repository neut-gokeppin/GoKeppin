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

<p>マスタ種類を選択し、クロスチェックを行うほうのボタンをクリックしてください。</p>
<br>

<form method=POST>
	<fieldset>
		<legend>スペックシート　マスタ種類</legend>
		&nbsp;&nbsp;&nbsp;&nbsp;<label><input type=radio name="tlcodeSpec" value="0" />仮0</label>
		&nbsp;&nbsp;&nbsp;&nbsp;<label><input type=radio name="tlcodeSpec" value="1" />仮1</label>
		&nbsp;&nbsp;&nbsp;&nbsp;<label><input type=radio name="tlcodeSpec" value="L" checked />本番</label>
	</fieldset>
	<br>

	<fieldset>
		<legend>ＰＤＡ　マスタ種類</legend>
		&nbsp;&nbsp;&nbsp;&nbsp;<label><input type=radio name="tlcodePda" value="0" />仮0</label>
		<!-- 2017/03/02 CT del start
		&nbsp;&nbsp;&nbsp;&nbsp;<label><input type=radio name="tlcodePda" value="-1" />仮1</label>
		     2017/03/02 CT del end -->
		&nbsp;&nbsp;&nbsp;&nbsp;<label><input type=radio name="tlcodePda" value="1" checked />本番</label>
	</fieldset>
	<br>

	<p>
		<input type=submit name="submit" value="検査項目 (ｽﾍﾟｯｸ:BCﾌｫｰﾏｯﾄﾏｽﾀ×PDA:検査項目ﾏｽﾀ)" style="padding:4px" /><br>
		検査項目のクロスチェックを行います。
	</p>

	<p>
		<input type=submit name="submit" value="検査指示 (ｽﾍﾟｯｸ:貼紙記号変換ﾏｽﾀ×PDA:指示記号ﾏｽﾀ)" style="padding:4px" /><br>
		検査指示のクロスチェックを行います。
	</p>

	<p>
		<input type=button name="back" value="終了" onClick="javascript:history.back()" style="padding:4px" /><br>
		前の画面に戻ります。
	</p>
</form>

 </body>
</html>
