package com.sport_pro_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@PropertySources({
		@PropertySource("classpath:application.properties"),
		@PropertySource(value = "file:.env", ignoreResourceNotFound = true),
		@PropertySource(value = "file:be-ecommerce/.env", ignoreResourceNotFound = true)
})
public class SportProBeApplication {

	public static void main(String[] args) {
		String envDir = "./";
		if (!new java.io.File(".env").exists() && new java.io.File("be-ecommerce/.env").exists()) {
			envDir = "./be-ecommerce";
		}

		Dotenv dotenv = Dotenv.configure()
				.directory(envDir)
				.ignoreIfMissing()
				.load();
		if (dotenv != null) {
			setIfPresent("APP_JWT_SECRET", dotenv.get("APP_JWT_SECRET"), dotenv.get("JWT_SECRET_KEY"));
			setIfPresent("APP_JWT_EXP_MINUTES", dotenv.get("APP_JWT_EXP_MINUTES"));
			setIfPresent("APP_OTP_EXP_MINUTES", dotenv.get("APP_OTP_EXP_MINUTES"));
			setIfPresent("APP_OTP_RESEND_SECONDS", dotenv.get("APP_OTP_RESEND_SECONDS"));
			setIfPresent("APP_FORGOT_PASSWORD_TOKEN_EXP_MINUTES", dotenv.get("APP_FORGOT_PASSWORD_TOKEN_EXP_MINUTES"));
			setIfPresent("APP_FORGOT_PASSWORD_MAX_ATTEMPTS", dotenv.get("APP_FORGOT_PASSWORD_MAX_ATTEMPTS"));
			setIfPresent("APP_ADMIN_SEED_ENABLED", dotenv.get("APP_ADMIN_SEED_ENABLED"));
			setIfPresent("APP_ADMIN_EMAIL", dotenv.get("APP_ADMIN_EMAIL"));
			setIfPresent("APP_ADMIN_PASSWORD", dotenv.get("APP_ADMIN_PASSWORD"));
			setIfPresent("DB_HOST", dotenv.get("DB_HOST"));
			setIfPresent("DB_PORT", dotenv.get("DB_PORT"));
			setIfPresent("DB_NAME", dotenv.get("DB_NAME"));
			setIfPresent("DB_USERNAME", dotenv.get("DB_USERNAME"));
			setIfPresent("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
			setIfPresent("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
			setIfPresent("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
			setIfPresent("APP_MAIL_FROM", dotenv.get("APP_MAIL_FROM"), dotenv.get("MAIL_FROM"));
			setIfPresent("MAIL_HOST", dotenv.get("MAIL_HOST"));
			setIfPresent("MAIL_PORT", dotenv.get("MAIL_PORT"));
			setIfPresent("MAIL_SMTP_AUTH", dotenv.get("MAIL_SMTP_AUTH"));
			setIfPresent("MAIL_SMTP_STARTTLS", dotenv.get("MAIL_SMTP_STARTTLS"));
			setIfPresent("CLOUDINARY_CLOUD_NAME", dotenv.get("CLOUDINARY_CLOUD_NAME"));
			setIfPresent("CLOUDINARY_API_KEY", dotenv.get("CLOUDINARY_API_KEY"));
			setIfPresent("CLOUDINARY_API_SECRET", dotenv.get("CLOUDINARY_API_SECRET"));
			setIfPresent("VNPAY_TMN_CODE", dotenv.get("VNPAY_TMN_CODE"));
			setIfPresent("VNPAY_HASH_SECRET", dotenv.get("VNPAY_HASH_SECRET"));
			setIfPresent("VNPAY_PAY_URL", dotenv.get("VNPAY_PAY_URL"));
			setIfPresent("VNPAY_RETURN_URL", dotenv.get("VNPAY_RETURN_URL"));
			setIfPresent("VNPAY_IPN_URL", dotenv.get("VNPAY_IPN_URL"));
		}
		SpringApplication.run(SportProBeApplication.class, args);
	}

	private static void setIfPresent(String key, String... candidates) {
		if (candidates == null) {
			return;
		}
		for (String value : candidates) {
			if (value != null && !value.isBlank()) {
				System.setProperty(key, value);
				return;
			}
		}
	}

}
