# Birthday Reminder 2.0
App for Android devices which notifies users of upcoming birthdays.

![](http://julianrosser.website/images/birthday_screen_1.png)

TODO
- Find and implement alternative to Date() What caused bug - Don't use old code, complete re-write, but find prev error
- Service
- Settings Fragment
- Notification Receiver

- Settings back-compatibility
- SQL database instead of JSON ??? necessary? Will take effort, perhaps as backup

- Deep linking & Analytics
- TypeFace-Light for api < 16

Log
- 10/12 - RobotoLight font. Custom date bg drawables with elevation.
- 09/12 - Saving/loading using lifecycle methods. New ItemMenuFragment to replace context hack. Rewrote BirthdayListView
          to Material-specs. Formatted and documented Adapter & Fragment.
- 08/12 - Added JSON save/load functions.
- 07/12 - Orientation change functionality. Adapter updates list correctly using UI Thread. Context menu (del, edit). Formatting & Documentation
- 06/12 - Add birthday (or edit) DialogFragment, layout & functionality. Fragment callback listener. Icon for menu_add.
- 04/12 - First real progress to redesign. Re-wrote Birthdays class, RecyclerView & Adapter now working correctly.
- 03/12 - Birthday list (RecyclerViewFragment) displays correctly. Rewrote Birthdays.java.
- 20/11 - Created RecyclerViewAdapter, converted to use Array of Birthdays. P
- 18/10 - Designed new UX on paper.
- 03/09 - Redesigned, formatted and documented Birthdays.java.
- 02/09/2015 - Built new AS project. Pushed to new GitHub repo.

