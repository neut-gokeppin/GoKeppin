package jp.co.ctc.components {

	import mx.controls.Label;
	import mx.controls.dataGridClasses.*;
	import mx.controls.DataGrid;
	import flash.display.Graphics;
	import jp.co.ctc.entity.MBcsignDTO;

	/**
	 * 編集区分が"I"(新規追加),"U"(変更)のセルの背景を赤色に設定する。
	 * @author CJ00971
	 *
	 */
	public class BackgroundColorItemRenderer extends Label
	{
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);

			var g:Graphics = graphics;

			if (data==null) {
				return;
			}

			// 現在の描画状態をクリアする。（列幅を小さくした時に描画されない領域が発生するため）
			g.clear();
			
			// 2016/02/24 DA ins start
			// 指示記号マスタ、行の削除の場合
			if (parentApplication.hasOwnProperty("isBcsignMainte") && parentApplication.isBcsignMainte == true) {
				var mBcsignDTO:MBcsignDTO = this.data as MBcsignDTO;
				if (mBcsignDTO.sopDeleteFlag == "1"){
					g.beginFill(resourceManager.getInt('resource','bcsignSopDeleteColor'));
					g.drawRect(0, 0, unscaledWidth, unscaledHeight);
					g.endFill();
					return;
				}
			}
			// 2016/02/24 DA ins end
			
			if (data.editDiv != null && data.editDiv != "M") {
				// 変更区分がnull、"M"(移動)でなければ背景を赤色
				g.beginFill(0xFFB6C1);
				g.drawRect(0, 0, unscaledWidth, unscaledHeight);
				g.endFill();
			}
			else {
				//g.beginFill(0xFFFFFF);
				//g.drawRect(0, 0, unscaledWidth, unscaledHeight);
				//g.endFill();
				// 通常の色に戻す。
				g.clear();
			}
		}
	}
}
