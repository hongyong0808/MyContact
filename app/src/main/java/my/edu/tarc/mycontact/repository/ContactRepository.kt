package my.edu.tarc.mycontact.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.mycontact.dao.ContactDao
import my.edu.tarc.mycontact.model.Contact


class ContactRepository (private val contactDao: ContactDao) {
    //Cache copy of dataset
    val allContact: LiveData<List<Contact>> = contactDao.getAll()

    suspend fun insert(contact: Contact){
        contactDao.insert(contact)
    }

    suspend fun delete(contact: Contact){
        contactDao.delete(contact)
    }

    suspend fun update(contact: Contact){
        contactDao.update(contact)
    }

    fun findByName(name: String): Contact{
        return contactDao.findByName(name)
    }

    fun syncContact(id: String){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("contact")

        val contactList = contactDao.getAll()
        if(!contactList.value.isNullOrEmpty()){
            for(contact in contactList.value!!.listIterator()){
                myRef.child(id).child(contact.phone)
                    .child("phone")
                    .setValue(contact.phone)

                myRef.child(id).child(contact.phone)
                    .child("name")
                    .setValue(contact.name)
            }
        }else{
            Log.d("SyncContact", "allContact is null or empty")
        }



    }
}