package com.tfit.BdBiProcSrvShEduOmc.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.ApiExceptionResult;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.ExceptionContent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;

/**
 * @Descritpion：异常转换器(异常信息转换为ApiErrorResult)
 * @author: tianfang_infotech
 * @date: 2019/1/3 15:44
 */
@ControllerAdvice
public class ExceptionTranslate {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler
    public @ResponseBody
    ApiExceptionResult<?> translateHandler(Throwable e) {

        logger.error("An exception occurred: ", e);

        String errorMsg = e.getMessage();
        String exceptionMsg = StringUtils.isNotBlank(errorMsg) ? errorMsg : ("未知名的异常: " + errorMsg);

        if (e instanceof MethodArgumentNotValidException) {

            MethodArgumentNotValidException argumentNotValidException = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = argumentNotValidException.getBindingResult();
            List<ObjectError> errors = bindingResult.getAllErrors();

            for (ObjectError objectError : errors) {
                if (objectError instanceof FieldError) {

                    FieldError fieldError = (FieldError) objectError;
                    errorMsg = objectError.getDefaultMessage();

                    String[] codes = fieldError.getCodes();
                    for (String code : codes) {
                        if (code.startsWith("typeMismatch")) {
                            errorMsg = " 数据格式非法";
                        }
                    }

                    return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal, fieldError.getField() + " : " + errorMsg));
                }
            }
        } else if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException formatException = (InvalidFormatException) e.getCause();
            List<JsonMappingException.Reference> references = formatException.getPath();
            if (null != references && !references.isEmpty()) {
                for (JsonMappingException.Reference reference : references) {
                    if (reference.getFieldName() != null) {

                        return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal, reference.getFieldName() + " : " + " 数据格式非法"));
                    }
                }
            } else {

                return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal, "数据格式非法"));
            }

        } else if (e instanceof org.springframework.validation.BindException) {
            org.springframework.validation.BindException bindException = (org.springframework.validation.BindException) e;
            List<ObjectError> errors = bindException.getAllErrors();
            if (null != errors && !errors.isEmpty()) {
                FieldError fieldError = (FieldError) errors.get(0);

                return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal, fieldError.getField() + " : " + errors.get(0).getDefaultMessage()));
            }
        } else if (e instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) e;
            try {
                String fieldName = constraintViolationException.getConstraintViolations().iterator().next().getPropertyPath().toString().substring(constraintViolationException.getConstraintViolations().iterator().next().getPropertyPath().toString().indexOf("["), constraintViolationException.getConstraintViolations().iterator().next().getPropertyPath().toString().length());

                return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal.getCode(),
                        fieldName + " : " + constraintViolationException.getConstraintViolations().iterator().next().getMessage()));
            } catch (Exception e1) {

                return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal.getCode(), "参数非法"));
            }
        } else if (e instanceof HttpMessageNotReadableException) {

            return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.HTTP_REQUEST_EXCEPTION.getCode(), "请求参数异常"));
        } else if (e instanceof HttpRequestMethodNotSupportedException) {

            return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.HTTP_REQUEST_EXCEPTION.getCode(), "请求方法不正确"));
        }
        //业务异常
        else if (e instanceof BusinessException) {

            return new ApiExceptionResult<>(new ExceptionContent(((BusinessException) e).getCode(), errorMsg));
        }
        //入参非法异常
        else if (e.getCause() instanceof IllegalArgumentException) {
            String fieldName = "";
            if (e instanceof TypeMismatchException) {
                MethodArgumentTypeMismatchException argumentTypeMismatchException = (MethodArgumentTypeMismatchException) e;
                fieldName = argumentTypeMismatchException.getName();
            }
            return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal.getCode(), "参数:" + fieldName + " 非法"));

        } else if (e instanceof MissingServletRequestParameterException) {

            MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) e;

            return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal.getCode(), " 缺失入参属性: " + missingServletRequestParameterException.getParameterName()));
        }
        //业务非法异常
        else if (e instanceof IllegalArgsException) {

            return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal.getCode(), e.getMessage()));
            //能过body等传过来的参数等非法校验
        } else if (e.getCause() instanceof InvalidFormatException) {

            InvalidFormatException invalidFormatException = (InvalidFormatException) e.getCause();
            if (invalidFormatException.getPath().size() < 1) {

                return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.UNKNOW_EXCEPTION.getCode(), exceptionMsg));
            }
            return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.Param_VisitIllegal.getCode(), invalidFormatException.getPath().get(0).getFieldName() + " : 数据格式非法"));

        } else if (e.getCause() instanceof SQLSyntaxErrorException) {

            return databaseException();
        } else if (e.getCause() instanceof SQLException) {

            return databaseException();
        } else if (e instanceof UncategorizedDataAccessException) {

            return databaseException();
        }

        return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.UNKNOW_EXCEPTION.getCode(), exceptionMsg));
    }

    private ApiExceptionResult<?> databaseException() {
        return new ApiExceptionResult<>(new ExceptionContent(IOTRspType.DATABASE_OPS_EXCEPTION));
    }
}
