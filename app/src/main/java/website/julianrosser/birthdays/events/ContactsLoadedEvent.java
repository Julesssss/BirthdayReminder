package website.julianrosser.birthdays.events;

import java.util.ArrayList;

import website.julianrosser.birthdays.Contact;

public class ContactsLoadedEvent {

   private ArrayList<Contact> contacts;

    public ContactsLoadedEvent(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }
}
