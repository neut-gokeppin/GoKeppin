package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.util.List;

import jp.co.ctc.entity.LgMSupplier;
import jp.co.ctc.service.LgMSupplierService;

import org.seasar.framework.container.SingletonS2Container;

/**
 * @author CJ01786
 *
 */
public class Supplier extends CreateResponse {

	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.CreateResponse#getResponse(java.io.ByteArrayInputStream)
	 */
	@Override
	public String getResponse(ByteArrayInputStream inputStream) {
		LgMSupplierService srvSupplier = SingletonS2Container.getComponent(LgMSupplierService.class);
		List<LgMSupplier> resSupplier = srvSupplier.getMSuppliers();

		this.srvXmlWriter.createDataset();

		for (LgMSupplier mSupplier : resSupplier) {
			this.srvXmlWriter.ceateTable();
			this.srvXmlWriter.addData("supplierCode", mSupplier.supplierCode);
			this.srvXmlWriter.addData("supplierName", mSupplier.supplierName);
		}

		return this.srvXmlWriter.getXMLData();
	}

}
