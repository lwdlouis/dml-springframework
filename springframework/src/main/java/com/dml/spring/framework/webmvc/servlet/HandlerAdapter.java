package com.dml.spring.framework.webmvc.servlet;

import com.dml.spring.framework.annotation.RequestParam;
import com.dml.spring.framework.annotation.ResponseBody;
import com.dml.spring.framework.ui.ConcurrentModel;
import com.dml.spring.framework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof HandlerMapping);
    }


    /**
     * 这就是执行 url 对应的方法，并封装成 ModelAndView 返回
     */
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        SimpleHandlerMapping handlerMapping = (SimpleHandlerMapping) handler;


        /**
         * 1。 获取 Request 的所有入参数
         */
        //获得方法的形参列表
        Map<String, String[]> params = request.getParameterMap();


        /**
         * 2。 获得 controller 方法的所有参数，并将 request 参数转换成 method 参数
         */
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        // 记录 method 方法的参数顺序
        Object[] methodParam = new Object[pa.length];
        Parameter[] methodParams = handlerMapping.getMethod().getParameters();
        ConcurrentModel model = null;

        for (int i = 0; i < methodParams.length ; i ++) {

            Parameter param = methodParams[i];

            if (param.isAnnotationPresent(RequestParam.class)) {
                String paramName = param.getAnnotation(RequestParam.class).value();
                methodParam[i] = param.getType().cast(params.get(paramName)[0]);

            } else if (param.getType().equals(Model.class)) {
                // 自己加的内容， 支持 model
                model = new ConcurrentModel();
                methodParam[i] = model;
            }

        }

//        for (int i = 0; i < pa.length ; i ++) {
//            for(Annotation a : pa[i]){
//                if(a instanceof RequestParam){
//                    String paramName = ((RequestParam) a).value();
//                    if(!"".equals(paramName.trim())){
////                        paramIndexMapping.put(paramName, i);
//                        methodParam[i] = params.get(paramName);
//                    }
//                }
//            }
//        }




        /**
         * 3。 执行 method 方法
         */
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), methodParam);


        /**
         * 4。 处理返回值, 封装成 modelAndView
         */
        ModelAndView mv = null;

        // 如果这个方法有 @ResponseBody 那么就输出成 json
        if (handlerMapping.getMethod().isAnnotationPresent(ResponseBody.class)) {
            mv = new ModelAndView(result);
        } else {
            mv = new ModelAndView(String.valueOf(result), model);
        }

        return mv;
    }

}
