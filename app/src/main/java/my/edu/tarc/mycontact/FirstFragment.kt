package my.edu.tarc.mycontact

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import my.edu.tarc.mycontact.databinding.FragmentFirstBinding
import my.edu.tarc.mycontact.viewmodel.ContactViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val contactViewModel: ContactViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Enable menu item
        setHasOptionsMenu(true)
        //val menuHost: MenuHost = requireActivity()
        //menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onStart() {
        Log.d("onStart", "First Fragment")
        super.onStart()
    }

    override fun onResume() {
        Log.d("onResume", "First Fragment")
        super.onResume()

        val contactAdapter = ContactAdapter(ContactAdapter.contactOnClickListener{
            //TODO: Event handler for contact click
            contactViewModel.selectedContact = it
            contactViewModel.editmode = true
            findNavController().navigate(R.id.action_ContractFragment_to_addContactFragment)
        })

        contactViewModel.contactList.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                Toast.makeText(context, getString(R.string.no_record),
                    Toast.LENGTH_SHORT).show()
                binding.textViewWelcome.visibility = View.VISIBLE
            }
            else
            {
                binding.textViewWelcome.visibility = View.GONE
            }
                contactAdapter.setContact(it)

        }

        binding.recyclerViewContact.layoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.recyclerViewContact.adapter = contactAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_save).isVisible = false
        menu.findItem(R.id.action_delete).isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}