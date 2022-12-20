package net.loginbuddy.tools.common.connection;

import org.apache.http.*;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

public class SidecarHttpResponse extends BasicHttpResponse {

    public SidecarHttpResponse(StatusLine statusline, ReasonPhraseCatalog catalog, Locale locale) {
        super(statusline, catalog, locale);
    }

    public SidecarHttpResponse(StatusLine statusline) {
        super(statusline);
    }

    public SidecarHttpResponse(ProtocolVersion ver, int code, String reason) {
        super(ver, code, reason);
    }

    @Override
    public StatusLine getStatusLine() {
        return super.getStatusLine();
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
