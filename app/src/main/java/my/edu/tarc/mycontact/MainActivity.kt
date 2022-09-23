package my.edu.tarc.mycontact

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import my.edu.tarc.mycontact.databinding.ActivityMainBinding
import my.edu.tarc.mycontact.viewmodel.ContactViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val contactViewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize ViewModel
        contactViewModel.contactList.observe(this,
            {
                Log.d("Main Activity", "Contact List Size:" + it.size)
            })

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_save).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_sync -> {
                if(!contactViewModel.profile.phone.isEmpty()){
                    contactViewModel.syncContact(contactViewModel.profile.phone)
                }else{
                    val alertDialog = AlertDialog.Builder(this)

                    alertDialog.apply {
                        setTitle(getString(R.string.profile))
                        setMessage(getString(R.string.profile_missing))
                        setPositiveButton(android.R.string.ok){ _, _ ->
                            findNavController(R.id.nav_host_fragment_content_main)
                                .navigate(R.id.action_ContactFragment_to_ProfileFragment)
                        }
                        setNegativeButton(android.R.string.cancel){ alertDialog, _ ->
                            alertDialog.dismiss()
                        }

                    }.show()
                }
                true
            }
            R.id.action_add -> {
                findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_ContractFragment_to_addContactFragment)
                true
            }
            R.id.action_profile -> {
                findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_ContactFragment_to_ProfileFragment)
                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy","Main")
    }
}