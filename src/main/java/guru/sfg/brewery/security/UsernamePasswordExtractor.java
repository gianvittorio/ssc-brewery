package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

public interface UsernamePasswordExtractor {
    String[] extract(HttpServletRequest request);
}
