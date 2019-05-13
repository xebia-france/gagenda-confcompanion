import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class TalkTest {

    @ParameterizedTest
    @ValueSource(strings = arrayOf("hands on", "handson", "Handson", "HandsOn", "hands-on", "codelab", "hands'on"))
    fun `Should set type to Hands-on When initializing Talk Given title contains`(keyword: String) {
        // GIVEN
        val title = keyword + " My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("Hands-on")
    }

    @Test
    fun `Should set type to Workshop When initializing Talk Given "workshop" in title`() {
        // GIVEN
        val title = "workshop title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("Workshop")
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf(
            "cocktail",
            "fondations",
            "lunch",
            "opening",
            "pause",
            "welcoming",
            "pitch"
    ))
    fun `Should set type to keynote When initializing Talk Given title starts with`(keyword: String) {
        // GIVEN
        val title = keyword + "My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("keynote")
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf(
            "afternoon break",
            "annonce",
            "breakfast",
            "closing",
            "déjeuner",
            "evening party",
            "keynote",
            "lunch",
            "morning break",
            "new comers",
            "newcomers",
            "plénière",
            "tisanes"

    ))
    fun `Should set type to keynote When initializing Talk Given title contains`(keyword: String) {
        // GIVEN
        val title = "Good " + keyword + " - My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("keynote")
    }

    @Test
    fun `Should set type to keynote When initializing Talk Given "break" exact title`() {
        // GIVEN
        val title = "break"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("keynote")
    }

    @Test
    fun `Should set type to keynote When initializing Talk Given title ends with "after xke"`() {
        // GIVEN
        val title = "Some test + after xke"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("keynote")
    }

    @Test
    fun `Should set type to keynote When initializing Talk Given title ends with "pitch"`() {
        // GIVEN
        val title = "Some test + pitch"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("keynote")
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf(
            "My pitch is good",
            "After XKE et partage"
    ))
    fun `Should not set type to keynote When initializing Talk Given title is`(keyword: String) {
        // GIVEN
        val title = keyword

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isNotEqualTo("keynote")
    }


    @ParameterizedTest
    @ValueSource(strings = arrayOf(
            "cocktail",
            "fondations",
            "lunch",
            "opening",
            "pause",
            "welcoming"
    ))
    fun `Should set kind to keynote When initializing Talk Given title starts with`(keyword: String) {
        // GIVEN
        val title = keyword + "My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.kind).isEqualTo("keynote")
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf(
            "afternoon break",
            "annonce",
            "breakfast",
            "closing",
            "déjeuner",
            "evening party",
            "keynote",
            "lunch",
            "morning break",
            "new comers",
            "newcomers",
            "plénière",
            "tisanes"

    ))
    fun `Should set kind to keynote When initializing Talk Given title contains`(keyword: String) {
        // GIVEN
        val title = "Good " + keyword + " - My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.kind).isEqualTo("keynote")
    }

    @Test
    fun `Should set kind to keynote When initializing Talk Given "break" exact title`() {
        // GIVEN
        val title = "break"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.kind).isEqualTo("keynote")
    }

    @Test
    fun `Should set kind to keynote When initializing Talk Given title ends with "after xke"`() {
        // GIVEN
        val title = "Some test + after xke"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.kind).isEqualTo("keynote")
    }

    @Test
    fun `Should set kind to keynote When initializing Talk Given title ends with "pitch"`() {
        // GIVEN
        val title = "Some test + pitch"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.kind).isEqualTo("keynote")
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf(
            "My pitch is good",
            "After XKE et partage"
    ))
    fun `Should not set kind to keynote When initializing Talk Given title is`(keyword: String) {
        // GIVEN
        val title = keyword

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.kind).isNotEqualTo("keynote")
    }

}
