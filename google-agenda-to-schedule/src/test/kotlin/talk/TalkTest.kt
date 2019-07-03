package talk

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import talk.Talk
import java.util.*

class TalkTest {

    @ParameterizedTest
    @ValueSource(strings = ["hands on", "handson", "Handson", "HandsOn", "hands-on", "codelab", "hands'on"])
    fun `should set type to hands-on When initializing Talk Given title contains`(keyword: String) {
        // GIVEN
        val title = "$keyword My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("handson")
    }

    @Test
    fun `should set type to workshop When initializing Talk Given "workshop" in title`() {
        // GIVEN
        val title = "workshop title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("workshop")
    }

    @Test
    fun `should set type to lunch When initializing Talk Given "breakfast" in title`() {
        // GIVEN
        val title = "Le déjeuner de Xebia"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("lunch")
    }

    @Test
    fun `Should set type to lunch When initializing Talk Given "lunch" in title`() {
        // GIVEN
        val title = "The Lunch at Xebia"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("lunch")
    }

    @Test
    fun `Should set type to party When initializing Talk Given "evening party" in title`() {
        // GIVEN
        val title = "The evening party at Xebia"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("party")
    }

    @Test
    fun `Should set type to party When initializing Talk Given "tisanes" in title`() {
        // GIVEN
        val title = "Les Tisanes Xebia"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("party")
    }

    @Test
    fun `Should set type to party When initializing Talk Given title starts with "cocktail"`() {
        // GIVEN
        val title = "Cocktail de fin"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("party")
    }

    @Test
    fun `Should set type to party When initializing Talk Given title ends with "after xke"`() {
        // GIVEN
        val title = "Some test + after xke"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("party")
    }

    @Test
    fun `Should set type to closing When initializing Talk Given title starts with closing`() {
        // GIVEN
        val title = "Closing event"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("closing")
    }

    @ParameterizedTest
    @ValueSource(strings = ["fondations"])
    fun `Should set type to keynote When initializing Talk Given title starts with`(keyword: String) {
        // GIVEN
        val title = keyword + "My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("keynote")
    }

    @ParameterizedTest
    @ValueSource(strings = ["opening", "welcoming"])
    fun `Should set type to opening When initializing Talk Given title starts with`(keyword: String) {
        // GIVEN
        val title = keyword + "My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("opening")
    }

    @ParameterizedTest
    @ValueSource(strings = ["annonce", "keynote", "new comers", "newcomers", "plénière"])
    fun `Should set type to keynote When initializing Talk Given title contains`(keyword: String) {
        // GIVEN
        val title = "Good $keyword - My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("keynote")
    }

    @ParameterizedTest
    @ValueSource(strings = ["morning break", "afternoon break", "meet and greet"])
    fun `Should set type to break When initializing Talk Given title contains`(keyword: String) {
        // GIVEN
        val title = "Good $keyword - My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("break")
    }

    @Test
    fun `Should set type to break When initializing Talk Given "break" exact title`() {
        // GIVEN
        val title = "break"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("break")
    }

    @Test
    fun `Should set type to break When initializing Talk Given title starts with "pause"`() {
        // GIVEN
        val title = "Pause bière"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("break")
    }

    @Test
    fun `Should set type to breakfast When initializing Talk Given title contains breakfast`() {
        // GIVEN
        val title = "Wonderful breakfast and magical beers"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.type).isEqualTo("breakfast")
    }

    @ParameterizedTest
    @ValueSource(strings = ["My pitch is good", "After XKE et partage"])
    fun `Should not set type to keynote When initializing Talk Given title is`(keyword: String) {
        // GIVEN

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), keyword, null, null, null)

        // THEN
        assertThat(talk.type).isNotEqualTo("keynote")
    }

    // TODO "After XKE et partage" should have "party" type

    @ParameterizedTest
    @ValueSource(strings = ["closing", "cocktail", "fondations", "lunch", "opening", "pause", "welcoming"])
    fun `Should set kind to keynote When initializing Talk Given title starts with`(keyword: String) {
        // GIVEN
        val title = keyword + "My slot title"

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), title, null, null, null)

        // THEN
        assertThat(talk.kind).isEqualTo("keynote")
    }

    @ParameterizedTest
    @ValueSource(strings = ["afternoon break", "annonce", "breakfast", "déjeuner", "evening party", "keynote", "lunch", "morning break", "new comers", "newcomers", "plénière", "tisanes"])
    fun `Should set kind to keynote When initializing Talk Given title contains`(keyword: String) {
        // GIVEN
        val title = "Good $keyword - My slot title"

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

    @ParameterizedTest
    @ValueSource(strings = ["My pitch is good", "After XKE et partage"])
    fun `Should not set kind to keynote When initializing Talk Given title is`(keyword: String) {
        // GIVEN

        // WHEN
        val talk = Talk("conferenceId", Date(), "id", Date(), keyword, null, null, null)

        // THEN
        assertThat(talk.kind).isNotEqualTo("keynote")
    }

}
