package ru.aston.recyclercontacts

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.aston.recyclercontacts.databinding.ActivityMainBinding
import java.util.Collections
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var isSelectionMode = false
    private val selectedContacts = mutableSetOf<Contact>()
    private lateinit var binding: ActivityMainBinding
    private val contactAdapter by lazy {
        ContactAdapter(
            onClick = { contact ->
                if (isSelectionMode) {
                    toggleContactSelection(contact)
                    Log.d("onClick", "isSelectionMode con: $contact")
                } else {
                    showEditContactScreen(contact)
                    Log.d("onClick", "showEditContactScreen()")
                }
            },
            onLongClick = { contact ->
                if (!isSelectionMode) {
                    toggleSelectionMode(true)
                    Log.d("onLongClick", "toggleSelectionMode true")
                }
                toggleContactSelection(contact)
                Log.d("onLongClick", "toggleContactSelection con: $contact")
            },
            onDragStart = { viewHolder: RecyclerView.ViewHolder ->
                itemTouchHelper.startDrag(viewHolder)
            }
        )
    }

    private val contactList = mutableListOf<Contact>().apply {
        repeat(100) {
            add(
                Contact(
                    it + 1,
                    "Имя$it",
                    "Фамилия$it",
                    "+7-${Random.nextInt(100, 999)}-${Random.nextInt(100, 999)}-${
                        Random.nextInt(10, 99)
                    }-${Random.nextInt(10, 99)}"
                )
            )
        }
    }

    private val itemTouchHelper: ItemTouchHelper by lazy {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition: Int = viewHolder.adapterPosition
                val toPosition: Int = target.adapterPosition
                if (fromPosition != toPosition) {
                    Collections.swap(contactList, fromPosition, toPosition)
                    contactAdapter.notifyItemMoved(fromPosition, toPosition)
                    return true
                }
                return false
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupToolBar()
        setupAddButton()
        setupDeleteControls()
    }

    private fun setupDeleteControls() {
        binding.btnCancel.setOnClickListener {
            toggleSelectionMode(false)
            clearSelection()
        }

        binding.btnDelete.setOnClickListener {
            deleteSelectedContacts()
        }
    }

    private fun setupToolBar() {
        binding.deleteButton.setOnClickListener { item ->
            when (item.id) {
                R.id.delete_button -> {
                    toggleSelectionMode(true)
                }
            }
            setSupportActionBar(binding.toolbar)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactAdapter
        }
        contactAdapter.submitList(contactList)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupAddButton() {
        binding.fabAdd.setOnClickListener {
            showAddContactScreen()
        }
    }

    private fun toggleSelectionMode(enable: Boolean) {
        isSelectionMode = enable
        selectedContacts.clear()
        binding.fabAdd.visibility = if (enable) View.GONE else View.VISIBLE
        binding.layoutDeleteControls.visibility = if (enable) View.VISIBLE else View.GONE
        contactAdapter.notifyDataSetChanged()
    }

    private fun clearSelection() {
        contactList.forEach { contact ->
            contact.isSelected = false
        }
        selectedContacts.clear()
        contactAdapter.notifyDataSetChanged()
    }

    private fun toggleContactSelection(contact: Contact) {
        if (contact.isSelected) {
            selectedContacts.remove(contact)
            contact.isSelected = false
        }
        else {
            selectedContacts.add(contact)
            contact.isSelected = true
        }
        contactAdapter.notifyDataSetChanged()
    }

    private fun deleteSelectedContacts() {
        val newList = contactList.filterNot{ it.isSelected }
        Log.d("deleteSelectedContacts", "selectedContacts: $selectedContacts")
        contactList.clear()
        contactList.addAll(newList)
        contactAdapter.submitList(contactList)
        toggleSelectionMode(false)
        clearSelection()
        contactAdapter.notifyDataSetChanged()
    }


    private fun showAddContactScreen() {
        val dialog = ContactDialogFragment(onSave = { newContact ->
            contactList.add(newContact)
            contactAdapter.submitList(contactList.toList())
        })
        dialog.show(supportFragmentManager, baseContext.getString(R.string.contacts_add))
    }

    private fun showEditContactScreen(contact: Contact) {
        val dialog = ContactDialogFragment(contact = contact, onSave = { updatedContact ->
            val index = contactList.indexOfFirst { it.id == updatedContact.id }
            if (index != -1) {
                contactList[index] = updatedContact
                contactAdapter.submitList(contactList.toList())
            }
        })
        dialog.show(supportFragmentManager, baseContext.getString(R.string.contacts_edit))
    }

}