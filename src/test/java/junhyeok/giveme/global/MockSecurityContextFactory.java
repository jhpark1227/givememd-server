package junhyeok.giveme.global;

import junhyeok.giveme.global.security.UserDetailsImpl;
import junhyeok.giveme.user.enums.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, Role.USER);
        UsernamePasswordAuthenticationToken auth = new	UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
