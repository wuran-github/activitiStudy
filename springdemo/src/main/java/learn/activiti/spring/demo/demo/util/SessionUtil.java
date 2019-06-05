package learn.activiti.spring.demo.demo.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtil {
    public static void removeSession(String attrName, HttpServletRequest request){
        HttpSession session=request.getSession();
        session.removeAttribute(attrName);
    }
    public static Object getSessionAttr(String attrName, HttpServletRequest request){
        return request.getSession().getAttribute(attrName);
    }
    public static void setSessionAttr(String attrName,Object value, HttpServletRequest request){
        request.getSession().setAttribute(attrName,value);
    }
}
