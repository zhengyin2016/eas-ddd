package com.eas.trainingcontext.northbound.remote;

import com.eas.dddcore.Resources;
import com.eas.trainingcontext.message.NominationRequest;
import com.eas.trainingcontext.message.NominationResponse;
import com.eas.trainingcontext.northbound.appservice.NominationAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 培训票REST资源（书中20.3.5节）
 *
 * 使用ddd-core中定义的Resources辅助类统一处理异常和响应。
 * 不同异常类别对应不同状态码。
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketResource {

    private final NominationAppService nominationAppService;

    public TicketResource(NominationAppService nominationAppService) {
        this.nominationAppService = nominationAppService;
    }

    @PostMapping("/nominate")
    public ResponseEntity nominate(@RequestBody NominationRequest request) {
        return Resources.execute(() -> {
            NominationResponse response = nominationAppService.nominate(request);
            return ResponseEntity.ok(response);
        }, "提名候选人");
    }
}
