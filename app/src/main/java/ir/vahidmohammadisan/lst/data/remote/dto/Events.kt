package ir.vahidmohammadisan.lst.data.remote.dto

data class Event(
    val id: Int,
    val category: String,
    val country: String,
    val description: String,
    val duration: Int,
    val end: String,
    val image: String,
    val isPrivate: Boolean,
    val location: List<Double>,
    val start: String,
    val state: String,
    val title: String
)
