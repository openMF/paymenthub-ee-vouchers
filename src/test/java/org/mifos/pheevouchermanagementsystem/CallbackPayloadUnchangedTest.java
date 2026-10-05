package org.mifos.pheevouchermanagementsystem;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mifos.pheevouchermanagementsystem.data.CallbackRequestDTO;
import org.mifos.pheevouchermanagementsystem.data.RedeemVoucherResponseDTO;
import org.mifos.pheevouchermanagementsystem.data.SuccessfulVouchers;
import org.mifos.pheevouchermanagementsystem.data.VoucherLifecycleCallbackResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Three places used to serialise a callback body with a private {@code new ObjectMapper()} instead of the configured
 * bean, and all three build a {@code RedeemVoucherResponseDTO}. Swapping them for the bean is only safe if the bytes on
 * the wire do not move, because somebody else parses them - so this pins them. The other two payloads below were
 * already written with the configured bean; they are pinned too.
 *
 * <p>
 * The difference that could bite is date handling: Boot's mapper writes dates as ISO strings where a plain one writes
 * timestamps. None of these three payloads has a date field - the only timestamp in them is already a String by the
 * time it is set - which is why the assertions below pass, and the test is here so that stays true if a field is ever
 * added.
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest
class CallbackPayloadUnchangedTest {

    @Autowired
    private ObjectMapper configuredMapper;

    private final ObjectMapper oldPrivateMapper = new ObjectMapper();

    private void assertSame(Object payload) throws Exception {
        assertThat(configuredMapper.writeValueAsString(payload)).isEqualTo(oldPrivateMapper.writeValueAsString(payload));
    }

    @Test
    @DisplayName("the redemption callback body is unchanged")
    void redeemVoucherResponseIsUnchanged() throws Exception {
        assertSame(new RedeemVoucherResponseDTO("01", "Voucher redemption successful", "17898290249812", null, "2026-09-19T12:00:00.000",
                "1789829024"));
    }

    @Test
    @DisplayName("the voucher creation callback body is unchanged")
    void createVoucherCallbackIsUnchanged() throws Exception {
        assertSame(new CallbackRequestDTO("REQ-1", "BATCH-1",
                List.of(new SuccessfulVouchers("INS-1", "KES", BigDecimal.valueOf(100), "narration", "ENCRYPTED", "17898290249812"))));
    }

    @Test
    @DisplayName("the lifecycle callback body is unchanged")
    void lifecycleCallbackIsUnchanged() throws Exception {
        assertSame(new VoucherLifecycleCallbackResponseDTO("178982902498", "REQ-1", 0, List.of()));
    }
}
