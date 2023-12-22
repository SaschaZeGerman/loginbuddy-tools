package net.loginbuddy.tools.client.message;

import org.apache.http.NameValuePair;

import java.util.List;

public interface ParameterProvider {

    List<NameValuePair> getParameters();

}
