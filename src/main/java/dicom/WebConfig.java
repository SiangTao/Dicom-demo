package dicom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//配置映射文件，即从本地找到图片   **重要
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 图片保存路径，自动从yml文件中获取数据
     * 示例： /Users/taosiang/dicom/images/
     */
    @Value("${file-save-path}")
    private String fileSavePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 配置资源映射
         * 意思是：如果访问的资源路径是以“/images/”开头的，
         * 就给我映射到本机的图片目录去找资源
         * 注意：目录路径后面的 “/”一定要带上
         */
        String normalizedPath = fileSavePath.endsWith("/") ? fileSavePath : fileSavePath + "/";
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + normalizedPath);
    }
}
