package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

public class HeaderUsernamePasswordExtractor implements UsernamePasswordExtractor {
    @Override
    public String[] extract(HttpServletRequest request) {
        String[] ans = new String[2];
        String username = request.getHeader("Api-Key");
        String password = request.getHeader("Api-Secret");

        ans[0] = (username == null) ? ("") : (username);
        ans[1] = (password == null) ? ("") : (password);

        return ans;
    }
}
