package com.dml.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View {


    private File viewFile;


    public View(File templateFile) {
        this.viewFile = templateFile;
    }

    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        URL url = this.getClass().getResource("/template");

        String protocol = url.getProtocol();
        RandomAccessFile ra = null;

        StringBuffer sb = new StringBuffer();
        String line = null;

        if ("file".equals(protocol)) {
            ra = new RandomAccessFile(this.viewFile,"r");

            while (null != (line = ra.readLine())) {
                handle(sb, line, model);
            }

        } else if ("jar".equals(protocol)) {

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/template/index.html")));

            while (null != (line = br.readLine())) {
                handle(sb, line, model);
            }
        }

        response.setCharacterEncoding("utf-8");
//        response.setContentType(DEFULAT_CONTENT_TYPE);
        response.getWriter().write(sb.toString());
    }


    private void handle(StringBuffer sb, String line, Map<String, ?> model) throws UnsupportedEncodingException {
        line = new String(line.getBytes("utf-8"), "utf-8");
        Pattern pattern = Pattern.compile("#\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String paramName = matcher.group();
            paramName = paramName.replaceAll("#\\{|\\}", "");
            Object paramValue = model.get(paramName);
            if (null == paramValue) {
                continue;
            }
            line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
            matcher = pattern.matcher(line);
        }
        sb.append(line);
    }


    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
