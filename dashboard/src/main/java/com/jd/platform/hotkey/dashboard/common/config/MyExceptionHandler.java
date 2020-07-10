package com.jd.platform.hotkey.dashboard.common.config;

import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.common.ex.BizException;
import com.jd.platform.hotkey.dashboard.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;


/**
 * @author liyunfeng31
 */
@ControllerAdvice
public class MyExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(value = BizException.class)
	@ResponseBody
	public Result bizExceptionHandler(BizException e, HttpServletResponse resp){
		resp.setStatus(e.getCode());
		logger.info("业务异常：",e);
		return Result.error(e.getCode(),e.getMsg());

	}


	@ExceptionHandler(value =Exception.class)
	@ResponseBody
	public Result exceptionHandler(Exception e){
		logger.info("未知异常：",e);
		return Result.error(ResultEnum.BIZ_ERROR);
	}


	public static void main(String[] args) {
		Random rd = new Random();

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter("D:/tmp.txt");//创建文本文件
			// yyMMddHHmm
			LocalDateTime time2 = DateUtil.strToLdt("2006140600", DateUtil.PATTERN_MINUS);
			for (int i = 0; i < 160000; i++) {
				int count = rd.nextInt(300);
				int day = DateUtil.reviseTime(time2,i,1)/100/100;
				int hour = DateUtil.reviseTime(time2,i,1)/100;
				int ms = DateUtil.reviseTime(time2,i,1);
				LocalDateTime ct = DateUtil.strToLdt(ms+"", DateUtil.PATTERN_MINUS);
				String sql ="INSERT INTO `hk_statistics` VALUES ("+(i+55000)+", 'rule1', "+count+", 'app1', 'rule1', " +
					""+day+", "+hour+", "+ms+", 5, "+(i+55000)+", \""+ ct.toString().replace("T"," ")+"\" );";
				System.out.println(sql);
				fileWriter.write(sql+"\r\n");//写入 \r\n换行
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	/*	LocalDateTime time = DateUtil.strToLdt("20060200", DateUtil.PATTERN_HOUR);
		for (int i = 0; i < 3000; i++) {
			int count = rd.nextInt(500);
			int day = DateUtil.reviseTime(time,i,2)/100;
			int hour = DateUtil.reviseTime(time,i,2);
			LocalDateTime ct = DateUtil.strToLdt(hour+"", DateUtil.PATTERN_HOUR);
			String sql ="INSERT INTO `hk_statistics` VALUES ("+i+", 'rule1', "+count+", 'app1', 'rule1', " +
					""+day+", "+hour+", 0, 6, "+i+", \""+ ct.toString().replace("T"," ")+"\" );";
			System.out.println(sql);
		}*/


	}
}
