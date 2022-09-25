package jr.brian.myapplication.data.model.local

data class StartUpViewPagerSlide(
    val title: String,
    val subtitle: String,
    val description: String
)

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
        subtitle = "to save your Favorites",
        description = "Access your account via web\n*myColorsWeb is coming soon"
    ),
    StartUpViewPagerSlide(
        title = "Sign In and sync",
        subtitle = "your Favorites",
        description = "Access your account via web\n*myColorsWeb is coming soon"
    ),
)