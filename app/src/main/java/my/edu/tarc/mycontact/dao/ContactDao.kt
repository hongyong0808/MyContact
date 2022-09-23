package my.edu.tarc.mycontact.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import my.edu.tarc.mycontact.model.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Query("SELECT * FROM contact")
    fun getAll(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact WHERE phone = :phone")
    fun findByPhone(phone: String) : Contact

    @Query("SELECT * FROM contact WHERE name LIKE :name")
    fun findByName(name: String): Contact
}