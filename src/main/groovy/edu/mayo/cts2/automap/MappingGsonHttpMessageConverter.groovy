package edu.mayo.cts2.automap

import edu.mayo.cts2.framework.core.json.JsonConverter
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException

import javax.annotation.Resource
import java.nio.charset.Charset

class MappingGsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String CTS2_MODEL_PACKAGE = "edu.mayo.cts2.framework.model"

    @Resource
    private JsonConverter jsonConverter;

    /**
     * Instantiates a new mapping gson http message converter.
     */
    public MappingGsonHttpMessageConverter() {
        super(new MediaType("application", "json", DEFAULT_CHARSET));
    }

    /* (non-Javadoc)
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#supports(java.lang.Class)
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getName().startsWith(CTS2_MODEL_PACKAGE);
    }

    /* (non-Javadoc)
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#readInternal(java.lang.Class, org.springframework.http.HttpInputMessage)
     */
    @Override
    protected Object readInternal(
            Class<? extends Object> clazz,
            HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {

        Object obj =
                this.jsonConverter.fromJson(IOUtils.toString(inputMessage.getBody()), clazz);

        return obj;
    }

    /* (non-Javadoc)
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#writeInternal(java.lang.Object, org.springframework.http.HttpOutputMessage)
     */
    @Override
    protected void writeInternal(
            Object t,
            HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        String json = this.jsonConverter.toJson(t);

        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(outputMessage.getBody());

            writer.write(json);

            writer.flush();
        } finally {
            if(writer != null){
                writer.close();
            }
        }
    }
}