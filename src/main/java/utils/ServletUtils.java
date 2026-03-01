package utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 生成当前请求的url
 * @Author: tsa
 * @Date: 2023/8/30 9:27
 */
public class ServletUtils {
    public static String getImageUrl(String imageName){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        String servletPath = request.getServletPath();
        int index = requestURL.indexOf(servletPath);
        return requestURL.delete(index+1 , requestURL.length())+"images/"+imageName;
    }

}
