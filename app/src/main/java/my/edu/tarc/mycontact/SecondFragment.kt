package my.edu.tarc.mycontact

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.edu.tarc.mycontact.databinding.FragmentSecondBinding
import my.edu.tarc.mycontact.viewmodel.ContactViewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private val contactViewModel: ContactViewModel by viewModels()

    //Get content from another app component
    //implicit intent
    private val getProfilePic = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri ->
        if(uri!==null){
            binding.imageViewPicture.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        //Retrieve data from preferences
        val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
        if(preferences != null){
            if(preferences.contains(getString(R.string.name))){
                binding.editTextTextName.setText(preferences.getString(getString(R.string.name), ""))
            }
            if(preferences.contains(getString(R.string.phone))){
                binding.editTextPhone.setText(preferences.getString(getString(R.string.phone), ""))
            }
            readImage()
        }

        binding.imageViewPicture.setOnClickListener{
            getProfilePic.launch("image/*")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_save).isVisible = true
        menu.findItem(R.id.action_add).isVisible = false
        menu.findItem(R.id.action_profile).isVisible = false
        menu.findItem(R.id.action_settings).isVisible = false
        menu.findItem(R.id.action_sync).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.action_save ->{
                //create an instance of Preferences
                val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
                val name = binding.editTextTextName.text.toString()
                val phone = binding.editTextPhone.text.toString()
                if (preferences != null) {
                    with(preferences.edit()){
                        putString(getString(R.string.name), name)
                        putString(getString(R.string.phone), phone)
                        putString(getString(R.string.profile), "profile.png")
                        apply()
                    }
                }
                saveImage()
                uploadPicture()
                Toast.makeText(context, getString(R.string.record_saved), Toast.LENGTH_SHORT).show()
            }
            android.R.id.home ->{
                findNavController().navigateUp()
            }
        }
        return true
    }

    private fun saveImage() {
        val drawable = binding.imageViewPicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val file = File(context?.filesDir, "profile.png")
        val outputStream: OutputStream

        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun readImage(){
        val file = File(this.context?.filesDir, "profile.png")
        if(file.exists()){
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.imageViewPicture.setImageBitmap(bitmap)
        }else{
            binding.imageViewPicture.setImageResource(R.drawable.ic_baseline_account_box_24)
        }
    }

    fun uploadPicture(){
        val myStorage = Firebase.storage("gs://my-contact-list-3986f.appspot.com")
        val myProfilePic = myStorage.reference.child("images").child("012")

        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)

        if(file.exists()){
            try {
                binding.progressBar.visibility = View.VISIBLE

                myProfilePic.putFile(Uri.fromFile(file))

                    .addOnSuccessListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(context, "File uploaded", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}