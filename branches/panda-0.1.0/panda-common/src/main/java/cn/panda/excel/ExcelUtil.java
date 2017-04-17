package cn.panda.excel;

import java.io.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

/**
 * @author tong.cx
 * @version 0.0.1
 * @usage POI读取excel工具类
 * @datetime 2016/3/29 10:52
 * @copyright wonhigh.cn
 */
@SuppressWarnings(
        {"rawtypes"})
public class ExcelUtil {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(ExcelUtil.class);
    private static ExcelUtil eu = new ExcelUtil();

    private ExcelUtil() {
    }

    public static ExcelUtil getInstance() {
        return eu;
    }

    /**
     * 处理对象转换为Excel
     *
     * @param template
     * @param objs
     * @param clz
     * @param isClasspath
     * @return
     */
    private ExcelTemplate objToExcel(String template, List objs, Class clz,
                                     boolean isClasspath) {
        ExcelTemplate et = ExcelTemplate.getInstance();
        try {
            if (isClasspath) {
                et.readTemplateByClasspath(template);
            } else {
                et.readTemplateByPath(template);
            }
            List<ExcelHeader> headers = getHeaderList(clz);
            Collections.sort(headers);
            /* 输出标题 */
            et.createRow();
            for (ExcelHeader eh : headers) {
                et.createCell(eh.getTitle());
            }
            /* 输出值 */
            for (Object obj : objs) {
                et.createRow();
                for (ExcelHeader eh : headers) {
                    String mn = getMethodName(eh);
                    /***
                     * 反射方式 传统方式
                     * Method m = clz.getDeclaredMethod(mn);
                     * Object rel = m.invoke(obj);
                     */

                    /* 使用BeanUtil来实现 */
                    et.createCell(BeanUtils.getProperty(obj, mn));
                }
            }
        } catch (IllegalAccessException e) {
            logger.error(String.format(" [ Excel Error ] 数据转换Excel异常 %s", e.getMessage()));
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            logger.error(String.format(" [ Excel Error ] 数据转换Excel异常 %s", e.getMessage()));
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            logger.error(String.format(" [ Excel Error ] 数据转换Excel异常 %s", e.getMessage()));
            e.printStackTrace();
        }
        return et;
    }

    /**
     * 将对象转换为Excel并且导出,该方法是基于模板的导出,导出到一个具体的路径中
     *
     * @param datas       模板中的替换的常量数据
     * @param template    模板路径
     * @param outPath     输出路径
     * @param objs        对象列表
     * @param clz         对象的类型
     * @param isClasspath 模板是否在classPath路径下
     */
    public void exportExcelByTemplate(Map<String, String> datas,
                                      String template, String outPath, List objs, Class clz,
                                      boolean isClasspath) {
        /* 将objs信息写入Excel */
        ExcelTemplate et = objToExcel(template, objs, clz, isClasspath);
        /* Title,Date,Dep信息替换*/
        et.replaceFinalData(datas);
        /* 将Excel写出到outPath*/
        et.writeToFile(outPath);
    }

