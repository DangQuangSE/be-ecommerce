package com.sport_pro_be.modules.chat.dto;

import com.sport_pro_be.modules.chat.constant.ChatConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank(message = ChatConstant.CONTENT_REQUIRED)
    @Size(max = 4000, message = ChatConstant.CONTENT_TOO_LONG)
    private String content;

    /// Optional image attachment URL.
    private String imageUrl;
}
