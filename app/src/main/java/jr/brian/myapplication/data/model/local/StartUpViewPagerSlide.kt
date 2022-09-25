package jr.brian.myapplication.data.model.local

data class StartUpViewPagerSlide(
    val title: String,
    val subtitle: String,
    val description: String
)

const val MY_COLORS_WEB_DESCRIPTION =
    "Access your Favorites online via myColorsWeb\n\n*myColorsWeb is coming soon"

fun pagerData() = listOf(
    StartUpViewPagerSlide(
        title = "myColors",
        subtitle = "Color Generator",
        description = "Uses xColors API"
    ),
    StartUpViewPagerSlide(
        title = "Search Specific",
        subtitle = "or Generate Random",
        description = "Double-Click to Copy | Long-Press to Save"
    ),
    StartUpViewPagerSlide(
        title = "Create an Account",
        subtitle = "to sync your Favorites",
        description = MY_COLORS_WEB_DESCRIPTION
    ),
    StartUpViewPagerSlide(
        title = "Sign In and sync",
        subtitle = "your Favorites",
        description = MY_COLORS_WEB_DESCRIPTION
    ),
)