package com.prestige.renovation.common.storage;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.prestige.renovation.common.config.AppProperties;
import com.prestige.renovation.common.exception.ApiException;

/**
 * Validates image uploads (FR-IDP-5): JPEG, PNG or WebP only, at most 5 MB. The declared content type,
 * the file extension and the actual file signature ("magic bytes") must all agree. The client file name is
 * never used for storage.
 */
@Component
public class ImageValidator {

    private static final Map<String, String> EXTENSION_TO_TYPE = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp");

    private final long maxBytes;

    public ImageValidator(AppProperties properties) {
        this.maxBytes = properties.images() == null || properties.images().maxBytes() <= 0
                ? 5L * 1024 * 1024 : properties.images().maxBytes();
    }

    /** Validated upload: bytes, canonical content type and canonical extension (jpg, png, webp). */
    public record ValidImage(byte[] data, String contentType, String extension) {
    }

    public ValidImage validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("file", "Please choose an image file");
        }
        if (file.getSize() > maxBytes) {
            throw ApiException.badRequest("file", "File is too large (maximum 5 MB)");
        }
        String extension = extensionOf(file.getOriginalFilename());
        String expectedType = EXTENSION_TO_TYPE.get(extension);
        String declared = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (expectedType == null || !expectedType.equals(declared)) {
            throw ApiException.badRequest("file", "Only JPEG, PNG or WebP images are allowed");
        }
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw ApiException.badRequest("file", "The file could not be read");
        }
        return validate(data, declared, extension);
    }

    /** Checks the bytes match the declared type. Package-visible for unit tests. */
    ValidImage validate(byte[] data, String contentType, String extension) {
        String detected = detectType(data);
        if (detected == null || !detected.equals(contentType)) {
            throw ApiException.badRequest("file", "The file content is not a valid JPEG, PNG or WebP image");
        }
        String canonicalExt = switch (detected) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> "webp";
        };
        return new ValidImage(data, detected, canonicalExt);
    }

    static String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    static String detectType(byte[] d) {
        if (d == null || d.length < 12) {
            return null;
        }
        if ((d[0] & 0xFF) == 0xFF && (d[1] & 0xFF) == 0xD8 && (d[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if ((d[0] & 0xFF) == 0x89 && d[1] == 'P' && d[2] == 'N' && d[3] == 'G') {
            return "image/png";
        }
        if (d[0] == 'R' && d[1] == 'I' && d[2] == 'F' && d[3] == 'F'
                && d[8] == 'W' && d[9] == 'E' && d[10] == 'B' && d[11] == 'P') {
            return "image/webp";
        }
        return null;
    }
}
