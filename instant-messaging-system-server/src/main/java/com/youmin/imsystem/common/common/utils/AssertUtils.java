package com.youmin.imsystem.common.common.utils;

import cn.hutool.core.util.ObjectUtil;

import com.youmin.imsystem.common.common.exception.BusinessException;
import com.youmin.imsystem.common.common.exception.CommonErrorEnum;
import com.youmin.imsystem.common.common.exception.ErrorEnum;
import org.hibernate.validator.HibernateValidator;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.text.MessageFormat;
import java.util.*;

/**
 * Data Validation Util
 */
public class AssertUtils {


    private static Validator failFastValidator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory().getValidator();


    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    public static <T> void fastFailValidate(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = failFastValidator.validate(obj);
        if (constraintViolations.size() > 0) {
            throwException(CommonErrorEnum.PARAM_VALID, constraintViolations.iterator().next().getMessage());
        }
    }


    public static <T> void allCheckValidateThrow(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        if (constraintViolations.size() > 0) {
            StringBuilder errorMsg = new StringBuilder();
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> violation = iterator.next();
                errorMsg.append(violation.getPropertyPath().toString()).append(":").append(violation.getMessage()).append(",");
            }

            throwException(CommonErrorEnum.PARAM_VALID, errorMsg.toString().substring(0, errorMsg.length() - 1));
        }
    }


    public static <T> Map<String, String> allCheckValidate(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        if (constraintViolations.size() > 0) {
            Map<String, String> errorMessages = new HashMap<>();
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> violation = iterator.next();
                errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errorMessages;
        }
        return new HashMap<>();
    }


    public static void isTrue(boolean expression, String msg) {
        if (!expression) {
            throwException(msg);
        }
    }

    public static void isTrue(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (!expression) {
            throwException(errorEnum, args);
        }
    }


    public static void isFalse(boolean expression, String msg) {
        if (expression) {
            throwException(msg);
        }
    }


    public static void isFalse(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (expression) {
            throwException(errorEnum, args);
        }
    }


    public static void isNotEmpty(Object obj, String msg) {
        if (isEmpty(obj)) {
            throwException(msg);
        }
    }


    public static void isNotEmpty(Object obj, ErrorEnum errorEnum, Object... args) {
        if (isEmpty(obj)) {
            throwException(errorEnum, args);
        }
    }


    public static void isEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            throwException(msg);
        }
    }

    public static void equal(Object o1, Object o2, String msg) {
        if (!ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }

    public static void notEqual(Object o1, Object o2, String msg) {
        if (ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }

    private static boolean isEmpty(Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    private static void throwException(String msg) {
        throwException(null, msg);
    }

    private static void throwException(ErrorEnum errorEnum, Object... arg) {
        if (Objects.isNull(errorEnum)) {
            errorEnum = CommonErrorEnum.BUSINESS_ERROR;
        }
        throw new BusinessException(errorEnum.getErrorCode(), MessageFormat.format(errorEnum.getErrorMessage(), arg));
    }


}