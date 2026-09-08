package org.springframework.core;

import java.io.IOException;

/**
 * Compatibility stub. org.springframework.core.NestedIOException was removed
 * in Spring Framework 6.0. This stub satisfies runtime class-loading references
 * from libraries compiled against Spring 5 (mybatis-spring, docx4j, etc.) that
 * still declare it in method signatures or exception tables. The class is never
 * actually thrown by Spring 6 code.
 */
@Deprecated
public class NestedIOException extends IOException {
    public NestedIOException(String msg) {
        super(msg);
    }
    public NestedIOException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
