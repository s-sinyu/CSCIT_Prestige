package com.prestige.renovation.common.storage;

import static com.prestige.renovation.support.ApiAssertions.assertApiError;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.prestige.renovation.common.config.AppProperties;

/** Upload validation: type, extension, magic bytes and size (FR-IDP-5). */
class ImageValidatorTest {

    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0x0D, 'I', 'H'};
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 'J', 'F', 'I', 'F', 0, 1};

    private final ImageValidator validator = new ImageValidator(new AppProperties("Asia/Singapore", null, null, null,
            new AppProperties.Images(10, 5 * 1024 * 1024), null));

    @Test
    void acceptsMatchingPng() {
        var result = validator.validate(new MockMultipartFile("file", "photo.PNG", "image/png", PNG));
        assertThat(result.contentType()).isEqualTo("image/png");
        assertThat(result.extension()).isEqualTo("png");
    }

    @Test
    void acceptsJpegWithJpegExtension() {
        var result = validator.validate(new MockMultipartFile("file", "photo.jpeg", "image/jpeg", JPEG));
        assertThat(result.extension()).isEqualTo("jpg");
    }

    @Test
    void rejectsDisallowedExtension() {
        assertApiError(() -> validator.validate(new MockMultipartFile("file", "x.gif", "image/gif", PNG)),
                HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejectsContentThatDoesNotMatchDeclaredType() {
        // Declared PNG, but the bytes are a script
        byte[] script = "<script>alert(1)</script>".getBytes();
        assertApiError(() -> validator.validate(new MockMultipartFile("file", "x.png", "image/png", script)),
                HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejectsJpegBytesDeclaredAsPng() {
        assertApiError(() -> validator.validate(new MockMultipartFile("file", "x.png", "image/png", JPEG)),
                HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejectsTooLargeFile() {
        byte[] big = new byte[5 * 1024 * 1024 + 1];
        System.arraycopy(PNG, 0, big, 0, PNG.length);
        assertApiError(() -> validator.validate(new MockMultipartFile("file", "x.png", "image/png", big)),
                HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejectsEmptyFile() {
        assertApiError(() -> validator.validate(new MockMultipartFile("file", "x.png", "image/png", new byte[0])),
                HttpStatus.BAD_REQUEST);
    }

    @Test
    void storageKeysAreStrictlyServerGenerated() {
        assertThat(DatabaseStorageService.isValidKey("0b0e2c0a-3c3f-4a8e-9f55-1c2d3e4f5a6b.jpg")).isTrue();
        assertThat(DatabaseStorageService.isValidKey("../../etc/passwd")).isFalse();
        assertThat(DatabaseStorageService.isValidKey("0b0e2c0a-3c3f-4a8e-9f55-1c2d3e4f5a6b.jpg/../x")).isFalse();
        assertThat(DatabaseStorageService.isValidKey(null)).isFalse();
    }
}
