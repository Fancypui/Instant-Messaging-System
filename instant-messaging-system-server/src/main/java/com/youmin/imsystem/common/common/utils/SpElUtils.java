package com.youmin.imsystem.common.common.utils;

import com.youmin.imsystem.common.common.annotation.RedissonLock;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Spring EL expression parser
 */
public class SpElUtils {

    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer parameterNameDiscover = new DefaultParameterNameDiscoverer();

    public static String getMethodKey(Method method){
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        return className+"#"+methodName;
    }

    public static String getKey(String key, Method method, Object[] args){
        //get parameterName
        String[] parameterNames = Optional.ofNullable(parameterNameDiscover.getParameterNames(method)).orElse(new String[]{});
        EvaluationContext context = new StandardEvaluationContext();//context that requires by el expression parser
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i],args[i]);
        }
        Expression expression = parser.parseExpression(key);
        return expression.getValue(context,String.class);
    }
}
