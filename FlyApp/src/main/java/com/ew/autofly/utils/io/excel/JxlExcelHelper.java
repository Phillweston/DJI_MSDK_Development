package com.ew.autofly.utils.io.excel;

import android.text.TextUtils;

import com.ew.autofly.utils.ReflectUtil;
import com.ew.autofly.utils.coordinate.CoordConvertManager;
import com.ew.autofly.xflyer.utils.DateHelperUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class JxlExcelHelper extends BaseExcelHelper {

    private static JxlExcelHelper instance = null;

    private File file;

    /**
     * 私有化构造方法
     *
     * @param file 文件对象
     */
    public JxlExcelHelper(File file) {
        super();
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * 获取单例对象并进行初始化
     *
     * @param file 文件对象
     * @return 返回初始化后的单例对象
     */
    public static JxlExcelHelper getInstance(File file) {
        if (instance == null) {

            synchronized (JxlExcelHelper.class) {

                if (instance == null) {
                    instance = new JxlExcelHelper(file);
                }
            }
        } else {

            if (!file.equals(instance.getFile())) {
                instance.setFile(file);
            }
        }
        return instance;
    }

    /**
     * 获取单例对象并进行初始化
     *
     * @param filePath 文件路径
     * @return 返回初始化后的单例对象
     */
    public static JxlExcelHelper getInstance(String filePath) {
        return getInstance(new File(filePath));
    }

    public <T> T readExcelFirstRow(Class<T> clazz, String[] fieldNames,
                                   int sheetNo, boolean hasTitle) throws Exception {
        T target = null;


        WorkbookSettings workbookSettings = new WorkbookSettings();
        Workbook workbook = Workbook.getWorkbook(file, workbookSettings);

        try {
            Sheet sheet = workbook.getSheet(sheetNo);
            int start = hasTitle ? 1 : 0;

            for (int i = start; i < sheet.getRows(); i++) {
                target = getContent(sheet, i, fieldNames, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

        return target;
    }

    @Override
    public <T> List<T> readExcel(Class<T> clazz, String[] fieldNames,
                                 int sheetNo, boolean hasTitle) throws Exception {
        List<T> dataModels = new ArrayList<T>();

        WorkbookSettings workbookSettings = new WorkbookSettings();
        Workbook workbook = Workbook.getWorkbook(file, workbookSettings);

        try {
            Sheet sheet = workbook.getSheet(sheetNo);
            int start = hasTitle ? 1 : 0;
            for (int i = start; i < sheet.getRows(); i++) {
                dataModels.add(getContent(sheet, i, fieldNames, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return dataModels;
    }

    @Override
    public <T> void writeExcel(Class<T> clazz, List<T> dataModels,
                               String[] fieldNames, String[] titles) throws Exception {
        WritableWorkbook workbook = null;
        try {

            if (file.exists()) {
                Workbook book = Workbook.getWorkbook(file);
                workbook = Workbook.createWorkbook(file, book);
            } else {
                workbook = Workbook.createWorkbook(file);
            }

            int sheetNo = workbook.getNumberOfSheets() + 1;
            String sheetName = DateHelperUtils.format(new Date(), "yyyyMMddHHmmssSS");
            WritableSheet sheet = workbook.createSheet(sheetName, sheetNo);

            for (int i = 0; i < titles.length; i++) {

                WritableFont font = new WritableFont(WritableFont.ARIAL, 10,
                        WritableFont.BOLD);
                WritableCellFormat format = new WritableCellFormat(font);

                format.setWrap(true);
                Label label = new Label(i, 0, titles[i], format);
                sheet.addCell(label);

                sheet.setColumnView(i, titles[i].length() + 10);
            }
            Field[] fields = clazz.getDeclaredFields();


            for (int i = 0; i < dataModels.size(); i++) {

                for (int j = 0; j < fieldNames.length; j++) {


                    String fieldName = fieldNames[j];
                    if (fieldName == null || UID.equals(fieldName)) {
                        continue;
                    }

                    for (Field field : fields) {

                        if (fieldName.equals(field.getName())) {

                            T target = dataModels.get(i);
                            Object result = ReflectUtil.invokeGetter(target, fieldName);
                            Label label;
                            if (result == null) {
                                label = new Label(j, i + 1, "");
                            } else {
                                label = new Label(j, i + 1,
                                        String.valueOf(result));
                            }

                            if (isDateType(clazz, fieldName)) {
                                label.setString(DateHelperUtils.format((Date) result));
                            }
                            sheet.addCell(label);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.write();
                workbook.close();
            }
        }
    }

    public List<String> getTitle() {
        Workbook workbook = null;
        List<String> titleList = new ArrayList<>();
        try {
            workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);

            int count = 0;
            Cell cell = null;
            while ((cell = sheet.getCell(count, 0)) != null) {
                String title = cell.getContents();
                if (!TextUtils.isEmpty(title) && !titleList.contains(title)) {
                    titleList.add(title);
                }
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return titleList;
    }

    public <T> T getContent(Sheet sheet, int row, String[] fieldNames, Class<T> clazz) throws InvocationTargetException,

            NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InstantiationException {

        T target = clazz.newInstance();

        try {
            for (int j = 0; j < fieldNames.length; j++) {
                String fieldName = fieldNames[j];
                if (fieldName == null || UID.equals(fieldName)) {
                    continue;
                }

                Cell cell = sheet.getCell(j, row);


                String content = cell.getContents();


                if (isDateType(clazz, fieldName)) {

                    ReflectUtil.invokeSetter(target, fieldName,
                            DateHelperUtils.string2DateTime(content));
                } else {
                    Field field = clazz.getDeclaredField(fieldName);

                    if (cell.getType() == CellType.NUMBER && j >= 4) {
                        NumberCell numberCell = (NumberCell) cell;
                        double value = numberCell.getValue();
                        if (field.getType() == Integer.TYPE) {
                            content = (int) value + "";
                        } else {
                            content = value + "";
                        }
                    }




                    if (fieldName.equals("latitude") || fieldName.equals("longitude")) {
                        if (content.contains("°") && content.contains("′")) {

                            content = content.replace("°", "-");
                            content = content.replace("′", "-");
                            content = content.replace("″", "-");
                            String format[] = content.split("-");
                            if (format.length == 3) {

                                double numFormat[] = new double[3];
                                for (int k = 0; k < format.length; k++) {
                                    numFormat[k] = Double.parseDouble(format[k]);
                                }
                                content = CoordConvertManager.convertToSimpleFormat(numFormat[0], numFormat[1], numFormat[2]) + "";
                            }
                        }
                    }


                    ReflectUtil.invokeSetter(target, fieldName,
                            parseValueWithType(content + "", field.getType()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }

}
