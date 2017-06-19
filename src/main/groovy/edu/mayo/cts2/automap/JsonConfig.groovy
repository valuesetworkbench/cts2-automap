package edu.mayo.cts2.automap

import edu.mayo.cts2.framework.core.json.JsonConverter
import org.springframework.boot.autoconfigure.web.HttpMessageConverters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
public class JsonConfig {

    @Bean
    public HttpMessageConverters customConverters() {
        new HttpMessageConverters(true, [new MappingGsonHttpMessageConverter(jsonConverter: new JsonConverter())]);
    }

}