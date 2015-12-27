# Birthday Reminder 2.0
App for Android devices which notifies users of upcoming birthdays.

![](http://julianrosser.website/images/app_screenshots/birthday180.png)![](http://julianrosser.website/images/app_screenshots/birthday184.png)

[<img src="http://i.imgur.com/aL8bBy5.png?1">](https://play.google.com/store/apps/details?id=website.julianrosser.birthdays)

TODO - Main:

- Translate last strings (Need Internet)
- Include Year
- Translated play store description and images  (Need Internet)
- Record screen-cast (Add, rotate, edit, delete, reminder)

Bugs:
- Crash when click noti while on settings activity and go back to 1st (Unlikely to happen to user)
- Quickly click to open Fragments twice
- SettingsActivity keeps calling Service. callback or some solution to ToManyCalls problem

Next Update?
- Widget
- Alternative Colour
- Import Birthdays from contacts
- Change Name to Simple Birthday reminder?
- Pref Item Background//////////////////////////////////////
- Data Backup
- RecyclerFragment onSavedInstance (array, adapter)
- Undo birthday delete?
- Pref category not bold -api19
- Tablet fragment layout & window sizes!
- Roboto font < api 16
- 2 birthdays on same day?
- Fragments as bottom cards?
- Custom Notification
- Button Merge to alertDialog?
- SQL database instead of JSON ??? necessary? Will take effort, perhaps as backup
- FAB (+ toolbar?) hide on scroll, but show on data changed


Log
- 27/12 - Added 2 weeks before option.
- 26/12 - Translated screenshots
- 25/12 - 2.0.1 update: Removed styled PrefDialog from >API19
- 24/12 - Analytics. App Indexing. >API16 Removed preference dialog background.
- 23/12 - Added edit animation logic. Refactored all text to Strings, sorted references. Translated pref & date strings. Translated help springs.
- 22/12 - RecycleView animations. debugging array position exceptions.
- 21/12 - Translated strings to 7 most used languages. Created HelpActivity. Finishing touches to layout. Replaced Toast with SnackBar.
- 20/12 - Small, normal and tablet layouts. Fixed bug where alarm wouldn't cancel when toggled. XLarge layout, final changes to list layout.
- 19/12 - Toolbar shadow, FAButton hide/show when scrolling. Finished Material styling. Toolbar & menu bg, preference bg & category styles.
- 18/12 - Replaced ActionBar with Toolbar. Material theme, styles & colours. Added reminder toggle option, icon & functionality.
          Somehow managed to delete build.gradle. CHAOS. Noticed the file was removed from last commit.
- 17/12 - Birthdays now saved and notification alarms set when data changes. FloatingActionButton, Material icon & colours.
          Cancel notification when deleted. Added preferences (Reminder time, vibrate?, sound?, test noti, sorting). New screenshots.
- 16/12 - Built custom Adapter so ItemOptionFrag shows Icons. Save/recall Fragment references onRotate.
          Dialogs theme & size. Replaced unnecessary Activity context refs with AppContext. Refactored Arrays.
          Created birthday_notification.mp3 & added to notification. Settings theme & Time preference
- 15/12 - Created NotificationBuilder class and added personalised notifications. Cake app icon. Vibrate pattern.
        - SettingActivity & Fragment. Works with days before remind setting. Reduced DatePicker padding.
- 14/12 - Auto sorting. Refactored LoadBirthdaysTask.
- 12/12 - Service is launched on boot. Created BootNotificationReceiver and SetAlarmService.
- 10/12 - Prevented context leaks. Built date/day remaining helper methods. RobotoLight font.
Custom date bg drawables with elevation. Now editing birthday instead of replacing. Updated screenshots.
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

