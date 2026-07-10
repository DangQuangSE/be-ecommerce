package com.sport_pro_be.modules.shop.service;

import com.sport_pro_be.exception.AppException;
import com.sport_pro_be.exception.BadRequestException;
import com.sport_pro_be.exception.ResourceNotFoundException;
import com.sport_pro_be.modules.shop.dto.GeocodeResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/// Unit tests for {@link MapboxGeocodingService}.
///
/// Input-validation branches (blank address, null/out-of-range lat or lng) are
/// asserted to short-circuit before ever touching {@link RestClient} -- these
/// need no HTTP mocking at all.
///
/// For the branches that do reach {@code RestClient}, the fluent chain
/// (`get().uri(...).retrieve().body(...)`) is exercised with a plain mock
/// whose `get()` is stubbed to throw (for the RestClientException -> AppException
/// mapping, which never needs to reach `.uri()`/`.body()`), and with a
/// {@link Answers#RETURNS_DEEP_STUBS} mock for the two branches that need a
/// resolved response (empty/null features -> ResourceNotFoundException, and the
/// happy path). RETURNS_DEEP_STUBS is the least-brittle option available here:
/// the repo has no HTTP-stubbing library (WireMock / MockWebServer) on the
/// classpath, and RestClient's fluent builder has no simpler test seam.
///
/// {@code MapboxGeocodingResponse} / {@code MapboxFeature} are private nested
/// records inside {@link MapboxGeocodingService}, so they cannot be referenced
/// by name from this test class; the happy-path and empty-features tests build
/// instances via reflection (constructor.setAccessible(true)) instead.
@ExtendWith(MockitoExtension.class)
class MapboxGeocodingServiceTest {

    private static final String ACCESS_TOKEN = "test-token";

    // ---- geocode(address): input validation ----

    @Test
    void geocode_withNullAddress_throwsBadRequestWithoutCallingRestClient() {
        RestClient restClient = mock(RestClient.class);
        MapboxGeocodingService service = newService(restClient);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.geocode(null));

        assertEquals("Địa chỉ không được để trống", ex.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void geocode_withBlankAddress_throwsBadRequestWithoutCallingRestClient() {
        RestClient restClient = mock(RestClient.class);
        MapboxGeocodingService service = newService(restClient);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.geocode("   "));

        assertEquals("Địa chỉ không được để trống", ex.getMessage());
        verifyNoInteractions(restClient);
    }

    // ---- reverseGeocode(lat, lng): input validation ----

    @Test
    void reverseGeocode_withNullLatitude_throwsBadRequestWithoutCallingRestClient() {
        RestClient restClient = mock(RestClient.class);
        MapboxGeocodingService service = newService(restClient);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.reverseGeocode(null, new BigDecimal("106.7215")));

