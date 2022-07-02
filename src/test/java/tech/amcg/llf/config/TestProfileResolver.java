package tech.amcg.llf.config;

import org.springframework.test.context.ActiveProfilesResolver;

public class TestProfileResolver implements ActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> testClass) {
        if("true".equals(System.getenv("CI"))) {
            return new String[] {"ci"};
        } else {
            return new String[] {"test"};
        }
    }
}
