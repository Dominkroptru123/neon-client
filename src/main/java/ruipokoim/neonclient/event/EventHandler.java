package ruipokoim.neonclient.event;

import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    Priority priority() default Priority.MEDIUM;
    enum Priority {LOWEST, LOW, MEDIUM, HIGH, HIGHEST}
}