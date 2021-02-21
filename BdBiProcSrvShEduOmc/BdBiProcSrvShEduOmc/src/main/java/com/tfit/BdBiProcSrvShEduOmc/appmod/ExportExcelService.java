package com.tfit.BdBiProcSrvShEduOmc.appmod;

import javax.servlet.http.HttpServletResponse;

/**
 * 导出Excel文档
 * @author chenchang
 * @date 20180922
 */
public interface ExportExcelService {

	/**
	 * 导出
	 * @param response
	 * @param jsonParams
	 * @throws Exception
	 */
    void doExport(HttpServletResponse response, String key) throws Exception;
}
