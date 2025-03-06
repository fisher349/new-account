package com.fisher.newaccountpoc.common.config;



import com.fisher.newaccountpoc.common.pojo.ErrorPageException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * error page配置
 */
@Controller
public class ErrorPageConfig extends BasicErrorController {
    public ErrorPageConfig(){
        super(new DefaultErrorAttributes(),new ErrorProperties());
    }

    @Override
    @RequestMapping(
            produces = {"text/html"}
    )
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        doError(request);
        return null;
    }

    @Override
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        doError(request);
        return null;
    }

    private void doError(HttpServletRequest request) {
        Map<String, Object> model = this.getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.ALL));

        //抛出ErrorPageException异常，方便被ExceptionHandlerConfig处理
        String path = model.get("path").toString();
        String status = model.get("status").toString();

        //静态资源文件发生404，无需抛出异常
        if(!path.contains("/common/") && !path.contains(".")){
            throw new ErrorPageException(Integer.valueOf(status), path);
        }
    }
}

