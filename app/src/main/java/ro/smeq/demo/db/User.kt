package ro.smeq.demo.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Long,
    val name: String,
    val username: String,
    val email: String,
    @Embedded val address: Address,
    val phone: String,
    val website: String,
    @Embedded val company: Company
)

data class Company(@ColumnInfo(name ="company_name") val name: String, val catchPhrase: String, val bs: String)
data class Geolocation(val lat: String, val lng: String)
data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    @Embedded val geo: Geolocation
)
