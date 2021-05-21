package top.luhancc.saas.ihrm.employee.controller;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import top.luhancc.hrm.common.controller.BaseController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.utils.BeanMapUtils;
import top.luhancc.hrm.common.utils.DownloadUtils;
import top.luhancc.saas.hrm.common.model.employee.UserCompanyJobs;
import top.luhancc.saas.hrm.common.model.employee.UserCompanyPersonal;
import top.luhancc.saas.hrm.common.model.employee.response.EmployeeReportResult;
import top.luhancc.saas.ihrm.employee.service.UserCompanyJobsService;
import top.luhancc.saas.ihrm.employee.service.UserCompanyPersonalService;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工信息controller
 */
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController extends BaseController<UserCompanyPersonal, UserCompanyPersonalService> {
    private final UserCompanyJobsService userCompanyJobsService;

    /**
     * 打印员工pdf报表x
     */
    @RequestMapping(value = "/{userId}/pdf", method = RequestMethod.GET)
    public void pdf(@PathVariable String userId) throws IOException {
        //1.引入jasper文件
        Resource resource = new ClassPathResource("templates/profile.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());

        //2.构造数据
        //a.用户详情数据
        UserCompanyPersonal personal = service.findById(userId);
        //b.用户岗位信息数据
        UserCompanyJobs jobs = userCompanyJobsService.findById(userId);
        //c.用户头像        域名 / userId
        String staffPhoto = "http://pkbivgfrm.bkt.clouddn.com/" + userId;

        System.out.println(staffPhoto);

        //3.填充pdf模板数据，并输出pdf
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> map1 = BeanMapUtils.beanToMap(personal);
        Map<String, Object> map2 = BeanMapUtils.beanToMap(jobs);
        params.putAll(map1);
        params.putAll(map2);
        params.put("staffPhoto", "staffPhoto");

        ServletOutputStream os = response.getOutputStream();
        try {
            JasperPrint print = JasperFillManager.fillReport(fis, params, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(print, os);
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
        }
    }

    /**
     * 员工个人信息保存
     */
    @RequestMapping(value = "/{userId}/personalInfo", method = RequestMethod.PUT)
    public Result<Void> savePersonalInfo(@PathVariable(name = "userId") String uid, @RequestBody UserCompanyPersonal userCompanyPersonal) throws Exception {
        userCompanyPersonal.setUserId(uid);
        userCompanyPersonal.setCompanyId(super.companyId);
        service.save(userCompanyPersonal);
        return Result.success();
    }

    /**
     * 员工个人信息读取
     */
    @RequestMapping(value = "/{userId}/personalInfo", method = RequestMethod.GET)
    public Result<UserCompanyPersonal> findPersonalInfo(@PathVariable(name = "userId") String uid) throws Exception {
        UserCompanyPersonal info = service.findById(uid);
        return Result.success(info);
    }

    /**
     * 员工岗位信息保存
     */
    @RequestMapping(value = "/{userId}/jobs", method = RequestMethod.PUT)
    public Result<Void> saveJobsInfo(@PathVariable(name = "userId") String uid, @RequestBody UserCompanyJobs sourceInfo) throws Exception {
        //更新员工岗位信息
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyJobs();
            sourceInfo.setUserId(uid);
            sourceInfo.setCompanyId(super.companyId);
        }
        userCompanyJobsService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工岗位信息读取
     */
    @RequestMapping(value = "/{userId}/jobs", method = RequestMethod.GET)
    public Result findJobsInfo(@PathVariable(name = "userId") String uid) throws Exception {
        UserCompanyJobs info = userCompanyJobsService.findById(uid);
        if (info == null) {
            info = new UserCompanyJobs();
            info.setUserId(uid);
            info.setCompanyId(companyId);
        }
        return new Result(ResultCode.SUCCESS, info);
    }


//    /**
//     * 当月人事报表导出
//     *  参数：
//     *      年月-月（2018-02%）
//     */
//    @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
//    public void export(@PathVariable String month) throws Exception {
//        //1.获取报表数据
//        List<EmployeeReportResult> list = userCompanyPersonalService.findByReport(companyId,month);
//        //2.构造Excel
//        //创建工作簿
//        Workbook wb = new XSSFWorkbook();
//        //构造sheet
//        Sheet sheet = wb.createSheet();
//        //创建行
//        //标题
//        String [] titles = "编号,姓名,手机,最高学历,国家地区,护照号,籍贯,生日,属相,入职时间,离职类型,离职原因,离职时间".split(",");
//        //处理标题
//
//        Row row = sheet.createRow(0);
//
//        int titleIndex=0;
//        for (String title : titles) {
//            Cell cell = row.createCell(titleIndex++);
//            cell.setCellValue(title);
//        }
//
//        int rowIndex = 1;
//        Cell cell=null;
//        for (EmployeeReportResult employeeReportResult : list) {
//            row = sheet.createRow(rowIndex++);
//            // 编号,
//            cell = row.createCell(0);
//            cell.setCellValue(employeeReportResult.getUserId());
//            // 姓名,
//            cell = row.createCell(1);
//            cell.setCellValue(employeeReportResult.getUsername());
//            // 手机,
//            cell = row.createCell(2);
//            cell.setCellValue(employeeReportResult.getMobile());
//            // 最高学历,
//            cell = row.createCell(3);
//            cell.setCellValue(employeeReportResult.getTheHighestDegreeOfEducation());
//            // 国家地区,
//            cell = row.createCell(4);
//            cell.setCellValue(employeeReportResult.getNationalArea());
//            // 护照号,
//            cell = row.createCell(5);
//            cell.setCellValue(employeeReportResult.getPassportNo());
//            // 籍贯,
//            cell = row.createCell(6);
//            cell.setCellValue(employeeReportResult.getNativePlace());
//            // 生日,
//            cell = row.createCell(7);
//            cell.setCellValue(employeeReportResult.getBirthday());
//            // 属相,
//            cell = row.createCell(8);
//            cell.setCellValue(employeeReportResult.getZodiac());
//            // 入职时间,
//            cell = row.createCell(9);
//            cell.setCellValue(employeeReportResult.getTimeOfEntry());
//            // 离职类型,
//            cell = row.createCell(10);
//            cell.setCellValue(employeeReportResult.getTypeOfTurnover());
//            // 离职原因,
//            cell = row.createCell(11);
//            cell.setCellValue(employeeReportResult.getReasonsForLeaving());
//            // 离职时间
//            cell = row.createCell(12);
//            cell.setCellValue(employeeReportResult.getResignationTime());
//        }
//        //3.完成下载
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        wb.write(os);
//        new DownloadUtils().download(os,response,month+"人事报表.xlsx");
//    }

    /**
     * 采用模板打印的形式完成报表生成
     * 模板
     * 参数：
     * 年月-月（2018-02%）
     */
    @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable String month) throws Exception {
        //1.获取报表数据
        List<EmployeeReportResult> list = service.findByReport(companyId, month);

        //2.加载模板
        Resource resource = new ClassPathResource("excel-template/hr-demo.xlsx");
        FileInputStream fis = new FileInputStream(resource.getFile());

        //3.根据模板创建工作簿
        Workbook wb = new XSSFWorkbook(fis);
        //4.读取工作表
        Sheet sheet = wb.getSheetAt(0);
        //5.抽取公共样式
        Row row = sheet.getRow(2);
        CellStyle styles[] = new CellStyle[row.getLastCellNum()];
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            styles[i] = cell.getCellStyle();
        }
        //6.构造单元格
        int rowIndex = 2;
        Cell cell = null;
        for (EmployeeReportResult employeeReportResult : list) {
            row = sheet.createRow(rowIndex++);
//            for(int j=0;j<styles.length;j++) {
//                cell = row.createCell(j);
//                cell.setCellStyle(styles[j]);
//            }

            // 编号,
            cell = row.createCell(0);
            cell.setCellValue(employeeReportResult.getUserId());
            cell.setCellStyle(styles[0]);
            // 姓名,
            cell = row.createCell(1);
            cell.setCellValue(employeeReportResult.getUsername());
            cell.setCellStyle(styles[1]);
            // 手机,
            cell = row.createCell(2);
            cell.setCellValue(employeeReportResult.getMobile());
            cell.setCellStyle(styles[2]);
            // 最高学历,
            cell = row.createCell(3);
            cell.setCellValue(employeeReportResult.getTheHighestDegreeOfEducation());
            cell.setCellStyle(styles[3]);
            // 国家地区,
            cell = row.createCell(4);
            cell.setCellValue(employeeReportResult.getNationalArea());
            cell.setCellStyle(styles[4]);
            // 护照号,
            cell = row.createCell(5);
            cell.setCellValue(employeeReportResult.getPassportNo());
            cell.setCellStyle(styles[5]);
            // 籍贯,
            cell = row.createCell(6);
            cell.setCellValue(employeeReportResult.getNativePlace());
            cell.setCellStyle(styles[6]);
            // 生日,
            cell = row.createCell(7);
            cell.setCellValue(employeeReportResult.getBirthday());
            cell.setCellStyle(styles[7]);
            // 属相,
            cell = row.createCell(8);
            cell.setCellValue(employeeReportResult.getZodiac());
            cell.setCellStyle(styles[8]);
            // 入职时间,
            cell = row.createCell(9);
            cell.setCellValue(employeeReportResult.getTimeOfEntry());
            cell.setCellStyle(styles[9]);
            // 离职类型,
            cell = row.createCell(10);
            cell.setCellValue(employeeReportResult.getTypeOfTurnover());
            cell.setCellStyle(styles[10]);
            // 离职原因,
            cell = row.createCell(11);
            cell.setCellValue(employeeReportResult.getReasonsForLeaving());
            cell.setCellStyle(styles[11]);
            // 离职时间
            cell = row.createCell(12);
            cell.setCellValue(employeeReportResult.getResignationTime());
            cell.setCellStyle(styles[12]);
        }
        //7.下载
        //3.完成下载
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        wb.write(os);
        new DownloadUtils().download(os, response, month + "人事报表.xlsx");
    }
}



