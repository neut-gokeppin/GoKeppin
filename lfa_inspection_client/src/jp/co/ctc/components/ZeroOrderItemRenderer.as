package jp.co.ctc.components
{
	import flash.display.Graphics;

	import mx.controls.DataGrid;
	import mx.controls.Label;
	import mx.controls.dataGridClasses.*;

	/**
	 * 順序が「0（未設定）」のセルの背景に色をつける。
	 * @author CJ00971
	 *
	 */
	public class ZeroOrderItemRenderer extends Label
	{
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);

			var g:Graphics = graphics;
			//			var grid:DataGrid = DataGrid(DataGridListData(listData).owner);

			//			if (listData!=null) {
			//				grid = DataGrid(DataGridListData(listData).owner);
			//			}

			if (data==null) {
				return;
			}

			// 現在の描画状態をクリアする。（列幅を小さくした時に描画されない領域が発生するため）
			g.clear();
			
			var setColorFlg:Boolean = false;
			if (data.hasOwnProperty("takeOrder") && data.takeOrder == 0) {
				setColorFlg = true;
			} else if (data.hasOwnProperty("inspecOrder") && data.inspecOrder == 0) {
				setColorFlg = true;
			}
			if (setColorFlg) {
				// 順序が０の場合背景を設定
				g.beginFill(0xC1FFB6);
				g.drawRect(0, 0, unscaledWidth, unscaledHeight);
				g.endFill();
			}
			else {
				//０でない場合通常の色に戻す。
				g.clear();
			}
		}
	}
}