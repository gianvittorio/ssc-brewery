package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

public class QueryParamsUsernamePasswordExtractor implements UsernamePasswordExtractor {
    @Override
    public String[] extract(HttpServletRequest request) {
        String[] ans = new String[2];

        String[] queryParams = request.getQueryString().split("&");
        if (queryParams == null || queryParams.length == 0) {
            return ans;
        }

        String username = "", password = "";
        for (String keyValue : queryParams) {
            String[] keyValuePair = keyValue.split("=");
            String key = keyValuePair[0], value = keyValuePair[1];

            if (key.equals("Api-Key")) {
                username = value;
            }

            if (key.equals("Api-Secret")) {
                password = value;
            }
        }

        ans[0] = username;
        ans[1] = password;

        return ans;
    }
}