        assertEquals("Vĩ độ và kinh độ không được để trống", ex.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void reverseGeocode_withNullLongitude_throwsBadRequestWithoutCallingRestClient() {
        RestClient restClient = mock(RestClient.class);
        MapboxGeocodingService service = newService(restClient);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.reverseGeocode(new BigDecimal("10.7295"), null));

        assertEquals("Vĩ độ và kinh độ không được để trống", ex.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void reverseGeocode_withLatitudeAboveNinety_throwsBadRequestWithoutCallingRestClient() {
        RestClient restClient = mock(RestClient.class);
        MapboxGeocodingService service = newService(restClient);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.reverseGeocode(new BigDecimal("91"), new BigDecimal("106.7215")));

        assertEquals("Vĩ độ không hợp lệ", ex.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void reverseGeocode_withLatitudeBelowNegativeNinety_throwsBadRequestWithoutCallingRestClient() {
        RestClient restClient = mock(RestClient.class);
        MapboxGeocodingService service = newService(restClient);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.reverseGeocode(new BigDecimal("-91"), new BigDecimal("106.7215")));

        assertEquals("Vĩ độ không hợp lệ", ex.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void reverseGeocode_withLongitudeAboveOneEighty_throwsBadRequestWithoutCallingRestClient() {
        RestClient restClient = mock(RestClient.class);
        MapboxGeocodingService service = newService(restClient);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.reverseGeocode(new BigDecimal("10.7295"), new BigDecimal("181")));

        assertEquals("Kinh độ không hợp lệ", ex.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void reverseGeocode_withLongitudeBelowNegativeOneEighty_throwsBadRequestWithoutCallingRestClient() {
        RestClient restClient = mock(RestClient.class);
        MapboxGeocodingService service = newService(restClient);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.reverseGeocode(new BigDecimal("10.7295"), new BigDecimal("-181")));

        assertEquals("Kinh độ không hợp lệ", ex.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void reverseGeocode_withInclusiveBoundaryValues_passesValidationAndReachesRestClient() {
        // 90/-90 and 180/-180 sit exactly on the guard clauses' compareTo(...)
        // < 0 / > 0 checks, so they must be treated as valid and reach
        // RestClient rather than being rejected as out-of-range. Confirmed here
        // via the RestClientException -> AppException mapping (rather than a
        // BadRequestException) once the call is attempted.
        RestClient restClient = mock(RestClient.class);
        when(restClient.get()).thenThrow(new RestClientException("boom"));
        MapboxGeocodingService service = newService(restClient);

        assertThrows(AppException.class, () -> service.reverseGeocode(new BigDecimal("90"), new BigDecimal("180")));
        assertThrows(AppException.class, () -> service.reverseGeocode(new BigDecimal("-90"), new BigDecimal("-180")));
    }

    // ---- RestClientException -> AppException(BAD_GATEWAY) mapping ----

    @Test
    void geocode_whenRestClientThrows_wrapsAsAppExceptionWithBadGateway() {
        RestClient restClient = mock(RestClient.class);
        when(restClient.get()).thenThrow(new RestClientException("connection refused"));
        MapboxGeocodingService service = newService(restClient);

        AppException ex = assertThrows(AppException.class, () -> service.geocode("123 Main St"));

        assertEquals("Không thể kết nối tới dịch vụ bản đồ", ex.getMessage());
        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatus());
    }

    @Test
    void reverseGeocode_whenRestClientThrows_wrapsAsAppExceptionWithBadGateway() {
        RestClient restClient = mock(RestClient.class);
        when(restClient.get()).thenThrow(new RestClientException("connection refused"));
        MapboxGeocodingService service = newService(restClient);

        AppException ex = assertThrows(AppException.class,
                () -> service.reverseGeocode(new BigDecimal("10.7295"), new BigDecimal("106.7215")));

        assertEquals("Không thể kết nối tới dịch vụ bản đồ", ex.getMessage());
        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatus());
    }

    // ---- empty/null response -> ResourceNotFoundException ----

    @Test
    void geocode_whenResponseHasNoFeatures_throwsResourceNotFound() throws Exception {
        RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        stubMapboxResponse(restClient, mapboxGeocodingResponse(List.of()));
        MapboxGeocodingService service = newService(restClient);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.geocode("Nowhere"));

        assertEquals("Không tìm thấy vị trí phù hợp", ex.getMessage());
    }

    @Test
    void geocode_whenResponseBodyIsNull_throwsResourceNotFound() {
        RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        stubMapboxResponse(restClient, null);
        MapboxGeocodingService service = newService(restClient);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.geocode("Nowhere"));

        assertEquals("Không tìm thấy vị trí phù hợp", ex.getMessage());
    }

    // ---- happy path: center [lng, lat] correctly swapped into GeocodeResult ----

    @Test
    void geocode_withValidResponse_mapsCenterAndPlaceNameSwappingLngLatToLatLng() throws Exception {
        RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        Object feature = mapboxFeature(
                "Ho Chi Minh City, Vietnam",
                new double[] {106.7215, 10.7295}); // Mapbox order: [lng, lat]
        stubMapboxResponse(restClient, mapboxGeocodingResponse(List.of(feature)));
        MapboxGeocodingService service = newService(restClient);

        GeocodeResult result = service.geocode("Ho Chi Minh City");

        assertEquals("Ho Chi Minh City, Vietnam", result.address());
        assertEquals(BigDecimal.valueOf(10.7295), result.latitude());
        assertEquals(BigDecimal.valueOf(106.7215), result.longitude());
    }

    @Test
    void reverseGeocode_withValidResponse_mapsCenterAndPlaceNameSwappingLngLatToLatLng() throws Exception {
        RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        Object feature = mapboxFeature(
                "106 Nguyen Van Linh, District 7, HCMC",
                new double[] {106.7215, 10.7295});
        stubMapboxResponse(restClient, mapboxGeocodingResponse(List.of(feature)));
        MapboxGeocodingService service = newService(restClient);

        GeocodeResult result = service.reverseGeocode(new BigDecimal("10.7295"), new BigDecimal("106.7215"));

        assertEquals("106 Nguyen Van Linh, District 7, HCMC", result.address());
        assertEquals(BigDecimal.valueOf(10.7295), result.latitude());
        assertEquals(BigDecimal.valueOf(106.7215), result.longitude());
    }

    // ---- helpers ----

    private static MapboxGeocodingService newService(RestClient restClient) {
        MapboxGeocodingService service = new MapboxGeocodingService(restClient);
        ReflectionTestUtils.setField(service, "accessToken", ACCESS_TOKEN);
        return service;
    }

    @SuppressWarnings("unchecked")
    private static void stubMapboxResponse(RestClient restClient, Object response) {
        when(restClient.get()
                .uri(any(Function.class))
                .retrieve()
                .body(any(Class.class)))
                .thenReturn(response);
    }

    /// Builds a MapboxGeocodingService.MapboxGeocodingResponse via reflection:
    /// it's a private nested record, inaccessible by name from this test class.
    private static Object mapboxGeocodingResponse(List<Object> features) throws Exception {
        Class<?> responseClass = Class.forName(
                "com.sport_pro_be.modules.shop.service.MapboxGeocodingService$MapboxGeocodingResponse");
        Constructor<?> constructor = responseClass.getDeclaredConstructor(List.class);
        constructor.setAccessible(true);
        return constructor.newInstance(features);
    }

    /// Builds a MapboxGeocodingService.MapboxFeature via reflection (see above).
    private static Object mapboxFeature(String placeName, double[] center) throws Exception {
        Class<?> featureClass = Class.forName(
                "com.sport_pro_be.modules.shop.service.MapboxGeocodingService$MapboxFeature");
        Constructor<?> constructor = featureClass.getDeclaredConstructor(String.class, double[].class);
        constructor.setAccessible(true);
        return constructor.newInstance(placeName, center);
    }
}
