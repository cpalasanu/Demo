package ro.smeq.demo.network

data class Post(val userId: Long, val id: Long, val title: String, val body: String)
data class User(
    val id: Long,
    val name: String,
    val username: String,
    val email: String,
    val address: Address,
    val phone: String,
    val website: String,
    val company: Company
)
data class Album(val userId: Long, val id: Long, val title: String)
data class Photo(val albumId: Long, val id: Long, val title: String, val url: String, val thumbnailUrl: String)

data class Company(val name: String, val catchPhrase: String, val bs: String)
data class Geolocation(val lat: String, val lng: String)
data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: Geolocation
)
