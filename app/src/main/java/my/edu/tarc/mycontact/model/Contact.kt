package my.edu.tarc.mycontact.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact (val name: String,
                    @PrimaryKey val phone: String)


