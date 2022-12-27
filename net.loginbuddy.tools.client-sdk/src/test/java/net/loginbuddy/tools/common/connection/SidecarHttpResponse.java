package net.loginbuddy.tools.common.connection;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SidecarHttpResponse extends BasicHttpResponse {

    public SidecarHttpResponse(StatusLine statusline) {
        super(statusline);
    }

    @Override
    public HttpEntity getEntity() {
        BasicHttpEntity bhe = new BasicHttpEntity();
        bhe.setContentType("application/json");
        bhe.setContentEncoding("UTF-8");
        try {
            bhe.setContent(new FileInputStream("src/test/resources/testResponse.json"));
            return bhe;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Header[] getHeaders(String name) {
        return new Header[] {
                new BasicHeader("Location", "http://localhost"),
                new BasicHeader("X-State", "teststate")
        };
    }

    @Override
    public Header getFirstHeader(String name) {
        return getHeaders(name)[0];
    }
}
