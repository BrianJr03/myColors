package jr.brian.myapplication.model.local

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
    )
)