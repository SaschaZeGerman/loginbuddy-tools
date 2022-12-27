package net.loginbuddy.tools.common.connection;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;

public class SidecarHttpRequest extends BasicHttpResponse {

    public SidecarHttpRequest(StatusLine statusline) {
        super(statusline);
    }

    @Override
    public Header[] getHeaders(String name) {
        return new Header[] {
                new BasicHeader("Location", "http://localhost"),
                new BasicHeader("X-State", "teststate")
        };
    }
}
