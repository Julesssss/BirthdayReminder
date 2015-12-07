# Birthday Reminder 2.0
App for Android devices which notifies users of upcoming birthdays.

![](http://julianrosser.website/images/birthday_screen_1.png)

TODO
- Read JSON data from prev version
- Find and implement alternative to Date()
- 'Birthday is soon' bug - Don't use old code, complete re-write, but find prev error
- SQL database instead of JSON ??? necessary? Will take effort, perhaps as backup
- Deep linking & Analytics
- Settings back-compatibility

Formatting & Documentation TODO
- MainActivity
- RecyclerListFragment
- RecyclerViewAdapter

Log
- 07/12 - Orientation change functionality. Adapter updates list correctly using UI Thread. Context menu (del, edit). Formatting & Documentation
- 06/12 - Add birthday (or edit) DialogFragment, layout & funtionality. Fragment callback listener. Icon for menu_add.
- 04/12 - First real progress to redesign. Re-wrote Birthdays class, RecyclerView & Adapter now working correctly.
- 03/12 - Birthday list (RecyclerViewFragment) displays correctly. Rewrote Birthdays.java.
- 20/11 - Created RecyclerViewAdapter, converted to use Array of Birthdays. P
- 18/10 - Designed new UX on paper.
- 03/09 - Redesigned, formatted and documented Birthdays.java.
- 02/09/2015 - Built new AS project. Pushed to new GitHub repo.

