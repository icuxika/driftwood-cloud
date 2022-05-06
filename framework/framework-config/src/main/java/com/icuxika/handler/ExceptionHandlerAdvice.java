package com.icuxika.handler;

import com.icuxika.common.ApiData;
import com.icuxika.common.ApiStatusCode;
import com.icuxika.exception.GlobalServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    /**
     * 全局异常兜底处理
     */
    @ExceptionHandler(Exception.class)
    public ApiData<Void> handleException(Exception e) {
        ApiData<Void> apiData = new ApiData<>();
        apiData.setCode(ApiStatusCode.EXCEPTION.getCode());
        apiData.setMsg(e.getMessage());
        return apiData;
    }

    /**
     * 业务异常消息
     */
    @ExceptionHandler(GlobalServiceException.class)
    public ApiData<Void> handleException(GlobalServiceException e) {
        ApiData<Void> apiData = new ApiData<>();
        apiData.setCode(e.getStatusCode());
        apiData.setMsg(e.getMessage());
        return apiData;
    }

    /**
     * [@RequestParam]注解的参数不存在
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiData<Void> handleException(MissingServletRequestParameterException e) {
        ApiData<Void> apiData = new ApiData<>();
        apiData.setCode(ApiStatusCode.PARAMETER_ERROR.getCode());
        apiData.setMsg(MessageFormat.format("缺少参数{0}，类型[{1}]", e.getParameterName(), e.getParameterType()));
        return apiData;
    }

    /**
     * [POST/GET]单参数验证未通过，必须在Controller类上加上@Validated注解，否则在参数上无效
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiData<Void> handleException(ConstraintViolationException e) {
        ApiData<Void> apiData = new ApiData<>();
        apiData.setCode(ApiStatusCode.PARAMETER_ERROR.getCode());
        apiData.setMsg(e.getMessage());
        return apiData;
    }

    /**
     * [GET]对象参数验证未通过
     */
    @ExceptionHandler(BindException.class)
    public ApiData<Void> handleException(BindException e) {
        ApiData<Void> apiData = new ApiData<>();
        apiData.setCode(ApiStatusCode.PARAMETER_ERROR.getCode());
        apiData.setMsg(parseErrors(e.getBindingResult()).toString());
        return apiData;
    }

    /**
     * [POST] 对象参数验证未通过
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiData<Void> handleException(MethodArgumentNotValidException e) {
        ApiData<Void> apiData = new ApiData<>();
        apiData.setCode(ApiStatusCode.PARAMETER_ERROR.getCode());
        apiData.setMsg(parseErrors(e.getBindingResult()).toString());
        return apiData;
    }

    private Map<String, String> parseErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream().collect(Collectors.toMap(objectError -> ((FieldError) objectError).getField(), objectError -> {
            if (objectError.getDefaultMessage() != null) {
                return objectError.getDefaultMessage();
            } else {
                return "参数校验失败";
            }
        }));
    }
}
