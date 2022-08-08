package io.github.rocketk.jorm.listener.event;

import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Optional;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public abstract class WithContextEvent implements Event {
    private Map<String, Object> context;

    public Optional<Object> getContext(String key) {
        Validate.notBlank(key, "key is blank");
        return Optional.ofNullable(context).map(c -> c.get(key));
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
