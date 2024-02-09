package com.example.mbtiService.handler;


import com.example.mbtiService.entity.Result;
import com.example.mbtiService.entity.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 自定义公共异常处理器
 * 1.声明异常处理器:@ControllerAdvice
 * 2.统一处理异常
 */
@Slf4j
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result zeroError(HttpServletRequest req, HttpServletResponse res, ArithmeticException e) {
        Result result = new Result(ResultCode.FAIL, e.getMessage());
        printLog(e, "除零异常");
        return result;
    }

    @ExceptionHandler(Exception.class) // 指定处理什么样的异常
    @ResponseBody
    public Result error(HttpServletRequest req, HttpServletResponse res, Exception e) {
        Result result = new Result(ResultCode.SERVER_ERROR, e.getMessage());
        printLog(e, "系统异常");
        return result;
    }

    // 统一处理日志
    private void printLog(Exception e, String message) {
        log.error("***** " + message + " *****");
        log.error("" + e);
        e.printStackTrace();
    }
}