package com.sport_pro_be.modules.chat.constant;

public final class ChatConstant {

    private ChatConstant() {
    }

    // Success messages
    public static final String CONVERSATIONS_RETRIEVED = "Lấy danh sách hội thoại thành công";
    public static final String MESSAGES_RETRIEVED = "Lấy tin nhắn thành công";
    public static final String MESSAGE_SENT = "Đã gửi tin nhắn";

    // Errors
    public static final String CONVERSATION_NOT_FOUND = "Không tìm thấy hội thoại";
    public static final String CONTENT_REQUIRED = "Nội dung tin nhắn không được để trống";
    public static final String CONTENT_TOO_LONG = "Nội dung tin nhắn quá dài";

    // Default support conversation
    public static final String DEFAULT_SUPPORT_TITLE = "Hỗ trợ Sport Pro";
    public static final String DEFAULT_SUPPORT_TAG = "Hỗ trợ";
    public static final String WELCOME_MESSAGE =
            "Chào bạn! Sport Pro có thể giúp gì cho bạn hôm nay?";
}
