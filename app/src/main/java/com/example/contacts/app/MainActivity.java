package com.example.contacts.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.R;
import com.example.contacts.database.Contact;
import com.example.contacts.database.ContactViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactAdapter.ContactClickListener, Filterable {
    public static final String EXTRA_REQUEST = "com.example.contacts.app.EXTRA_REQUEST";
    public static final String EXTRA_CONTACT = "com.example.contacts.app.EXTRA_CONTACT";
    public static final int REQUEST_ADD_CONTACT = 1;
    public static final int REQUEST_EDIT_CONTACT = 2;
    private ContactAdapter adapter;
    private ContactViewModel contactViewModel;

    private Filter contactsFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Contact> filteredContacts = new ArrayList<>();
            List<Contact> allContacts = contactViewModel.getAllContacts().getValue();

            if (constraint == null || constraint.length() == 0) {
                filteredContacts.addAll(allContacts);
            } else {
                String filterPattern = constraint.toString().toLowerCase();

                for (Contact contact : allContacts) {
                    if (contact.getName().toLowerCase().contains(filterPattern) ||
                            contact.getEmail().toLowerCase().contains(filterPattern) ||
                            String.valueOf(contact.getTelephoneNumber()).toLowerCase().contains(filterPattern)) {

                        filteredContacts.add(contact);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredContacts;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Contact> filteredContacts = (List<Contact>) results.values;
            adapter.setAllContacts(filteredContacts);

            if (filteredContacts == null || filteredContacts.size() == 0) {
                Toast.makeText(MainActivity.this, "No search results", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(MainActivity.this, EditContactActivity.class);
                startIntent.putExtra(EXTRA_REQUEST, REQUEST_ADD_CONTACT);
                startActivityForResult(startIntent, REQUEST_ADD_CONTACT);
            }
        });

        buildRecyclerView();
        initViewModel();
    }

    private void buildRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        dividerItemDecoration.setDrawable(getDrawable(R.drawable.separator_shape));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ContactAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initViewModel() {
        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        contactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                adapter.setAllContacts(contacts);
            }
        });
    }

    @Override
    public void onContactClick(Contact contact) {
        Intent startIntent = new Intent(this, EditContactActivity.class);
        startIntent.putExtra(EXTRA_REQUEST, REQUEST_EDIT_CONTACT);
        startIntent.putExtra(EXTRA_CONTACT, contact);
        startActivityForResult(startIntent, REQUEST_EDIT_CONTACT);
    }

    @Override
    public void onContactsDelete(List<Contact> contacts) {
        contactViewModel.delete(contacts);
        Toast.makeText(this, "Contacts deleted", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Contact contact = (Contact) data.getSerializableExtra(EXTRA_CONTACT);

            if (requestCode == REQUEST_ADD_CONTACT) {
                contactViewModel.insert(contact);
                Toast.makeText(this, "Contact saved", Toast.LENGTH_LONG).show();
            }


            if (requestCode == REQUEST_EDIT_CONTACT) {
                contactViewModel.update(contact);
                Toast.makeText(this, "Contact updated", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public Filter getFilter() {
        return contactsFilter;
    }
}
