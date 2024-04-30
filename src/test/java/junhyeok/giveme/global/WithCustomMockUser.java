package junhyeok.giveme.global;

import junhyeok.giveme.user.enums.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface WithCustomMockUser {
    long id() default 1L;
    Role role() default Role.USER;
}
