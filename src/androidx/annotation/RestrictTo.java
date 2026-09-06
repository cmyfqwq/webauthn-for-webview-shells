package androidx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 编译期桩 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.PACKAGE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
public @interface RestrictTo {
    Scope[] value();
    enum Scope { LIBRARY_GROUP, LIBRARY, TESTS, GROUP_ID }
}
