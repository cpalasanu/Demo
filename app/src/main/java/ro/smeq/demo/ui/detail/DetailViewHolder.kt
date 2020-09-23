package ro.smeq.demo.ui.detail

sealed class ListItem(val id: Long)
class HeaderListItem(id: Long, val title: String, val body: String) : ListItem(id)
class AlbumListItem(id: Long, val title: String): ListItem(id)
class PhotoListItem(id: Long, val title: String, val imgUrl: String): ListItem(id)
