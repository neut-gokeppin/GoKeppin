/**
 *
 */
package jp.co.ctc.action;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.struts.upload.FormFile;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.struts.annotation.Execute;

/**
 * テスト用。 http://localhost:8400/lfa_inspection/index
 *
 * @author CJ01615
 *
 */
public class IndexAction {

	/** リクエストパラメータで上書き */
	public String str = "";

	/** リクエストパラメータで上書き */
	public FormFile file = null;

	/** jspに渡す変数 */
	public String newStr = "";


	/**
	 * s2strutsサンプル
	 * http://localhost:8400/lfa_inspection/index/
	 *
	 * @return index.jsp
	 */
	@Execute(validator = false)
	public String index() {
		newStr = str.length() + "";

		if (file != null) {
			String path = "C:\\共有\\upload\\" + file.getFileName();
	    	OutputStream out;
			try {
				out = new BufferedOutputStream(new FileOutputStream(path));
		        try {
		            out.write(file.getFileData(), 0, file.getFileSize());
		        } finally {
		            out.close();
		        }
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}

		return "index.jsp";
	}
}
