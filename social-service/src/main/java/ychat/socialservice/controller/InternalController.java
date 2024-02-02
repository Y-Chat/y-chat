package ychat.socialservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.service.InternalService;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
@ResponseStatus(HttpStatus.OK)
@Validated
@Tag(
    name = "Internal Endpoint",
    description = "Specialised endpoints for the use of internal services. Cannot be requested " +
                  "from outside the API gateway and does not require authentication."
)
public class InternalController {
    private final InternalService internalService;

    public InternalController(@NonNull InternalService internalService) {
        this.internalService = internalService;
    }

    @GetMapping("/{userId}/shouldReceive/{chatId}")
    @Operation(
        summary = "Check for a given user if they should receive a message sent to the given chat.",
        description = "Returns a boolean value which responds to the action."
    )
    public boolean shouldReceive(@PathVariable UUID userId, @PathVariable UUID chatId) {
        return internalService.shouldReceive(userId, chatId);
    }
}
