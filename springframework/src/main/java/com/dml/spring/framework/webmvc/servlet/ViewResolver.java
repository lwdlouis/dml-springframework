package com.dml.spring.framework.webmvc.servlet;

import lombok.Data;

import java.io.File;

@Data
public class ViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    private File templateRootDir;

    public ViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }


    public View resolveViewName(String viewName, Object o) {
        if (null == viewName || viewName.trim().isEmpty()) {
            System.out.println("error ....");
        }

        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new View(templateFile);

    }

}
