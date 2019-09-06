package speaker

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import talk.Talk
import java.util.*

internal class SpeakerServiceTest {
  @Test
  internal fun `should keep only one speaker`() {
    // GIVEN
    val events = listOf(
      Talk("confId", Date(), "id", Date(), "title", listOf(SpeakerTalk("id", "name")), "summary"),
      Talk("confId", Date(), "id", Date(), "title", listOf(SpeakerTalk("id", "name")), "summary")
    )

    // WHEN
    val speakers = SpeakerService().convert(events)

    // THEN
    assertThat(speakers).hasSize(1)
    assertThat(speakers[0].talks).hasSize(2)
  }
}
