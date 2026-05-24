package com.eas.dddcore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

/**
 * REST 辅助类
 * 统一封装 REST 接口的执行逻辑，处理异常并返回统一响应格式。
 * 参考书中 20.3.5 菱形对称架构的北向远程服务模式。
 */
public class Resources {

    private static final Logger logger = LoggerFactory.getLogger(Resources.class);

    /**
     * 执行操作并返回统一响应，携带操作描述
     */
    public static ResponseEntity<ResponseMessage> execute(Supplier<Object> supplier, String description) {
        try {
            Object result = supplier.get();
            if (result instanceof ResponseEntity) {
                return (ResponseEntity<ResponseMessage>) result;
            }
            return ResponseEntity.ok(ResponseMessage.success(result));
        } catch (DomainException e) {
            logger.warn("{} - 领域异常: {}", description, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseMessage.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (ApplicationException e) {
            logger.warn("{} - 应用异常: {}", description, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseMessage.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("{} - 系统异常", description, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    /**
     * 执行操作并返回统一响应（无操作描述）
     */
    public static ResponseEntity<ResponseMessage> execute(Supplier<Object> supplier) {
        return execute(supplier, "未命名操作");
    }

    /**
     * 执行无返回值操作
     */
    public static ResponseEntity<ResponseMessage> execute(Runnable runnable, String description) {
        return execute(() -> {
            runnable.run();
            return null;
        }, description);
    }

    /**
     * 执行无返回值操作（无操作描述）
     */
    public static ResponseEntity<ResponseMessage> execute(Runnable runnable) {
        return execute(runnable, "未命名操作");
    }

    /**
     * 执行操作并返回带状态的响应
     */
    public static ResponseEntity<ResponseMessage> executeWithStatus(Supplier<Object> supplier,
                                                                     HttpStatus successStatus,
                                                                     String description) {
        try {
            Object result = supplier.get();
            return ResponseEntity.status(successStatus)
                    .body(ResponseMessage.success(result));
        } catch (DomainException e) {
            logger.warn("{} - 领域异常: {}", description, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseMessage.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (ApplicationException e) {
            logger.warn("{} - 应用异常: {}", description, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseMessage.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("{} - 系统异常", description, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
