package my.edu.tarc.mycontact

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import my.edu.tarc.mycontact.databinding.FragmentAddContactBinding
import my.edu.tarc.mycontact.model.Contact
import my.edu.tarc.mycontact.viewmodel.ContactViewModel

class AddContactFragment : Fragment() {

    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!
    private val contactViewModel: ContactViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentAddContactBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(contactViewModel.editmode){
            binding.editTextPhone2.isEnabled = false
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.edit)
            //Transfer data from selected contact to UI
            binding.editTextPhone2.setText(contactViewModel.selectedContact.phone)
            binding.editTextTextPersonName.setText(contactViewModel.selectedContact.name)
            binding.editTextTextPersonName.requestFocus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_save).isVisible = true
        menu.findItem(R.id.action_add).isVisible = false
        menu.findItem(R.id.action_profile).isVisible = false
        menu.findItem(R.id.action_settings).isVisible = false
        menu.findItem(R.id.action_sync).isVisible = false
//        if(contactViewModel.editmode){
//            menu.findItem(R.id.action_delete).isVisible = true
//        }
//        else{
//            menu.findItem(R.id.action_delete).isVisible = contactViewModel.editmode
//        }
        menu.findItem(R.id.action_delete).isVisible = contactViewModel.editmode
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.action_save ->{
                val name = binding.editTextTextPersonName.text.toString()
                val phone = binding.editTextPhone2.text.toString()
                val newContact = Contact(name, phone)

                //MainActivity.contactList.add(newContact)
                if(contactViewModel.editmode)
                {
                    contactViewModel.update(newContact)
                }
                else {
                    contactViewModel.insert(newContact)
                }
                Toast.makeText(context, getString(R.string.record_saved), Toast.LENGTH_SHORT).show()

//                val navController = activity?.findNavController(R.id.nav_host_fragment_content_main)
//                navController?.navigateUp()
                findNavController().navigateUp()
                true
            }

            R.id.action_delete->{
                val alertDialog = AlertDialog.Builder(requireActivity())

                alertDialog.apply {
                    setTitle(R.string.delete)
                    setMessage(R.string.delete_message)
                    setPositiveButton(R.string.delete){_,_->
                        val newContact = Contact(
                            binding.editTextTextPersonName.text.toString(),
                            binding.editTextPhone2.text.toString()
                        )
                        contactViewModel.delete(newContact)
                        contactViewModel.editmode = false
                        findNavController().navigateUp()
                    }

                    setNegativeButton(android.R.string.cancel){
                        alertDialog,_->alertDialog.dismiss()
                    }
                }.show()
            }
            //click up and set the editmode to false
            android.R.id.home->{
                contactViewModel.editmode = false
                findNavController().navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
