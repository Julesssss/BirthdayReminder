package website.julianrosser.birthdays.model

import java.util.ArrayList

class BirthdayItemClickEvent(val birthday: Birthday)
class BirthdaysLoadedEvent(val birthdays: ArrayList<Birthday>)
class ContactsLoadedEvent(val contacts: ArrayList<Contact>)