    /**
     * 导出对象到Excel，不是基于模板的，直接新建一个Excel完成导出，基于路径的导出
     *
     * @param outPath 导出路径
     * @param objs    对象列表
     * @param clz     对象类型
     * @param isXssf  是否是2007版本
     */
    public void exportExcel(String outPath, List objs, Class clz, boolean isXssf) {
        Workbook wb = null;
        FileOutputStream fos = null;
        try {
            if (isXssf) {
                wb = new XSSFWorkbook();
            } else {
                wb = new HSSFWorkbook();
            }
            Sheet sheet = wb.createSheet();
            Row r = sheet.createRow(0);
            List<ExcelHeader> headers = getHeaderList(clz);
            Collections.sort(headers);
            /** 写标题 */
            for (int i = 0; i < headers.size(); i++) {
                r.createCell(i).setCellValue(headers.get(i).getTitle());
            }
            /** 写数据 */
            Object obj = null;
            for (int i = 0; i < objs.size(); i++) {
                r = sheet.createRow(i + 1);
                obj = objs.get(i);
                for (int j = 0; j < headers.size(); j++) {
                    r.createCell(j).setCellValue(
                            BeanUtils.getProperty(obj,
                                    getMethodName(headers.get(j))));
                }
            }
        } catch (IllegalAccessException e) {
            logger.error(String.format(" [ Excel Error ] 导出Excel异常 %s", e.getMessage()));
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            logger.error(String.format(" [ Excel Error ] 导出Excel异常 %s", e.getMessage()));
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            logger.error(String.format(" [ Excel Error ] 导出Excel异常 %s", e.getMessage()));
            e.printStackTrace();
        }

        try {
            fos = new FileOutputStream(outPath);
            wb.write(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 从流文件读取相应的Excel文件到对象列表
     *
     * @param path     类路径下的path
     * @param clz      对象类型
     * @param readLine 开始行，注意是标题所在行
     * @param tailLine 底部有多少行，在读入对象时，会减去这些行
     * @return
     */
    public List<Object> readExcelByStream(InputStream inputStream, Class clz,
                                             int readLine, int tailLine) throws Exception {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(inputStream);
            return excelToObjs(wb, clz, readLine, tailLine);
        } catch (InvalidFormatException e) {
            logger.error(String.format(" [ Excel Error ] 读取Excel异常 %s", e.getMessage()));
            throw new Exception("readExcelByStream() Exception!");
//            e.printStackTrace();
        } catch (Exception e) {
            logger.error(String.format(" [ Excel Error ] 读取Excel异常 %s", e.getMessage()));
            throw new Exception("readExcelByStream() Exception!");
//            e.printStackTrace();
        }
//        return null;
    }
    /**
     * 从类路径读取相应的Excel文件到对象列表
     *
     * @param path     类路径下的path
     * @param clz      对象类型
     * @param readLine 开始行，注意是标题所在行
     * @param tailLine 底部有多少行，在读入对象时，会减去这些行
     * @return
     */
    public List<Object> readExcelByClasspath(String path, Class clz,
                                             int readLine, int tailLine) {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(ExcelUtil.class
                    .getResourceAsStream(path));
            return excelToObjs(wb, clz, readLine, tailLine);
        } catch (InvalidFormatException e) {
            logger.error(String.format(" [ Excel Error ] 读取Excel异常 %s", e.getMessage()));
//            e.printStackTrace();
        } catch (Exception e) {
            logger.error(String.format(" [ Excel Error ] 读取Excel异常 %s", e.getMessage()));
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从文件路径读取相应的Excel文件到对象列表
     *
     * @param path     文件路径下的path
     * @param clz      对象类型
     * @param readLine 开始行，注意是标题所在行
     * @param tailLine 底部有多少行，在读入对象时，会减去这些行
     * @return
     */
    public List<Object> readExcelByPath(String path, Class clz, int readLine,
                                        int tailLine) {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(new FileInputStream(new File(path)));
            return excelToObjs(wb, clz, readLine, tailLine);
        } catch (InvalidFormatException e) {
            logger.error(String.format(" [ Excel Error ] 创建Workbook异常 %s", e.getMessage()));
//            e.printStackTrace();
        } catch (Exception e) {
            logger.error(String.format(" [ Excel Error ] 创建Workbook异常 %s", e.getMessage()));
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从类路径读取相应的Excel文件到对象列表，标题行为0，没有尾行
     *
     * @param path 路径
     * @param clz  类型
     * @return 对象列表
     */
    public List<Object> readExcelByClasspath(String path, Class clz) {
        return this.readExcelByClasspath(path, clz, 0, 0);
    }

    /**
     * 从文件路径读取相应的Excel文件到对象列表，标题行为0，没有尾行
     *
     * @param path 路径
     * @param clz  类型
     * @return 对象列表
     */
    public List<Object> readExcelByPath(String path, Class clz) {
        return this.readExcelByPath(path, clz, 0, 0);
    }


    /**
     * @param wb
     * @param clz
     * @param readLine 开始标题2行
     * @param tailLine 最后时间和地址2行
     * @return
     */
    private List<Object> excelToObjs(Workbook wb, Class clz, int readLine,
                                     int tailLine) throws Exception {
        Sheet sheet = wb.getSheetAt(0);
        List<Object> objs = null;
        try {
            Row row = sheet.getRow(readLine);
            objs = new ArrayList<Object>();
            Map<Integer, String> maps = getHeaderMap(row, clz);
            if (maps == null || maps.size() <= 0) {
                logger.error(String.format(" [ Excel Error ] 读取的Excel的格式不正确,检查是否设定了合适的行"));
                throw new NullPointerException(" [ Excel Error ] 读取的Excel的格式不正确,检查是否设定了合适的行");
//                return null;
            }
            for (int i = readLine + 1; i <= sheet.getLastRowNum() - tailLine; i++) {
                row = sheet.getRow(i);
                Object obj = clz.newInstance();
                for (Cell cell : row) {
                    int ci = cell.getColumnIndex();
                    String mn = maps.get(ci).substring(3);
                    mn = mn.substring(0, 1).toLowerCase() + mn.substring(1);
                    BeanUtils.copyProperty(obj, mn, this.getCellValue(cell));
                }
                objs.add(obj);
            }
        } catch (InstantiationException e) {
            logger.error(String.format(" [ Excel Error ] Excel转换数据异常 %s", e.getMessage()));
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return objs;
    }

    private List<ExcelHeader> getHeaderList(Class clz) {
        List<ExcelHeader> headers = new ArrayList<ExcelHeader>();
        Method[] ms = clz.getDeclaredMethods();
        for (Method m : ms) {
            String mn = m.getName();
            if (mn.startsWith("get")) {
                if (m.isAnnotationPresent(ExcelResources.class)) {
                    ExcelResources er = m.getAnnotation(ExcelResources.class);
                    headers.add(new ExcelHeader(er.title(), er.order(), mn));
                }
            }
        }
        return headers;
    }

    private Map<Integer, String> getHeaderMap(Row titleRow, Class clz) {
        List<ExcelHeader> headers = getHeaderList(clz);
        Map<Integer, String> maps = new HashMap<Integer, String>();
        for (Cell cell : titleRow) {
            String title = cell.getStringCellValue();
            for (ExcelHeader eh : headers) {
                if (eh.getTitle().equals(title.trim())) {
                    maps.put(cell.getColumnIndex(),
                            eh.getMethodName().replace("get", "set"));
                    break;
                }
            }
        }
        return maps;
    }

    private String getCellValue(Cell cell) {
        String cellValue = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            default:
                cellValue = null;
                break;
        }
        return cellValue;
    }

    /**
     * 根据标题获取相应的方法名称
     *
     * @param eh
     * @return
     */
    private String getMethodName(ExcelHeader eh) {
        String mn = eh.getMethodName().substring(3);
        mn = mn.substring(0, 1).toLowerCase() + mn.substring(1);
        return mn;
    }
}

