package cn.panda.excel.test;

import cn.panda.common.excel.ExcelUtil;
import cn.panda.common.utils.DateUtilsV1;
import cn.panda.common.model.AbstractJob;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @usage       导入导出excel
 * @author      tong.cx
 * @version     0.0.1
 * @datetime    2016/3/29 10:31
 * @copyright   wonhigh.cn
 */

public class PoiTest {
    /* 基于模板，有标题 */
    @Test
    public void testExportExcelByTemplate() {
        List<AbstractJob> jobHandleRequires = new ArrayList<AbstractJob>();
        jobHandleRequires.add(new AbstractJob("dc_retail_pos", "order_main", "02:00", "1", ""));
        Map<String, String> datas = new HashMap<String, String>();
        datas.put("title", "DC调度系统任务报表");
        datas.put("date", DateUtilsV1.getCurDate());
        datas.put("dep", "www.wonhigh.cn");
        ExcelUtil.getInstance().exportExcelByTemplate(datas, "/monJobTemplate.xls",
                "E:/dcTemplateExcel.xls", jobHandleRequires,
                AbstractJob.class, true);
    }

    @Test
    public void testReadExcelByStream() throws FileNotFoundException {
        //InputStream inputStream = new FileInputStream(new File("E:\\job-template\\dcTemplateExcel.xlsx"));
        InputStream inputStream = new FileInputStream(new File("C:\\Users\\user\\Desktop\\调度报表2016-04-15 14_54_06.xlsx"));
        List<Object> stus;
		try {
			stus = ExcelUtil.getInstance().readExcelByStream(inputStream, AbstractJob.class, 1,
			        2);
			 for (Object obj : stus) {
		            AbstractJob jobHandleRequire = (AbstractJob) obj;
		            System.out.println(jobHandleRequire);
		        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }
    @Test
    public void testReadTemplateExcel() {
        List<Object> stus = ExcelUtil.getInstance().readExcelByPath(
                "E:/dcTemplateExcel.xlsx", AbstractJob.class, 1,
                2);
        for (Object obj : stus) {
            AbstractJob jobHandleRequire = (AbstractJob) obj;
            System.out.println(jobHandleRequire);
        }
    }
}
