package my.edu.tarc.mycontact.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Context
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import my.edu.tarc.mycontact.R
import my.edu.tarc.mycontact.dao.ContactDao
import my.edu.tarc.mycontact.database.ContactDatabase
import my.edu.tarc.mycontact.model.Contact
import my.edu.tarc.mycontact.model.Profile
import my.edu.tarc.mycontact.repository.ContactRepository

class ContactViewModel(application: Application) :
    AndroidViewModel(application) {
    //Create a UI dataset
    val contactList: LiveData<List<Contact>>
    private val contactRepository: ContactRepository
    var profile: Profile = Profile()
    var selectedContact: Contact = Contact("","")
    //if false no show the delete button
    var editmode: Boolean = false

    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        contactRepository = ContactRepository(contactDao)
        contactList = contactRepository.allContact

        val preferences = application.getSharedPreferences(
            application.applicationContext.packageName.toString(), MODE_PRIVATE
        )

        if (preferences.contains(application.resources.getString(R.string.name))) {
            profile.name = preferences.getString("name", "").toString()
        }
        if (preferences.contains(application.resources.getString(R.string.phone))) {
            profile.phone = preferences.getString("phone", "").toString()
        }
        if (preferences.contains(application.resources.getString(R.string.profile))) {
            profile.pic = preferences.getString("pic", "").toString()
        }
    }

    //Launching a coroutine
    fun insert(contact: Contact) = viewModelScope.launch {
        contactRepository.insert(contact)
    }

    fun delete(contact: Contact) = viewModelScope.launch {
        contactRepository.delete(contact)
    }

    fun update(contact: Contact) = viewModelScope.launch {
        contactRepository.update(contact)
    }

    fun syncContact(id: String){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("contact")

        if(!contactList.value.isNullOrEmpty()){
            for(contact in contactList.value!!.iterator()){
                myRef.child(id).child(contact.phone)
                    .child("phone")
                    .setValue(contact.phone)

                myRef.child(id).child(contact.phone)
                    .child("name")
                    .setValue(contact.name)
            }
        }else{
            Toast.makeText(getApplication(), "Contact is empty", Toast.LENGTH_SHORT).show()
            Log.d("SyncContact", "allContact is null or empty")
        }
    }
}