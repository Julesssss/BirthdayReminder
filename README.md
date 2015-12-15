# Birthday Reminder 2.0
App for Android devices which notifies users of upcoming birthdays.

![](http://julianrosser.website/images/app_screenshots/birthday15.png)![](http://julianrosser.website/images/app_screenshots/birthday16.png)

TODO
- Delay to notification, when to remind?? For each?
- Save when we add, edit or delete!
- Settings Fragment: sorting, notification settings, test notification
- Deep linking & Analytics
- Material Icons & notification icon size?
- Add icons to ItemOptionsMenu

- Save activityContext in MainActivity's onCreate reference with getActivity.mContext.
- OnSavedInstance - Method needed to save fragment?
- SQL database instead of JSON ??? necessary? Will take effort, perhaps as backup
- TypeFace-Light for api < 16
- 2 birthdays on same day?
- App launcher icon name

Next Update
- Widget
- Custom Notification

Bugs:
- Yesterdays date stays at top sometimes?
- Quickly click to open Fragments twice

Log
- 15/12 - Created NotificationBuilder class and added personalised notifications. Cake app icon. Vibrate pattern.
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

