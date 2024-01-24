package ychat.socialservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.service.ChatService;

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
    private final ChatService chatService;

    public InternalController(@NonNull ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/canReceive")
    @Operation(
        summary = "TODO",
        description = "TODO define what it returns exactly"
    )
    public void canReceive(@PathVariable @NotNull UUID groupId) {
        return; // TODO define
    }
}
